import { expect /* beforeAll, afterAll */, test } from 'bun:test';
import fs from 'fs';
import path from 'path';
import { firstFtcSrc, getProjectFilePath, isDirectory } from '../utility';

test('getProjectFilePath simple test', () => {
  expect(getProjectFilePath('TestTeam', 'FileName.java')).toBe(
    path.join('TestTeam', firstFtcSrc, 'testteam', 'FileName.java'),
  );
});

test('isDirectory testing', async () => {
  expect(await isDirectory(__dirname)).toBe(true);
  expect(await isDirectory(path.join(__dirname, 'nonexistent_directory'))).toBe(
    false,
  );
  expect(fs.existsSync(__filename)).toBe(true);
  expect(await isDirectory(__filename)).toBe(false);
});
