import { expect, test } from 'bun:test';
import path from 'path';
import {
  GetPathFileNames,
  getPathFiles,
  getRelativeRepoRoot,
  getTeamDirectories,
} from '../getpaths';

function getTestRepoPath(): string {
  return path.resolve(__dirname, 'test-repo-root');
}

test('getRelativeRepoRoot finds the repo root', async () => {
  const currentPath = path.resolve(__dirname, '../../..');
  const repoRoot = await getRelativeRepoRoot(currentPath);
  expect(repoRoot).toBe(currentPath);
});

test('getRelativeRepoRoot throws if no repo root found', async () => {
  const invalidPath = path.resolve(__dirname, '../../../../nonexistent/path');
  await expect(getRelativeRepoRoot(invalidPath)).rejects.toThrow(
    'Could not find repository root',
  );
});

test('getRelativeRoot finds the test repository root', async () => {
  const testRepoPath = getTestRepoPath();
  const repoRoot = await getRelativeRepoRoot(
    path.join(testRepoPath, 'some', 'nested', 'directory'),
  );
  expect(repoRoot).toBe(testRepoPath);
});

test('getTeamDirectories finds team directories', async () => {
  const repoRoot = await getTestRepoPath();
  const teamDirs = await getTeamDirectories(repoRoot);
  expect(teamDirs).toContain('TeamA');
  expect(teamDirs).toContain('TeamB');
});

test('getPathFiles finds path files', async () => {
  const repoRoot = await getTestRepoPath();
  const pathFiles = await getPathFiles(repoRoot, 'TeamA');
  expect(pathFiles.length).toBe(2);
  expect(pathFiles).toContain('TeamTestPaths.java');
  expect(pathFiles).toContain(path.join('subdir', 'PathsLiveHere.java'));
});

test('getPathFiles finds no path files', async () => {
  const repoRoot = await getTestRepoPath();
  const pathFiles = await getPathFiles(repoRoot, 'TeamB');
  expect(pathFiles).toEqual([]);
});

test('raw endpoint invocation', async () => {
  const res: Response = await GetPathFileNames();
  expect(res).toHaveProperty('ok', true);
  expect(res).toHaveProperty('status', 200);
  expect(res).toHaveProperty('headers');
  const data = await res.json();
  expect(data).toBeDefined();
});
