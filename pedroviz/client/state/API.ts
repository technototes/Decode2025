import { chkAnyOf, isString } from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousPose,
  chkPathChainClass,
  chkPathDatabase,
  ErrorOr,
  isError,
  makeError,
  Path,
  PathChainClass,
  PathDatabase,
  Team,
} from '../../server/types';
import { MappedIndex } from '../types';
import { MakeMappedIndex } from './IndexedFile';
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
  data: null as null | MappedIndex,
};
const indexedFiles: Map<[Team, Path], MappedIndex> = new Map();

export async function LoadAndIndexFile(
  team: string,
  file: string,
): Promise<ErrorOr<MappedIndex>> {
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
  const pcf = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkAnyOf(chkPathChainClass, isString),
    'Invalid PathChainFile loaded from server',
  );
  if (isString(pcf)) {
    return makeError(pcf);
  }
  const indexFile = await MakeMappedIndex(pcf, indexedFiles);
  if (isError(indexFile)) {
    return makeError(
      indexFile,
      `Loaded file ${team}/${file} has dangling references.`,
    );
  } else {
    // TODO: This shouldn't be manual. If you have dangling references, you should evaluate the other files...
    indexedFiles.set([team as Team, file as Path], indexFile);
  }
  lastLoadedIndexFile.data = indexFile;
  return indexFile;
}

export function UpdateIndexFile(team: string, file: string, data: MappedIndex) {
  lastLoadedIndexFile.team = team;
  lastLoadedIndexFile.file = file;
  lastLoadedIndexFile.data = data;
}

export async function SavePath(
  team: string,
  path: string,
  data: PathChainClass,
): Promise<undefined | string> {
  // NYI on the server, either :D
  return 'NYI';
}
