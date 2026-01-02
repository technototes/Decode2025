import { chkAnyOf, isString } from '@freik/typechk';
import {
  AnonymousBezier,
  AnonymousPose,
  chkPathChainFile,
  chkTeamPaths,
  ErrorOr,
  isError,
  makeError,
  PathChainFile,
  TeamPaths,
} from '../../server/types';
import { MakeMappedIndex } from './IndexedFile';
import { fetchApi } from './Storage';
import { MappedIndex } from '../types';

export type ValidRes = ErrorOr<true>;
// Some of the logic seems a little odd, because I want the validation to fully
// run on everything so I'm avoiding any logical short-circuiting...

const colorLookup: Map<string, number> = new Map();
let colorCount = 0;

export function getColorFor(
  item: string | AnonymousBezier | AnonymousPose,
): number {
  if (isString(item)) {
    if (!colorLookup.has(item)) {
      colorLookup.set(item, colorCount++);
    }
    return colorLookup.get(item);
  }
  return getColorFor(JSON.stringify(item));
}

export async function GetPaths(): Promise<TeamPaths> {
  const teamFileList = await fetchApi('getpaths', chkTeamPaths, {});
  for (const i of Object.keys(teamFileList)) {
    teamFileList[i].sort();
  }
  return teamFileList;
}

// last loaded file, I guess?
const lastLoadedIndexFile = {
  team: '',
  file: '',
  data: null as null | MappedIndex,
};
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
    chkAnyOf(chkPathChainFile, isString),
    'Invalid PathChainFile loaded from server',
  );
  if (isString(pcf)) {
    return makeError(pcf);
  }
  const indexFile = MakeMappedIndex(pcf);
  if (isError(indexFile)) {
    return makeError(
      indexFile,
      `Loaded file ${team}/${file} has dangling references.`,
    );
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
  data: PathChainFile,
): Promise<undefined | string> {
  // NYI on the server, either :D
  return 'NYI';
}
