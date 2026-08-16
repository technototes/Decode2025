import { isDefined, isUndefined } from '@freik/typechk';

import {
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
  isTangentFacing,
  isValueName,
} from '../CodeTypeCheck';
import {
  AnonymousFacing,
  AnonymousValue,
  BezierRef,
  FacingConstant,
  FacingLinear,
  FacingPiece,
  FacingPieceWise,
  FacingPoint,
  FacingReversed,
  FacingReversible,
  FacingSimple,
  HeadingRef,
  ParsedClass,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../CodeTypes';
import {
  ConcreteConstantHeading,
  ConcreteHeading,
  ConcreteLinearHeading,
  ConcretePiece,
  ConcretePiecewiseHeading,
  ConcretePointHeading,
  ConcreteReversedHeading,
  ConcreteReversibleHeading,
  ConcreteSimpleHeading,
  ConcreteTangentHeading,
  Point,
} from './ConcreteEvalTypes';
import { GetNameLookup } from './state/IndexedFile';

// Very dumb canstants lookup table
export function readConstant(name: string): number | undefined {
  switch (name) {
    case 'Math.PI':
      return Math.PI;
    case 'Math.E':
      return Math.E;
  }
}

/*export*/ function calcValueRef(
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
      // Special case constants go here:
      const value = readConstant(av);
      if (isDefined(value)) {
        return value;
      }
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

/*export*/ function calcPoseRefHeading(
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
  let h: number | undefined;
  if (isDefined(ap.heading)) {
    h = calcHeadingRef(ap.heading, ctx, circ);
  }
  return {
    x: calcValueRef(ap.x, ctx, seen),
    y: calcValueRef(ap.y, ctx, seen),
    h,
  };
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

/*export*/ function calcHeadingRef(
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
    const constVal = readConstant(hr);
    if (isDefined(constVal)) {
      return constVal;
    }
    throw new Error(`Missing heading for ${hr}`);
  } else if (isRadiansRef(hr)) {
    return (Math.PI * calcValueRef(hr.radians, ctx, circ)) / 180.0;
  } else {
    return calcValueRef(hr, ctx, circ);
  }
}

/*export*/ function calcValue(
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

export function calcFacing(
  heading: AnonymousFacing,
  ctx: ParsedClass,
): ConcreteHeading {
  if (isReversibleFacing(heading)) {
    return mkReversible(heading, ctx);
  } else if (isReversedFacing(heading)) {
    return mkReversed(heading, ctx);
  } else if (isPiecewiseFacing(heading)) {
    return mkPiecewise(heading, ctx);
  }
  return mkTangent();
}

function cerr(nm: string, set: Set<string>): Error {
  return new Error(
    `Circular reference for ${nm} (${[...set.keys()].join(', ')} cause the cycle)`,
  );
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
  heading: FacingReversible,
  ctx: ParsedClass,
): ConcreteReversibleHeading {
  if (isTangentFacing(heading)) {
    return mkTangent();
  } else if (isConstantFacing(heading)) {
    return mkConstant(heading, ctx);
  } else if (isLinearFacing(heading)) {
    return mkLinear(heading, ctx);
  } else if (isPointFacing(heading)) {
    return mkPoint(heading, ctx);
  }
  throw new Error(`Unknown simple Facing type`);
}

function mkReversed(
  heading: FacingReversed,
  ctx: ParsedClass,
): ConcreteReversedHeading {
  const revheading = mkReversible(heading.facing, ctx);
  return { type: 'R', heading: revheading };
}

function mkSimple(
  heading: FacingSimple,
  ctx: ParsedClass,
): ConcreteSimpleHeading {
  if (isReversedFacing(heading)) {
    return mkReversed(heading, ctx);
  } else {
    return mkReversible(heading, ctx);
  }
}

function mkPiece(piece: FacingPiece, ctx: ParsedClass): ConcretePiece {
  return {
    start: calcValueRef(piece.timing.start, ctx),
    end: calcValueRef(piece.timing.end, ctx),
    heading: mkSimple(piece.heading, ctx),
  };
}

function mkPiecewise(
  head: FacingPieceWise,
  ctx: ParsedClass,
): ConcretePiecewiseHeading {
  return { type: 'L', pieces: head.pieces.map((fp) => mkPiece(fp, ctx)) };
}

export function GetValueAsString(vr: ValueRef): string {
  if (isValueName(vr)) {
    return vr;
  }
  if (isIntValue(vr)) {
    return vr.int.toFixed(0);
  }
  // Even in *radians* this is about .57 of a degree, so 2 decimal places seems good enough
  return vr.double.toFixed(2);
}
