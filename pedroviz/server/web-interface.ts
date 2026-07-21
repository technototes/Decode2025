import { Pickle } from '@freik/typechk';

import {
  GetDatabase,
  WebGetPathChainClassList,
  WebGetPathChainClassRoot,
} from './full-database';
import { isError, Path, Team } from './types';

// Send the list of TeamPaths to the client

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
