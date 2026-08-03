import { chkPathDatabase } from 'IpcTypeCheck';
import {
  isDefined,
  isError,
  Pickle,
  SafelyUnpickle,
  Unpickle,
} from '@freik/typechk';

import { Path, Team } from '../IpcTypes';
import {
  GetDatabase,
  ReplaceDatabase,
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

export async function SaveDatabase(flattenedDb: string): Promise<Response> {
  console.log('Saving DB');
  try {
    const db = SafelyUnpickle(flattenedDb, chkPathDatabase);
    if (isDefined(db)) {
      ReplaceDatabase(db);
    } else {
      return Response.json({ error: 'Failed to unpickle database' });
    }
  } catch (err) {
    return Response.json({ error: 'Crashed while unpickle database' });
  }
  return Response.json({ success: 1 });
}
