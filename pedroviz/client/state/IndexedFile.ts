import {
  AccError,
  ErrorOr,
  isDefined,
  isError,
  isUndefined,
  MakeError,
} from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  EmptyPathChainClass,
  HeadingRef,
  isAnonymousValue,
  isConstantFacing,
  isDoubleValue,
  isIntValue,
  isLinearFacing,
  isRadiansRef,
  isRef,
  isTangentFacing,
  PathChainClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { AnonymousPathChain, FileIndex, NameLookup, Point } from '../types';
import { ValidRes } from './API';

export function MakeFileIndex(pcf: PathChainClass): FileIndex {
  const namedValues = new Map<ValueName, ValueRef | RadiansRef>(
    pcf.values.map((nv) => [nv.name, nv.value]),
  );
  const namedPoses = new Map<PoseName, PoseRef>(
    pcf.poses.map((np) => [np.name, np.pose]),
  );
  const namedBeziers = new Map<BezierName, BezierRef>(
    pcf.beziers.map((nb) => [nb.name, nb.points]),
  );
  const namedPathChains = new Map<PathChainName, AnonymousPathChain>(
    pcf.pathChains.map((npc) => [
      npc.name,
      { paths: npc.paths, heading: npc.pathHeading },
    ]),
  );
  return {
    container: pcf,
    namedValues,
    namedBeziers,
    namedPoses,
    namedPathChains,
  };
}

// Make a thing that can accumulate indexes (*can* in the *future*)
function MakeNameLookup(): NameLookup {
  let indexMap: FileIndex | null = null;
  const registerIndex = (index: FileIndex) => {
    indexMap = index;
  };
  const findValue = (
    val: ValueName,
    context: PathChainClass,
  ): ValueRef | RadiansRef | undefined => {
    return indexMap ? indexMap.namedValues.get(val) : undefined;
  };
  const findPose = (
    val: PoseName,
    context: PathChainClass,
  ): PoseRef | undefined => {
    return indexMap ? indexMap.namedPoses.get(val) : undefined;
  };
  const findBezier = (
    val: BezierName,
    context: PathChainClass,
  ): BezierRef | undefined => {
    return indexMap ? indexMap.namedBeziers.get(val) : undefined;
  };
  const findPath = (
    val: PathChainName,
    context: PathChainClass,
  ): AnonymousPathChain | undefined => {
    return indexMap ? indexMap.namedPathChains.get(val) : undefined;
  };
  return { registerIndex, findBezier, findPath, findPose, findValue };
}

const nameLookup = MakeNameLookup();
export function GetNameLookup(): NameLookup {
  return nameLookup;
}

