import { isString, Pickle } from '@freik/typechk';

import {
  GetDatabase,
  WebGetPathChainClassList,
  WebGetPathChainClassRoot,
  WebGetTeamPaths,
} from './full-database';
import { GetTeamPaths } from './getpaths';
import { isError, Path, Team } from './types';

// Send the list of TeamPaths to the client

export async function GetPathFileNames(): Promise<Response> {
  // First, get the path to the root of the repository:
  // console.log('Found the following paths:', filePaths);
  return Response.json(WebGetTeamPaths());
}
export async function LoadClassList(
  team: string,
  path: string,
): Promise<Response> {
  const res = WebGetPathChainClassList(team as Team, path as Path);
  if (isError(res)) {
    return Response.json({ error: res.errors().join('\n') });
  }
  return Response.json(res);
}
export async function LoadPath(team: string, path: string): Promise<Response> {
  const pcc = WebGetPathChainClassRoot(team as Team, path as Path);
  if (isError(pcc)) {
    return Response.json({ error: pcc.errors().join('\n') });
  }
  return Response.json(JSON.parse(Pickle(pcc)));
}

export async function LoadDatabase(): Promise<Response> {
  return Response.json(JSON.parse(Pickle(GetDatabase())));
}
