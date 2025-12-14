import { isString } from '@freik/typechk';
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

function foundValueRef(vr: ValueRef, id: string): ValidRes {
  if (isRef(vr)) {
    if (!ipcf.namedValues.has(vr) && !ipcf.namedPoses.has(vr)) {
      return makeError(
        `${id}'s "${vr}" value reference appears to be undefined.`,
      );
    }
  }
  return true;
}

function foundHeadingRef(hr: HeadingRef, id: string): ValidRes {
  if (chkRadiansRef(hr)) {
    return foundValueRef(hr.radians, `${id}'s Radians ref`);
  }
  return foundValueRef(hr, id);
}

function noDanglingRefsOnPose(pose: AnonymousPose, id: string): ValidRes {
  let res: ValidRes = true;
  if (pose.heading) {
    res = foundHeadingRef(pose.heading, `${id}'s heading`);
  }
  res = accError(foundValueRef(pose.x, `${id}'s x coordinate`), res);
  return accError(foundValueRef(pose.y, `${id}'s y coordinate`), res);
}

function noDanglingRefsOnPoseRef(pr: PoseRef, id: string): ValidRes {
  if (isRef(pr)) {
    return ipcf.namedPoses.has(pr)
      ? true
      : makeError(`${id}'s "${pr}" pose reference appears to be undefined`);
  }
  return noDanglingRefsOnPose(pr, id);
}

function noDanglingRefsOnBezier(curve: AnonymousBezier, id: string): ValidRes {
  let res: ValidRes = true;
  curve.points.forEach((pr, index) => {
    res = accError(
      noDanglingRefsOnPoseRef(pr, `${id}'s element ${index}`),
      res,
    );
  });
  if (curve.type === 'line' && curve.points.length !== 2) {
    return accError(res, makeError(`${id}'s line doesn't have 2 points`));
  } else if (curve.type === 'curve' && curve.points.length < 2) {
    return accError(res, makeError(`${id}'s line doesn't have enough points`));
  }
  return res;
}

function noDanglingRefsOnBezierRef(br, id: string): ValidRes {
  if (isRef(br)) {
    return ipcf.namedBeziers.has(br)
      ? true
      : makeError(`${id}'s bezier reference appears to be undefined`);
  }
  return noDanglingRefsOnBezier(br, id);
}
function noDanglingRefsOnChain(
  brs: BezierRef[],
  heading: HeadingType,
  id: string,
): ValidRes {
  let res: ValidRes = true;
  if (chkConstantHeading(heading)) {
    res = foundHeadingRef(heading.heading, `${id}'s constant heading ref`);
  } else if (chkInterpolatedHeading(heading)) {
    res = foundHeadingRef(heading.headings[0], `${id}'s start heading ref`);
    res = accError(
      foundHeadingRef(heading.headings[1], `${id}'s end heading ref`),
      res,
    );
  }
  brs.forEach((br, index) => {
    res = accError(
      noDanglingRefsOnBezierRef(br, `${id}'s path element ${index}`),
      res,
    );
  });
  return res;
}

export function RegisterFreshFile(pcf: PathChainFile): void {
  ipcf.name = pcf.name;
  ipcf.values = pcf.values;
  ipcf.poses = pcf.poses;
  ipcf.beziers = pcf.beziers;
  ipcf.pathChains = pcf.pathChains;
  ipcf.namedValues = new Map(pcf.values.map((nv, i) => [nv.name, i]));
  ipcf.namedPoses = new Map(pcf.poses.map((np, i) => [np.name, i]));
  ipcf.namedBeziers = new Map(pcf.beziers.map((nb, i) => [nb.name, i]));
  ipcf.namedPathChains = new Map(pcf.pathChains.map((npc, i) => [npc.name, i]));
}

export function IndexPathChainFile(pcf: PathChainFile): IndexedPCFile {
  const values = new Map<string, AnonymousValue>(
    pcf.values.map((nv) => [nv.name, nv.value]),
  );
  const poses = new Map<string, AnonymousPose>(
    pcf.poses.map((np) => [np.name, np.pose]),
  );
  const beziers = new Map<string, AnonymousBezier>(
    pcf.beziers.map((nb) => [nb.name, nb.points]),
  );
  const pathChains = new Map<string, AnonymousPathChain>(
    pcf.pathChains.map((npc) => [
      npc.name,
      { paths: npc.paths, heading: npc.heading },
    ]),
  );
  return { values, poses, beziers, pathChains };
}

