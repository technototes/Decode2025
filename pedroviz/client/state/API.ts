import {
  chkAnyOf,
  ErrorOr,
  isError,
  isString,
  MakeError,
} from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousPose,
  chkParsedClass,
  chkPathDatabase,
  ParsedClass,
  Path,
  PathDatabase,
  Team,
} from '../../server/types';
import { FileIndex, NameLookup } from '../types';
import { GetNameLookup, MakeFileIndex, ValidateIndex } from './IndexedFile';
import { fetchApi } from './Storage';

export type ValidRes = ErrorOr<true>;
// Some of the logic seems a little odd, because I want the validation to fully
// run on everything so I'm avoiding any logical short-circuiting...

const colorLookup: Map<string, number> = new Map();
let colorCount = 0;

// This provides a consistent color for a given curve/pose
export function getColorFor(
  item: string | AnonymousBezier | AnonymousPose,
): number {
  if (isString(item)) {
    if (!colorLookup.has(item)) {
      colorLookup.set(item, colorCount++);
    }
    return colorLookup.get(item)!;
  }
  return getColorFor(JSON.stringify(item));
}

// Get the entire Database from the server
export async function GetFullDb(): Promise<PathDatabase> {
  return await fetchApi('db', chkPathDatabase, new Map());
}

// last loaded file, I guess?
const lastLoadedIndexFile = {
  team: '',
  file: '',
  data: null as null | FileIndex,
};
const indexedFiles: Map<[Team, Path], FileIndex> = new Map();

export async function LoadAndIndexFile(
  team: string,
  file: string,
): Promise<ErrorOr<FileIndex>> {
  if (
    lastLoadedIndexFile.team === team &&
    lastLoadedIndexFile.file === file &&
    lastLoadedIndexFile.data !== null
  ) {
    console.log('using cached file for', team, file);
    return lastLoadedIndexFile.data;
  }
  lastLoadedIndexFile.team = team;
  lastLoadedIndexFile.file = file;
  lastLoadedIndexFile.data = null;
  const pc = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkAnyOf(chkParsedClass, isString),
    'Invalid ParsedClass loaded from server',
  );
  if (isString(pc)) {
    return MakeError(pc);
  }
  const indexFile = await MakeFileIndex(pc);
  const lookup: NameLookup = GetNameLookup();
  lookup.registerIndex(indexFile);
  const validate = ValidateIndex(indexFile, lookup, pc);
  if (isError(validate)) {
    return MakeError(
      validate,
      `Loaded file ${team}/${file} has dangling references.`,
    );
  } else {
    // TODO: This shouldn't be manual. If you have dangling references, you should evaluate the other files...
    indexedFiles.set([team as Team, file as Path], indexFile);
  }
  lastLoadedIndexFile.data = indexFile;
  return indexFile;
}

export function UpdateIndexFile(team: string, file: string, data: FileIndex) {
  lastLoadedIndexFile.team = team;
  lastLoadedIndexFile.file = file;
  lastLoadedIndexFile.data = data;
}

export async function SavePath(
  team: string,
  path: string,
  data: ParsedClass,
): Promise<undefined | string> {
  // NYI on the server, either :D
  return 'NYI';
}
