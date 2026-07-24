import {
  AccError,
  ErrorOr,
  isDefined,
  isError,
  isString,
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
  PathChainHelper,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { AnonymousPathChain, FileIndex, NameLookup, Point } from '../types';
import { ValidRes } from './API';

export function MakeFileIndex(container: PathChainClass): FileIndex {
  const namedValues = new Map<ValueName, ValueRef | RadiansRef>(
    container.values.map((nv) => [nv.name, nv.value]),
  );
  const namedPoses = new Map<PoseName, PoseRef>(
    container.poses.map((np) => [np.name, np.pose]),
  );
  const namedBeziers = new Map<BezierName, BezierRef>(
    container.beziers.map((nb) => [nb.name, nb.points]),
  );
  const namedPathChains = new Map<PathChainName, AnonymousPathChain>(
    container.pathChains.map((npc) => [
      npc.name,
      { paths: npc.paths, heading: npc.pathHeading },
    ]),
  );
  const staticShortcuts = new Map<string, string>(
    container.pathChainHelpers.map((pch) => [pch.name, pch.staticType]),
  );
  return {
    container,
    namedValues,
    namedBeziers,
    namedPoses,
    namedPathChains,
    staticShortcuts,
  };
}

// Make a thing that can accumulate indexes (*can* in the *future*)
function MakeNameLookup(): NameLookup {
  let indexMap: Map<string, FileIndex> = new Map();
  const registerIndex = (index: FileIndex) => {
    indexMap.set(index.container.fullName, index);
  };
  // Private helper to find an index for a given full name (or parsed class)
  const getIndex = (pcc: PathChainClass | string): FileIndex | undefined => {
    return indexMap.get(isString(pcc) ? pcc : pcc.fullName);
  };
  // Private helper to look for names, including cross-class and static namespace shortcuts
  function dig<K extends string, V>(
    val: K,
    context: PathChainClass,
    sel: (idx: FileIndex) => Map<K, V>,
  ): V | undefined {
    const index = getIndex(context);
    if (isUndefined(index)) {
      console.error('Unable to find context index', context.fullName);
      return;
    }
    const res = sel(index).get(val);
    if (isDefined(res)) {
      return res;
    }
    // We didn't find the item. If it has no dots, then it's a flat name, and we're done.
    const dot = val.indexOf('.');
    if (dot < 0) {
      return;
    }
    const ss = getIndex(context)?.staticShortcuts.get(val.substring(0, dot));
    if (isDefined(ss)) {
      // Try again, with the updated name:
      return dig(`${ss}${val.substring(dot)}`, context, sel);
    }
    // Okay, let's see if we can find a package class that matches
    const lastDot = val.lastIndexOf('.');
    const prefix = val.substring(0, lastDot);
    const suffix = val.substring(lastDot + 1);
    // Walk the import list, looking for a fully qualified class name
    // that exists in the index
    for (const imp of context.imports) {
      const pkg = getIndex(`${imp}.${prefix}`);
      if (isUndefined(pkg)) {
        continue;
      }
      const maybe = dig(suffix, pkg.container, sel);
      if (isDefined(maybe)) {
        return maybe;
      }
    }
  }
  const findValue = (
    val: ValueName,
    context: PathChainClass,
  ): ValueRef | RadiansRef | undefined => {
    return dig(val, context, (idx: FileIndex) => idx.namedValues);
  };
  const findPose = (
    val: PoseName,
    context: PathChainClass,
  ): PoseRef | undefined => {
    return dig(val, context, (idx: FileIndex) => idx.namedPoses);
  };
  const findBezier = (
    val: BezierName,
    context: PathChainClass,
  ): BezierRef | undefined => {
    return dig(val, context, (idx: FileIndex) => idx.namedBeziers);
  };
  const findPath = (
    val: PathChainName,
    context: PathChainClass,
  ): AnonymousPathChain | undefined => {
    return dig(val, context, (idx: FileIndex) => idx.namedPathChains);
  };
  const reset = () => {
    indexMap.clear();
  };
  return { registerIndex, findBezier, findPath, findPose, findValue, reset };
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
  staticShortcuts: new Map(),
};
