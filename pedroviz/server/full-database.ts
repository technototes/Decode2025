import {
  ErrorOr,
  isDefined,
  isError,
  isUndefined,
  MakeError,
} from '@freik/typechk';

import { GetTeamPaths } from './getpaths';
import { anyItems, MakePathChainFile } from './PathChainLoader';
import {
  Path,
  PathChainClass,
  PathDatabase,
  PathDBKey,
  Team,
  TeamPaths,
} from './types';
import { getProjectFilePath } from './utility';

const teampaths: Map<Team, Path[]> = new Map();
const database: PathDatabase = new Map();

export function makeKey(team: Team, path: Path): PathDBKey {
  return `${team}*${path}` as PathDBKey;
}

export function getTeamPath(dbKey: PathDBKey): [Team, Path] {
  const items = dbKey.split('*');
  if (items.length === 2) {
    return items as [Team, Path];
  }
  console.error('Invalid key provided: ', dbKey);
  return ['team', 'path'] as [Team, Path];
}

export function ForEachPathChainIndex(
  top: PathChainClass,
  funcStop: (pcc: PathChainClass) => true | unknown,
): void {
  const work = [top];
  while (work.length > 0) {
    const item = work.pop()!;
    if (funcStop(item) === true) {
      return;
    }
    work.push(...Object.values(item.children));
  }
}

async function GetPathChainIndex(
  team: string,
  file: string,
): Promise<ErrorOr<[string[], PathChainClass]>> {
  const filepath = getProjectFilePath(team, file);
  const pcc = await MakePathChainFile(filepath);
  if (isError(pcc)) {
    return pcc;
  }
  const list: string[] = [];
  ForEachPathChainIndex(pcc, (item) => list.push(item.name));
  return [list, pcc];
}

function RegisterPathChainIndex(
  team: Team,
  path: Path,
  classList: string[],
  pcc: PathChainClass,
): void {
  if (!anyItems(pcc)) {
    return;
  }
  console.log('Registering', team, path, classList, pcc.fullName);
  const paths = teampaths.get(team);
  if (isDefined(paths)) {
    paths.push(path);
  } else {
    teampaths.set(team, [path]);
  }
  database.set(makeKey(team, path), [classList, pcc]);
}

export async function PopulateDatabase() {
  const teamPaths: TeamPaths = await GetTeamPaths();
  for (const team of Object.keys(teamPaths) as Team[]) {
    for (const path of teamPaths[team]!) {
      const pci = await GetPathChainIndex(team, path);
      if (!isError(pci)) {
        RegisterPathChainIndex(team, path, pci[0], pci[1]);
      }
    }
  }
}

export function GetAllIndexContent(): [Team, Path, string[]][] {
  return [...database.entries()].map(([key, [classes, pcc]]) => {
    const [team, path] = getTeamPath(key);
    return [team, path, classes];
  });
}

export function GetDatabase(): PathDatabase {
  return database;
}

// Interfaces to the web server to talk to the web client:

export function WebGetPathChainClassList(
  team: Team,
  path: Path,
): ErrorOr<string[]> {
  const res = database.get(makeKey(team, path));
  if (isUndefined(res)) {
    return MakeError(`List: ${team}:${path} no Pedro pathing classes found`);
  }
  return res[0];
}

export function WebGetPathChainClassRoot(
  team: Team,
  path: Path,
): ErrorOr<PathChainClass> {
  const res = database.get(makeKey(team, path));
  if (isUndefined(res)) {
    return MakeError(`Root: ${team}:${path} no Pedro pathing classes found`);
  }
  return res[1];
}

/*
if (import.meta.main) {
  PopulateDatabase()
    .then(() => console.log('done'))
    .catch(console.error);
}
*/
