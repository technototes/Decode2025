import { isDefined, isUndefined } from '@freik/typechk';
import {
  accError,
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  ErrorOr,
  HeadingRef,
  isAnonymousValue,
  isConstantHeading,
  isDoubleValue,
  isError,
  isInterpolatedHeading,
  isIntValue,
  isRadiansRef,
  isRef,
  makeError,
  PathChainFile,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { ValidRes } from './API';
import { AnonymousPathChain, MappedIndex, Point } from '../types';

export function MakeMappedIndex(
  pcf: PathChainFile,
): ErrorOr<MappedIndex> {
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
      { paths: npc.paths, heading: npc.heading },
    ]),
  );

  function checkValueRef(vr: ValueRef, id: string): ValidRes {
    if (isRef(vr)) {
      if (!namedValues.has(vr)) {
        return makeError(
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
    res = accError(checkValueRef(pose.x, `${id}'s x coordinate`), res);
    return accError(checkValueRef(pose.y, `${id}'s y coordinate`), res);
  }

  function checkPoseRef(pr: PoseRef, id: string): ValidRes {
    if (isRef(pr)) {
      return namedPoses.has(pr)
        ? true
        : makeError(`${id}'s "${pr}" pose reference appears to be undefined`);
    }
    return checkAnonymousPose(pr, id);
  }

  function checkAnonymousBezier(curve: AnonymousBezier, id: string): ValidRes {
    let res: ValidRes = true;
    curve.points.forEach((pr, index) => {
      res = accError(checkPoseRef(pr, `${id}'s element ${index}`), res);
    });
    if (curve.type === 'line' && curve.points.length !== 2) {
      return accError(res, makeError(`${id}'s line doesn't have 2 points`));
    } else if (curve.type === 'curve' && curve.points.length < 2) {
      return accError(
        res,
        makeError(`${id}'s line doesn't have enough points`),
      );
    }
    return res;
  }

  function checkBezierRef(br: BezierRef, id: string): ValidRes {
    if (isRef(br)) {
      return namedBeziers.has(br)
        ? true
        : makeError(`${id}'s bezier reference appears to be undefined`);
    }
    return checkAnonymousBezier(br, id);
  }

  function checkAnonymousPathChain(
    apc: AnonymousPathChain,
    id: string,
  ): ValidRes {
    let res: ValidRes = true;
    if (isConstantHeading(apc.heading)) {
      res = checkHeadingRef(
        apc.heading.heading,
        `${id}'s constant heading ref`,
      );
    } else if (isInterpolatedHeading(apc.heading)) {
      res = checkHeadingRef(
        apc.heading.headings[0],
        `${id}'s start heading ref`,
      );
      res = accError(
        checkHeadingRef(apc.heading.headings[1], `${id}'s end heading ref`),
        res,
      );
    }
    apc.paths.forEach((br, index) => {
      res = accError(checkBezierRef(br, `${id}'s path element ${index}`), res);
    });
    return res;
  }

  function validateUniqueNames(): ValidRes {
    const allNames = new Set<string>([
      ...namedValues.keys(),
      ...namedPoses.keys(),
      ...namedBeziers.keys(),
      ...namedPathChains.keys(),
    ]);
    if (
      allNames.size !==
      namedValues.size +
        namedPoses.size +
        namedBeziers.size +
        namedPathChains.size
    ) {
      // TODO: Provide a detailed diagnostic of which names are duplicated
      return makeError(
        'Duplicate names found between values, points, beziers, and path chains.',
      );
    }
    return true;
  }

  function validatePathChainIndex(): ErrorOr<true> {
    let good: ValidRes = true;
    namedPoses.forEach((pr, name) => {
      good = accError(checkPoseRef(pr, name), good);
    });
    namedBeziers.forEach((br, name) => {
      good = accError(checkBezierRef(br, name), good);
    });
    namedPathChains.forEach((apc, name) => {
      good = accError(checkAnonymousPathChain(apc, name), good);
    });
    good = accError(validateUniqueNames(), good);
    return isError(good) ? good : true;
  }

  const res = validatePathChainIndex();
  if (isError(res)) {
    return res;
  }

  return { namedValues, namedBeziers, namedPoses, namedPathChains };
}

function cerr(nm: string, set: Set<string>): Error {
  return new Error(
    `Circular reference for ${nm} (${[...set.keys()].join(', ')} cause the cycle)`,
  );
}

export function calcValueRef(
  idx: MappedIndex,
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
    av = idx.namedValues.get(av as ValueName);
  }
  /* This shouldn't ever occur
  if (isUndefined(av)) {
    throw new Error(`Invalid ValueRef ${vr}`);
  }
  */
  return calcValue(idx, av, seen);
}

export function calcPoseRefHeading(
  idx: MappedIndex,
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
    ap = idx.namedPoses.get(ap);
  }
  /*
  if (isUndefined(ap)) {
    throw new Error(`Invalid PoseRef ${pr}`);
  }
  if (isUndefined(ap.heading)) {
    throw new Error(`No heading for Pose ${ap} from PoseRef ${pr}`);
  }
  */
  return calcHeadingRef(idx, ap.heading, seen);
}

export function calcPoseRef(
  idx: MappedIndex,
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
    ap = idx.namedPoses.get(ap);
  }
  if (isUndefined(ap)) {
    throw new Error(`Invalid PoseRef ${pr}`);
  }
  return { x: calcValueRef(idx, ap.x, seen), y: calcValueRef(idx, ap.y, seen) };
}

export function calcBezierRef(
  idx: MappedIndex,
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
    ab = idx.namedBeziers.get(ab);
  }
  /*
  if (isUndefined(ab)) {
    throw new Error(`Invalid BezierRef ${br}`);
  }
  */
  return ab.points.map((p) => calcPoseRef(idx, p, seen));
}

export function calcHeadingRef(
  idx: MappedIndex,
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
  idx: MappedIndex,
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
export const EmptyMappedFile: MappedIndex = {
  namedValues: new Map(),
  namedPoses: new Map(),
  namedBeziers: new Map(),
  namedPathChains: new Map(),
};
