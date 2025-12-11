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
import { fetchApi } from './Storage';
import { Point } from './types';

export let namedValues: Map<string, NamedValue> = new Map();
export let namedPoses: Map<string, NamedPose> = new Map();
export let namedBeziers: Map<string, NamedBezier> = new Map();
export let namedPathChains: Map<string, NamedPathChain> = new Map();

export type ValidRes = ErrorOr<true>;
// Some of the logic seems a little odd, because I want the validation to fully
// run on everything so I'm avoiding any logical short-circuiting...

function foundValueRef(vr: ValueRef, id: string): ValidRes {
  if (isRef(vr)) {
    if (!namedValues.has(vr) && !namedPoses.has(vr)) {
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
    return namedPoses.has(pr)
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
    return namedBeziers.has(br)
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
  namedValues = new Map(pcf.values.map((nv) => [nv.name, nv]));
  namedPoses = new Map(pcf.poses.map((np) => [np.name, np]));
  namedBeziers = new Map(pcf.beziers.map((nb) => [nb.name, nb]));
  namedPathChains = new Map(pcf.pathChains.map((npc) => [npc.name, npc]));
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

export const EmptyPathChainFile: PathChainFile = {
  name: 'empty',
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
};

export async function LoadFile(
  team: string,
  file: string,
): Promise<PathChainFile> {
  const pcf = await fetchApi(
    `loadpath/${encodeURIComponent(team)}/${encodeURIComponent(file)}`,
    chkPathChainFile,
    EmptyPathChainFile,
  );
  RegisterFreshFile(pcf);
  if (validatePathChainFile(pcf) === true) {
    return pcf;
  } else {
    return EmptyPathChainFile;
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
  return numFromVal(isRef(vr) ? namedValues.get(vr).value : vr);
}

export function pointFromPose(pr: AnonymousPose): Point {
  return { x: getValue(pr.x), y: getValue(pr.y) };
}

export function getPose(pr: PoseRef): AnonymousPose {
  try {
    return isRef(pr) ? namedPoses.get(pr).pose : pr;
  } catch (e) {
    // console.error(`Invalid PoseRef ${pr}`);
    throw e;
  }
}

export function pointFromPoseRef(pr: PoseRef): [number, Point] {
  return [getColorFor(getPose(pr)), pointFromPose(getPose(pr))];
}

export function getBezier(br: BezierRef): AnonymousBezier {
  return isRef(br) ? namedBeziers.get(br).points : br;
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
