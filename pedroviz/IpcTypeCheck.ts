import { ParsedClass } from 'CodeTypes';
import {
  chkMultiMapOf,
  MakeMultiMap,
} from 'node_modules/@freik/containers/lib/esm';
import {
  chkMapOf,
  chkObjectOfExactType,
  isArrayOfString,
  isRecordOf,
  isString,
  typecheck,
} from '@freik/typechk';

import { chkParsedClass } from './CodeTypeCheck';
import { ClassKey, Path, PathDatabase, PathKey, Team } from './IpcTypes';

export function getPathKey(team: Team, path: Path): PathKey {
  return `${team}*${path}` as PathKey;
}

export function PathFromKey(pathKey: PathKey): Path {
  const elems = pathKey.split('*');
  return (elems.pop() || '') as Path;
}

export function chkPathKey(obj: unknown): obj is PathKey {
  if (!isString(obj)) {
    return false;
  }
  const pieces = obj.split('*');
  return pieces.length === 2;
}

export function getClassKey(pathKey: PathKey, className: string): ClassKey {
  return `${pathKey};${className}` as ClassKey;
}

export function ClassFromKey(classKey: ClassKey): string {
  const elems = classKey.split(';');
  return elems.pop() || '';
}

export function chkClassKey(obj: unknown): obj is ClassKey {
  if (!isString(obj)) {
    return false;
  }
  const pieces = obj.split(';');
  return pieces.length === 2 && chkPathKey(pieces[0]);
}

export const chkPathDatabase: typecheck<PathDatabase> = chkObjectOfExactType({
  TeamPaths: chkMultiMapOf(isString, chkPathKey),
  PathClasses: chkMultiMapOf(chkPathKey, chkClassKey),
  ParsedClasses: chkMapOf(chkClassKey, chkParsedClass),
});

export const EmptyPathDatahase: PathDatabase = Object.freeze({
  TeamPaths: MakeMultiMap<Team, PathKey>(),
  PathClasses: MakeMultiMap<PathKey, ClassKey>(),
  ParsedClasses: new Map<ClassKey, ParsedClass>(),
});