export function ValidateIndex(
  fileIndex: FileIndex,
  lkup: NameLookup,
  context: PathChainClass,
): ErrorOr<true> {
  function checkValueRef(vr: ValueRef, id: string): ValidRes {
    if (isRef(vr)) {
      if (!lkup.findValue(vr, context)) {
        // TODO: This should trigger cross file lookup
        return MakeError(
          `${id}'s "${vr}" value reference appears to be undefined.`,
        );
      }
    }
    return true;
  }

  function checkHeadingRef(hr: HeadingRef, id: string): ValidRes {
    if (isRadiansRef(hr)) {
      hr = hr.radians;
    }
    const valueRefCheck = checkValueRef(hr as ValueRef, id);
    if (valueRefCheck !== true) {
      // A heading ref could be a pose ref instead of a value ref
      // TODO: Maybe keep track of this stuff somehow?
      return checkPoseRef(hr as PoseName, id);
    }
    return true;
  }

  function checkAnonymousPose(pose: AnonymousPose, id: string): ValidRes {
    let res: ValidRes = true;
    if (pose.heading) {
      res = checkHeadingRef(pose.heading, `${id}'s heading`);
    }
    res = AccError(checkValueRef(pose.x, `${id}'s x coordinate`), res);
    return AccError(checkValueRef(pose.y, `${id}'s y coordinate`), res);
  }

  function checkPoseRef(pr: PoseRef, id: string): ValidRes {
    if (isRef(pr)) {
      return lkup.findPose(pr, context)
        ? true
        : // TODO: This should trigger cross file lookup
          MakeError(`${id}'s "${pr}" pose reference appears to be undefined`);
    }
    return checkAnonymousPose(pr, id);
  }

  function checkAnonymousBezier(curve: AnonymousBezier, id: string): ValidRes {
    let res: ValidRes = true;
    curve.points.forEach((pr, index) => {
      res = AccError(checkPoseRef(pr, `${id}'s element ${index}`), res);
    });
    if (curve.type === 'line' && curve.points.length !== 2) {
      return AccError(res, MakeError(`${id}'s line doesn't have 2 points`));
    } else if (curve.type === 'curve' && curve.points.length < 2) {
      return AccError(
        res,
        MakeError(`${id}'s line doesn't have enough points`),
      );
    }
    return res;
  }

  function checkBezierRef(br: BezierRef, id: string): ValidRes {
    if (isRef(br)) {
      return lkup.findBezier(br, context)
        ? true
        : // TODO: This should trigger cross file lookup
          MakeError(`${id}'s bezier reference appears to be undefined`);
    }
    return checkAnonymousBezier(br, id);
  }

  function checkAnonymousPathChain(
    apc: AnonymousPathChain,
    id: string,
  ): ValidRes {
    let res: ValidRes = true;
    if (isConstantFacing(apc.heading)) {
      res = checkHeadingRef(
        apc.heading.heading,
        `${id}'s constant heading ref`,
      );
    } else if (isLinearFacing(apc.heading)) {
      res = checkHeadingRef(apc.heading.start, `${id}'s start heading ref`);
      res = AccError(
        checkHeadingRef(apc.heading.end, `${id}'s end heading ref`),
        res,
      );
    } else if (isTangentFacing(apc.heading)) {
      // Nothing to see here...
    } else {
      // TODO: Handing reverse, point, and piecewise
      console.error(
        'NYI: Not checking the heading interpolator used by this path chain!',
        apc.heading,
      );
    }
    apc.paths.forEach((br, index) => {
      res = AccError(checkBezierRef(br, `${id}'s path element ${index}`), res);
    });
    return res;
  }

  function validateUniqueNames(): ValidRes {
    const allNames = new Set<string>([
      ...fileIndex.namedValues.keys(),
      ...fileIndex.namedPoses.keys(),
      ...fileIndex.namedBeziers.keys(),
      ...fileIndex.namedPathChains.keys(),
    ]);
    if (
      allNames.size !==
      fileIndex.namedValues.size +
        fileIndex.namedPoses.size +
        fileIndex.namedBeziers.size +
        fileIndex.namedPathChains.size
    ) {
      // TODO: Provide a detailed diagnostic of which names are duplicated
      return MakeError(
        'Duplicate names found between values, points, beziers, and path chains.',
      );
    }
    return true;
  }

  function validatePathChainIndex(): ErrorOr<true> {
    let good: ValidRes = true;
    fileIndex.namedPoses.forEach((pr, name) => {
      good = AccError(checkPoseRef(pr, name), good);
    });
    fileIndex.namedBeziers.forEach((br, name) => {
      good = AccError(checkBezierRef(br, name), good);
    });
    fileIndex.namedPathChains.forEach((apc, name) => {
      good = AccError(checkAnonymousPathChain(apc, name), good);
    });
    good = AccError(validateUniqueNames(), good);
    return isError(good) ? good : true;
  }

  const res = validatePathChainIndex();
  if (isError(res)) {
    return res;
  }
  return true;
}

function cerr(nm: string, set: Set<string>): Error {
  return new Error(
    `Circular reference for ${nm} (${[...set.keys()].join(', ')} cause the cycle)`,
  );
}

export function calcValueRef(
  idx: FileIndex,
  vr: ValueRef | RadiansRef,
  circ?: Set<string>,
): number {
  let av = vr;
  const seen = new Set<string>(circ ?? []);
  while (isRef(av)) {
    if (seen.has(av)) {
      throw cerr(av, seen);
    }
    seen.add(av);
    const maybe = idx.namedValues.get(av as ValueName);
    if (isUndefined(maybe)) {
      throw new Error(`Invalid ValueRef ${vr} through ${av}`);
    }
    av = maybe;
  }
  /* This shouldn't ever occur
  if (isUndefined(av)) {
    throw new Error(`Invalid ValueRef ${vr}`);
  }
  */
  return calcValue(idx, av, seen);
}

