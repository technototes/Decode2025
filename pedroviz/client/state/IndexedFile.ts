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
  AnonymousFacing,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  EmptyParsedClass,
  FacingConstant,
  FacingLinear,
  FacingPiece,
  FacingPieceWise,
  FacingPoint,
  FacingReversed,
  FacingSimple,
  HeadingRef,
  isAnonymousValue,
  isConstantFacing,
  isDoubleValue,
  isIntValue,
  isLinearFacing,
  isPiecewiseFacing,
  isPointFacing,
  isRadiansRef,
  isRef,
  isReversedFacing,
  isReversibleFacing,
  isSimpleFacing,
  isTangentFacing,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import {
  AnonymousPathChain,
  chkConcreteTangentHeading,
  ConcreteConstantHeading,
  ConcreteHeadingType,
  ConcreteLinearHeading,
  ConcretePiece,
  ConcretePieceWiseHeading,
  ConcretePointHeading,
  ConcreteReversedHeading,
  ConcreteReversibleHeadingType,
  ConcreteSimpleHeadingType,
  ConcreteTangentHeading,
  NameLookup,
  OneFileIndex,
  Point,
} from '../types';
import { ValidRes } from './API';

export function MakeFileIndex(container: ParsedClass): OneFileIndex {
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
  let indexMap: Map<string, OneFileIndex> = new Map();
  const registerIndex = (index: OneFileIndex) => {
    indexMap.set(index.container.fullName, index);
  };
  // Private helper to find an index for a given full name (or parsed class)
  const getIndex = (pc: ParsedClass | string): OneFileIndex | undefined => {
    return indexMap.get(isString(pc) ? pc : pc.fullName);
  };
  // Private helper to look for names, including cross-class and static namespace shortcuts
  function dig<K extends string, V>(
    val: K,
    context: ParsedClass,
    sel: (idx: OneFileIndex) => Map<K, V>,
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
    context: ParsedClass,
  ): ValueRef | RadiansRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedValues);
  };
  const findPose = (
    val: PoseName,
    context: ParsedClass,
  ): PoseRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedPoses);
  };
  const findBezier = (
    val: BezierName,
    context: ParsedClass,
  ): BezierRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedBeziers);
  };
  const findPath = (
    val: PathChainName,
    context: ParsedClass,
  ): AnonymousPathChain | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedPathChains);
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
  fileIndex: OneFileIndex,
  lkup: NameLookup,
  context: ParsedClass,
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
  vr: ValueRef | RadiansRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): number {
  let av = vr;
  const lkup = GetNameLookup();
  const seen = new Set<string>(circ ?? []);
  while (isRef(av)) {
    if (seen.has(av)) {
      throw cerr(av, seen);
    }
    seen.add(av);
    const maybe = lkup.findValue(av as ValueName, ctx);
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
  return calcValue(av, ctx, seen);
}

export function calcPoseRefHeading(
  pr: PoseRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): number {
  let ap = pr;
  const seen = new Set<string>(circ ?? []);
  const lkup = GetNameLookup();
  while (isRef(ap)) {
    if (seen.has(ap)) {
      throw cerr(ap, seen);
    }
    seen.add(ap);
    const maybe = lkup.findPose(ap, ctx);
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
  return calcHeadingRef(ap.heading, ctx, seen);
}

export function calcPoseRef(
  pr: PoseRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): Point {
  let ap = pr;
  const lkup = GetNameLookup();
  const seen = new Set<string>(circ ?? []);
  while (isRef(ap)) {
    if (seen.has(ap)) {
      throw cerr(ap, seen);
    }
    seen.add(ap);
    const maybe = lkup.findPose(ap, ctx);
    if (isUndefined(maybe)) {
      throw new Error(`Invalid PoseRef ${pr} through ${ap}`);
    }
    ap = maybe;
  }
  if (isUndefined(ap)) {
    throw new Error(`Invalid PoseRef ${pr}`);
  }
  return { x: calcValueRef(ap.x, ctx, seen), y: calcValueRef(ap.y, ctx, seen) };
}

export function calcBezierRef(
  br: BezierRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): Point[] {
  const lkup = GetNameLookup();
  let ab = br;
  const seen = new Set<string>(circ ?? []);
  while (isRef(ab)) {
    if (seen.has(ab)) {
      throw cerr(ab, seen);
    }
    seen.add(ab);
    const maybe = lkup.findBezier(ab, ctx);
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
  return ab.points.map((p) => calcPoseRef(p, ctx, seen));
}

export function calcHeadingRef(
  hr: HeadingRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): number {
  if (isRef(hr)) {
    // Either a PoseName, AnonymousValue, or ValueName;
    if (isAnonymousValue(hr)) {
      return calcValueRef(hr, ctx, circ);
    }
    const lkup = GetNameLookup();
    const val = lkup.findValue(hr as ValueName, ctx);
    if (isDefined(val)) {
      return calcValueRef(val, ctx, circ);
    }
    const pose = lkup.findPose(hr as PoseName, ctx);
    if (isDefined(pose)) {
      return calcPoseRefHeading(pose, ctx, circ);
    }
    throw new Error(`Missing heading for ${hr}`);
  } else if (isRadiansRef(hr)) {
    return (Math.PI * calcValueRef(hr.radians, ctx, circ)) / 180.0;
  } else {
    return calcValueRef(hr, ctx, circ);
  }
}

// Evaluation from the parsed code representation:
export function calcValue(
  av: AnonymousValue | RadiansRef,
  ctx: ParsedClass,
  circ?: Set<string>,
): number {
  if (isDoubleValue(av)) {
    return av.double;
  } else if (isIntValue(av)) {
    return av.int;
  } else {
    const lkup = GetNameLookup();
    return (Math.PI * calcValueRef(av.radians, ctx, circ)) / 180.0;
  }
}

function mkTangent(): ConcreteTangentHeading {
  return { type: 'T' };
}

function mkConstant(
  heading: FacingConstant,
  ctx: ParsedClass,
): ConcreteConstantHeading {
  return { type: 'C', heading: calcHeadingRef(heading.heading, ctx) };
}

function mkLinear(
  heading: FacingLinear,
  ctx: ParsedClass,
): ConcreteLinearHeading {
  return {
    type: 'I',
    headings: [
      calcHeadingRef(heading.start, ctx),
      calcHeadingRef(heading.end, ctx),
    ],
  };
}

function mkPoint(heading: FacingPoint, ctx: ParsedClass): ConcretePointHeading {
  return { type: 'P', heading: calcPoseRef(heading.point, ctx) };
}

function mkReversible(
  heading: FacingSimple,
  ctx: ParsedClass,
): ConcreteReversibleHeadingType {
  if (isTangentFacing(heading)) {
    return mkTangent();
  } else if (isConstantFacing(heading)) {
    return mkConstant(heading, ctx);
  } else if (isLinearFacing(heading)) {
    return mkLinear(heading, ctx);
  } else if (isPointFacing(heading)) {
    return mkPoint(heading, ctx);
  }
  throw new Error('Unknown simple Facing type');
}

function mkReversed(
  heading: FacingReversed,
  ctx: ParsedClass,
): ConcreteReversedHeading {
  const revheading = mkReversible(heading.facing, ctx);
  return { type: 'R', heading: revheading };
}

function mkPiece(piece: FacingPiece, ctx: ParsedClass): ConcretePiece {
  return {
    start: calcValueRef(piece.timing.start, ctx),
    end: calcValueRef(piece.timing.end, ctx),
    heading: mkReversible(piece.heading, ctx),
  };
}

function mkPiecewise(
  head: FacingPieceWise,
  ctx: ParsedClass,
): ConcretePieceWiseHeading {
  return { type: 'L', pieces: head.pieces.map((fp) => mkPiece(fp, ctx)) };
}

export function calcFacing(
  heading: AnonymousFacing,
  ctx: ParsedClass,
): ConcreteHeadingType {
  if (isReversibleFacing(heading)) {
    return mkReversible(heading, ctx);
  } else if (isReversedFacing(heading)) {
    return mkReversed(heading, ctx);
  } else if (isPiecewiseFacing(heading)) {
    return mkPiecewise(heading, ctx);
  }
  return mkTangent();
}

export const EmptyMappedFile: OneFileIndex = {
  container: EmptyParsedClass,
  namedValues: new Map(),
  namedPoses: new Map(),
  namedBeziers: new Map(),
  namedPathChains: new Map(),
  staticShortcuts: new Map(),
};
