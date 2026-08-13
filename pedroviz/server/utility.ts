import { promises as fsp } from 'node:fs';
import path from 'node:path';

export const firstFtcSrc = path.join(
  'src',
  'main',
  'java',
  'org',
  'firstinspires',
  'ftc',
);

export function getProjectFilePath(team: string, filename: string): string {
  return path.join('..', team, firstFtcSrc, team.toLocaleLowerCase(), filename);
}

export async function isDirectory(path: string): Promise<boolean> {
  try {
    const stats = await fsp.stat(path);
    return stats.isDirectory();
  } catch {
    // An error is thrown if the path doesn't exist
    return false;
  }
}