export function calcPoseRefHeading(
  idx: FileIndex,
  pr: PoseRef,
  circ?: Set<string>,
): number {
  let ap = pr;
  const seen = new Set<string>(circ ?? []);
  while (isRef(ap)) {
    if (seen.has(ap)) {
      throw cerr(ap, seen);
    }
    seen.add(ap);
    const maybe = idx.namedPoses.get(ap);
    if (isUndefined(maybe)) {
      throw new Error(`Invalid PoseRef heading ${pr} through ${ap}`);
    }
    ap = maybe;
  }
  if (isUndefined(ap)) {
    throw new Error(`Invalid PoseRef ${pr}`);
  }
  if (isUndefined(ap.heading)) {
    throw new Error(`No heading for Pose ${ap} from PoseRef ${pr}`);
  }
  return calcHeadingRef(idx, ap.heading, seen);
}

export function calcPoseRef(
  idx: FileIndex,
  pr: PoseRef,
  circ?: Set<string>,
): Point {
  let ap = pr;
  const seen = new Set<string>(circ ?? []);
  while (isRef(ap)) {
    if (seen.has(ap)) {
      throw cerr(ap, seen);
    }
    seen.add(ap);
    const maybe = idx.namedPoses.get(ap);
    if (isUndefined(maybe)) {
      throw new Error(`Invalid PoseRef ${pr} through ${ap}`);
    }
    ap = maybe;
  }
  if (isUndefined(ap)) {
    throw new Error(`Invalid PoseRef ${pr}`);
  }
  return { x: calcValueRef(idx, ap.x, seen), y: calcValueRef(idx, ap.y, seen) };
}

export function calcBezierRef(
  idx: FileIndex,
  br: BezierRef,
  circ?: Set<string>,
): Point[] {
  let ab = br;
  const seen = new Set<string>(circ ?? []);
  while (isRef(ab)) {
    if (seen.has(ab)) {
      throw cerr(ab, seen);
    }
    seen.add(ab);
    const maybe = idx.namedBeziers.get(ab);
    if (isUndefined(maybe)) {
      throw new Error(`Invalid BezierRef ${br} through ${ab}`);
    }
    ab = maybe;
  }
  /*
  if (isUndefined(ab)) {
    throw new Error(`Invalid BezierRef ${br}`);
  }
  */
  return ab.points.map((p) => calcPoseRef(idx, p, seen));
}

export function calcHeadingRef(
  idx: FileIndex,
  hr: HeadingRef,
  circ?: Set<string>,
): number {
  if (isRef(hr)) {
    // Either a PoseName, AnonymousValue, or ValueName;
    if (isAnonymousValue(hr)) {
      return calcValueRef(idx, hr, circ);
    }
    const val = idx.namedValues.get(hr as ValueName);
    if (isDefined(val)) {
      return calcValueRef(idx, val, circ);
    }
    const pose = idx.namedPoses.get(hr as PoseName);
    if (isDefined(pose)) {
      return calcPoseRefHeading(idx, pose, circ);
    }
    throw new Error(`Missing heading for ${hr}`);
  } else if (isRadiansRef(hr)) {
    return (Math.PI * calcValueRef(idx, hr.radians, circ)) / 180.0;
  } else {
    return calcValueRef(idx, hr, circ);
  }
}

// Evaluation from the parsed code representation:
export function calcValue(
  idx: FileIndex,
  av: AnonymousValue | RadiansRef,
  circ?: Set<string>,
): number {
  if (isDoubleValue(av)) {
    return av.double;
  } else if (isIntValue(av)) {
    return av.int;
  } else {
    return (Math.PI * calcValueRef(idx, av.radians, circ)) / 180.0;
  }
}

export const EmptyMappedFile: FileIndex = {
  container: EmptyPathChainClass,
  namedValues: new Map(),
  namedPoses: new Map(),
  namedBeziers: new Map(),
  namedPathChains: new Map(),
};
