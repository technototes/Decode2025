import { hasField, isString, isUndefined } from '@freik/typechk';

import { MakePathChainFile } from './PathChainLoader';
import { PathChainClass } from './types';
import { getProjectFilePath } from './utility';

export async function LoadPath(
  team: string,
  filename: string,
): Promise<Response> {
  const filePath = getProjectFilePath(team, filename);
  const paths = await loadPathChainsFromFile(filePath);
  if (isString(paths)) {
    return Response.json({ error: paths });
  }
  return Response.json(paths);
}

export async function loadPathChainsFromFile(
  filePath: string,
): Promise<PathChainClass | string> {
  return MakePathChainFile(filePath);
}

export async function LoadClassList(filepath: string): Promise<Response> {
  const pcc = await loadPathChainsFromFile(filepath);
  if (isString(pcc)) {
    return Response.json({ error: pcc });
  }
  const list = [];
  const work = [pcc];
  while (work.length > 0) {
    const item = work.pop();
    if (isUndefined(item)) {
      break;
    }
    list.push(item.name);
    work.push(...Object.values(pcc.children));
  }
  return Response.json(list);
}
