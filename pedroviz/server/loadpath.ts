import { hasField, isString, isUndefined } from '@freik/typechk';

import { MakePathChainFile } from './PathChainLoader';
import { ErrorOr, isError, makeError, PathChainClass } from './types';
import { getProjectFilePath } from './utility';

export async function GetPathChainIndex(
  team: string,
  file: string,
): Promise<ErrorOr<[string[], PathChainClass]>> {
  const filepath = getProjectFilePath(team, file);
  const pcc = await MakePathChainFile(filepath);
  if (isError(pcc)) {
    return pcc;
  }
  const list = [];
  const work = [pcc];
  while (work.length > 0) {
    const item = work.pop();
    if (isUndefined(item)) {
      break;
    }
    list.push(item.name);
    work.push(...Object.values(item.children));
  }
  return [list, pcc];
}
