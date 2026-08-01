import { isError, Pickle } from '@freik/typechk';

import { Path, Team } from '../IpcTypes';
import {
  GetDatabase,
  WebGetParsedClassList,
  WebGetParsedClassRoot,
} from './full-database';

// Send the list of TeamPaths to the client

export async function LoadClassList(
  team: string,
  path: string,
): Promise<Response> {
  const res = WebGetParsedClassList(team as Team, path as Path);
  if (isError(res)) {
    return Response.json({ error: res.errors().join('\n') });
  }
  return Response.json(res);
}

export async function LoadPath(team: string, path: string): Promise<Response> {
  const pc = WebGetParsedClassRoot(team as Team, path as Path);
  if (isError(pc)) {
    return Response.json({ error: pc.errors().join('\n') });
  }
  return Response.json(JSON.parse(Pickle(pc)));
}

export async function LoadDatabase(): Promise<Response> {
  return Response.json(JSON.parse(Pickle(GetDatabase())));
}
