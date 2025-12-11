import fs, { promises as fsp } from 'node:fs';
import path from 'node:path';
import { TeamPaths } from './types';
import { firstFtcSrc, isDirectory } from './utility';

// Send the list of TeamPaths to the client
export async function GetPathFileNames(): Promise<Response> {
  // First, get the path to the root of the repository:
  const repoRoot = await getRelativeRepoRoot(
    Bun.fileURLToPath(new URL('.', import.meta.url)),
  );
  // Get the list of all team code roots
  const teamDirs = await getTeamDirectories(repoRoot);
  // Next, look for paths in each team directory
  const filePaths: TeamPaths = {};
  for (const teamName of teamDirs) {
    filePaths[teamName] = await getPathFiles(repoRoot, teamName);
  }
  // console.log('Found the following paths:', filePaths);
  return Response.json(filePaths);
}

const pathNameMatch = /Path[^\/\\]*\.java$/;

// Find all the files in the team directory that look like good Path files
export async function getPathFiles(
  repoRoot: string,
  teamName: string,
): Promise<string[]> {
  const teamDir = path.join(
    repoRoot,
    teamName,
    firstFtcSrc,
    teamName.toLocaleLowerCase(),
  );
  const pathFiles: string[] = [];
  // A worklist of directories to check for PedroPath-containing java files
  const pathsToCheck: string[] = [teamDir];
  while (pathsToCheck.length > 0) {
    const curDir = pathsToCheck.pop()!;
    const entries = await fsp.readdir(curDir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(curDir, entry.name);
      if (entry.isDirectory()) {
        pathsToCheck.push(fullPath);
      } else if (await isPathFile(entry)) {
        pathFiles.push(path.relative(teamDir, fullPath));
      }
    }
  }
  return pathFiles;
}

// The imports we're looking for in a Path*.java file:
const imports = [
  /^\s*import\s+com\.pedropathing\.follower\.Follower\s*;/,
  /^\s*import\s+com\.pedropathing\.geometry\.Bezier[A-Za-z]+\s*;/,
  /^\s*import\s+com\.pedropathing\.geometry\.Pose\s*;/,
  /^\s*import\s+com\.pedropathing\.paths\.PathChain\s*;/,
];

async function isPathFile(entry: fs.Dirent): Promise<boolean> {
  if (!entry.isFile() || !pathNameMatch.test(entry.name)) {
    return false;
  }
  const fileContent = (
    await fsp.readFile(path.join(entry.parentPath, entry.name), 'utf-8')
  ).split('\n');
  const matches = fileContent.filter((line) => {
    for (const imp of imports) {
      if (imp.test(line.trim())) {
        return true;
      }
    }
    return false;
  });
  // console.log(`File ${filePath} has ${matches.length} relevant import lines.`, matches);
  return matches.length >= 4;
}

export async function getRelativeRepoRoot(
  currentPath: string,
): Promise<string> {
  let prevPath = '';
  while (currentPath != prevPath) {
    if (
      (await fsp.exists(path.join(currentPath, 'settings.gradle'))) &&
      (await fsp.exists(path.join(currentPath, 'build.gradle'))) &&
      (await fsp.exists(path.join(currentPath, 'FtcRobotController'))) &&
      (await isDirectory(path.join(currentPath, 'FtcRobotController')))
    ) {
      return currentPath;
    }
    prevPath = currentPath;
    currentPath = path.dirname(currentPath);
  }
  throw new Error('Could not find repository root');
}

export async function getTeamDirectories(repoRoot: string): Promise<string[]> {
  const entries = await fsp.readdir(`${repoRoot}`, { withFileTypes: true });
  const teamDirs = entries
    .filter((dir) => isTeamDirectory(repoRoot, dir))
    .map((dir) => dir.name);
  return teamDirs;
}

// A directory is a team directory if:
// - It is a directory
// - Its name does not start with a .
// - Its name is not "FtcRobotController"
// - It contains a 'build.gradle' file
// - It has a 'src/main/java/org/firstinspires/ftc/<team-name>' subdirectory
// - It is referred to in the settings.gradle file at the repo root (NYI)
function isTeamDirectory(repoRoot: string, dir: fs.Dirent): boolean {
  if (!dir.isDirectory()) {
    return false;
  }
  const name = dir.name;
  if (name[0] === '.') {
    return false;
  }
  if (name === 'FtcRobotController') {
    return false;
  }
  // Check for the presence of a 'build.gradle' file
  const buildGradlePath = path.join(repoRoot, dir.name, 'build.gradle');
  if (!fs.existsSync(buildGradlePath)) {
    return false;
  }
  // Check for the presence of the 'src/main/java/org/firstinspires/ftc/<team-name>' subdirectory
  const teamSrcPath = path.join(
    repoRoot,
    dir.name,
    firstFtcSrc,
    dir.name.toLocaleLowerCase(),
  );
  if (!fs.existsSync(teamSrcPath) || !isDirectory(teamSrcPath)) {
    return false;
  }
  // TODO: Ensure that the directory is included in the settings.gradle file
  /*
  const settingsGradlePath = path.join(repoRoot, 'settings.gradle');
  if (!fs.existsSync(settingsGradlePath)) {
    return false;
  }
  const settingsGradleContent = fs.readFileSync(settingsGradlePath, 'utf-8');
  if (!settingsGradleContent.includes(dir.name)) {
    return false;
  }
  */
  return true;
}
