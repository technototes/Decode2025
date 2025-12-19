import { chkAnyOf, isString } from '@freik/typechk';
import {
  accError,
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierRef,
  chkConstantHeading,
  chkInterpolatedHeading,
  chkPathChainFile,
  chkRadiansRef,
  chkTeamPaths,
  ErrorOr,
  HeadingRef,
  HeadingType,
  isError,
  isRef,
  makeError,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  PathChainFile,
  PoseRef,
  TeamPaths,
  ValueRef,
} from '../../server/types';
import { MakeIndexedFile } from './IndexedFile';
import { fetchApi } from './Storage';
import {
  AnonymousPathChain,
  IndexedFile,
  IndexedPCF,
  IndexedPCFile,
  Point,
} from './types';

let ipcf: IndexedPCF = {
  name: 'empty',
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  namedValues: new Map<string, number>(),
  namedPoses: new Map<string, number>(),
  namedBeziers: new Map<string, number>(),
  namedPathChains: new Map<string, number>(),
};

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

export const EmptyPathChainFile: IndexedPCF = {
  name: '',
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  namedValues: new Map(),
  namedPoses: new Map(),
  namedBeziers: new Map(),
  namedPathChains: new Map(),
};

// last loaded file, I guess?
const lastLoadedFile = { team: '', file: '', data: null as null | IndexedFile };
export async function LoadFile(
  team: string,
  file: string,
): Promise<ErrorOr<IndexedFile>> {
  if (
    lastLoadedFile.team === team &&
    lastLoadedFile.file === file &&
    lastLoadedFile.data !== null
  ) {
    console.log('using cached file for', team, file);
    return lastLoadedFile.data;
  }
  lastLoadedFile.team = team;
  lastLoadedFile.file = file;
  lastLoadedFile.data = null;
  const pcf = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkAnyOf(chkPathChainFile, isString),
    "Invalid PathChainFile loaded from server",
  );
  if (isString(pcf)) {
    return makeError(pcf);
  }
  const indexFile = MakeIndexedFile(pcf);
  if (isError(indexFile)) {
    return makeError(`Loaded file ${team}/${file} has dangling references.`);
  }
  lastLoadedFile.data = indexFile;
  return indexFile;
}

export async function SavePath(
  team: string,
  path: string,
  data: PathChainFile,
): Promise<undefined | string> {
  // NYI on the server, either :D
  return 'NYI';
}