export function validatePathChainFile(pcf: PathChainFile): ErrorOr<true> {
  let good: ValidRes = true;
  pcf.poses.forEach((pr) => {
    good = accError(noDanglingRefsOnPose(pr.pose, pr.name), good);
  });
  pcf.beziers.forEach((br, index) => {
    good = accError(
      noDanglingRefsOnBezier(br.points, `${br.name}'s element ${index}`),
      good,
    );
  });
  pcf.pathChains.forEach((npc) => {
    good = accError(
      noDanglingRefsOnChain(npc.paths, npc.heading, npc.name),
      good,
    );
  });
  return isError(good) ? good : true;
}

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
let curTeam = '';
let curFile = '';

/*
export async function LoadFile(
  team: string,
  file: string,
): Promise<IndexedPCF> {
  // We cache a single file, because it's likely to be reloaded often
  if (curTeam === team && curFile === file) {
    return ipcf;
  }
  curTeam = team;
  curFile = file;
  const pcf = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkPathChainFile,
    { name: '', values: [], poses: [], beziers: [], pathChains: [] },
  );
  RegisterFreshFile(pcf);
  if (validatePathChainFile(pcf) === true) {
    return ipcf;
  } else {
    return EmptyPathChainFile;
  }
}
*/

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
    console.log('using cachewd file for', team, file);
    return lastLoadedFile.data;
  }
  lastLoadedFile.team = team;
  lastLoadedFile.file = file;
  lastLoadedFile.data = null;
  const pcf = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkPathChainFile,
    EmptyPathChainFile,
  );
  console.log('loaded file from server for', team, file);
  console.log(pcf);
  const indexFile = MakeIndexedFile(pcf);
  if (isError(indexFile)) {
    return makeError(`Loaded file ${team}/${file} has dangling references.`);
  }
  lastLoadedFile.data = indexFile;
  return indexFile;
}

export function SetNamedValue(nv: NamedValue): void {
  const idx = ipcf.namedValues.get(nv.name);
  if (idx !== undefined) {
    ipcf.values[idx] = nv;
  } else {
    ipcf.namedValues.set(nv.name, ipcf.values.length);
    ipcf.values.push(nv);
  }
}

export function SetNamedPose(np: NamedPose): void {
  const idx = ipcf.namedPoses.get(np.name);
  if (idx !== undefined) {
    ipcf.poses[idx] = np;
  } else {
    ipcf.namedPoses.set(np.name, ipcf.poses.length);
    ipcf.poses.push(np);
  }
}

export function SetNamedBezier(nb: NamedBezier): void {
  const idx = ipcf.namedBeziers.get(nb.name);
  if (idx !== undefined) {
    ipcf.beziers[idx] = nb;
  } else {
    ipcf.namedBeziers.set(nb.name, ipcf.beziers.length);
    ipcf.beziers.push(nb);
  }
}

export function SetNamedPathChain(npc: NamedPathChain): void {
  const idx = ipcf.namedPathChains.get(npc.name);
  if (idx !== undefined) {
    ipcf.pathChains[idx] = npc;
  } else {
    ipcf.namedPathChains.set(npc.name, ipcf.pathChains.length);
    ipcf.pathChains.push(npc);
  }
}

export async function SavePath(
  team: string,
  path: string,
  data: PathChainFile,
): Promise<undefined | string> {
  // NYI on the server, either :D
  return 'NYI';
}

// Evaluation from the parsed code representation:
export function numFromVal(av: AnonymousValue): number {
  switch (av.type) {
    case 'double':
    case 'int':
      return av.value;
    case 'radians':
      return (Math.PI * av.value) / 180.0;
  }
}

export function getValue(vr: ValueRef): number {
  return numFromVal(
    isRef(vr) ? ipcf.values[ipcf.namedValues.get(vr)].value : vr,
  );
}

export function pointFromPose(pr: AnonymousPose): Point {
  return { x: getValue(pr.x), y: getValue(pr.y) };
}

export function getPose(pr: PoseRef): AnonymousPose {
  try {
    return isRef(pr) ? ipcf.poses[ipcf.namedPoses.get(pr)].pose : pr;
  } catch (e) {
    // console.error(`Invalid PoseRef ${pr}`);
    throw e;
  }
}

export function pointFromPoseRef(pr: PoseRef): [number, Point] {
  return [getColorFor(getPose(pr)), pointFromPose(getPose(pr))];
}

export function getBezier(br: BezierRef): AnonymousBezier {
  return isRef(br) ? ipcf.beziers[ipcf.namedBeziers.get(br)].points : br;
}

export function getBezierPoints(br: BezierRef): [number, [number, Point][]] {
  const ab = getBezier(br);
  return [getColorFor(ab), ab.points.map(pointFromPoseRef)];
}

export function getValueFromHeaderRef(hr: HeadingRef): number {
  if (isRef(hr)) {
    return getValue(hr);
  } else if (chkRadiansRef(hr)) {
    return (Math.PI * getValue(hr.radians)) / 180.0;
  } else {
    return getValue(hr);
  }
}
