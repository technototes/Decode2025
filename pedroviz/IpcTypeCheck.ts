import {
  chkMapOf,
  chkTupleOf,
  isArrayOfString,
  isRecordOf,
  isString,
  typecheck,
} from '@freik/typechk';

import { chkParsedClass } from './CodeTypeCheck';
import { PathDatabase, PathDBKey, PathDBValue, TeamPaths } from './IpcTypes';

export function chkPathDBKey(obj: unknown): obj is PathDBKey {
  if (!isString(obj)) {
    return false;
  }
  const pieces = obj.split('*');
  return pieces.length === 2;
}
export const chkPathDBValue: typecheck<PathDBValue> = chkTupleOf(
  isArrayOfString,
  chkParsedClass,
);
export const chkPathDatabase: typecheck<PathDatabase> = chkMapOf(
  chkPathDBKey,
  chkPathDBValue,
);

export function chkTeamPaths(t: unknown): t is TeamPaths {
  return isRecordOf(t, isString, isArrayOfString);
}
