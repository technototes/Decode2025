import {
  chkAnyOf,
  chkArrayOf,
  chkFieldOf,
  chkMapOf,
  chkObjectOfExactType,
  chkRecordOf,
  hasFieldOf,
  hasFieldType,
  hasStrField,
  isArrayOfString,
  isDefined,
  isFunction,
  isNumber,
  isRecordOf,
  isString,
  typecheck,
} from '@freik/typechk';

// This is a Typescript mechanism to allow strings to be constrained a bit more.
// I'm not sure if it's worth the trouble or not...
declare const brand: unique symbol;
export type Nominal<T, Brand extends string> = T & { readonly [brand]: Brand };

export type ErrorVal = {
  errors: () => string[];
  [Symbol.toPrimitive]: (hint: string) => unknown;
};
export type ErrorOr<T> = T | ErrorVal;

export const isError = chkObjectOfExactType<ErrorVal>({
  errors: isFunction,
  [Symbol.toPrimitive]: isFunction,
});
export function makeError(
  error: string | string[] | ErrorVal,
  more?: string | string[] | ErrorVal,
): ErrorVal {
  const errors: string[] = [];
  errors.push(
    ...(isString(error) ? [error] : isError(error) ? error.errors() : error),
  );
  if (isDefined(more)) {
    errors.push(
      ...(isString(more) ? [more] : isError(more) ? more.errors() : more),
    );
  }
  return {
    errors: () => errors,
    [Symbol.toPrimitive]: (hint: string) =>
      hint === 'string' ? errors.join('\n') : null,
  };
}
export function addError<T>(
  maybeErr: ErrorOr<T>,
  moreErrors: string | string[] | ErrorVal,
): ErrorVal {
  if (isError(maybeErr)) {
    return makeError(maybeErr, moreErrors);
  }
  return makeError(moreErrors);
}
export function accError<T>(maybe: ErrorOr<T>, prev: ErrorOr<T>): ErrorOr<T> {
  return isError(prev) ? addError(maybe, prev) : maybe;
}

export type Team = Nominal<string, 'Team'>;
export type Path = Nominal<string, 'Path'>;
export type TeamPaths = Record<Team, Path[]>;

// Values
export type IntValue = { int: number };
export type DoubleValue = { double: number };
export type AnonymousValue = IntValue | DoubleValue;
export type ValueName = Nominal<string, 'Value'>;
export type NamedValue = { name: ValueName; value: ValueRef | RadiansRef };
export type ValueRef = AnonymousValue | ValueName;
export type RadiansRef = { radians: ValueRef };
export type HeadingRef = RadiansRef | ValueRef | PoseName;

// Poses
export type PoseName = Nominal<string, 'Pose'>;
export type AnonymousPose = { x: ValueRef; y: ValueRef; heading?: HeadingRef };
export type NamedPose = { name: PoseName; pose: PoseRef };
export type PoseRef = AnonymousPose | PoseName;

// Beziers
export type BezierName = Nominal<string, 'Bezier'>;
export type AnonymousBezier = { type: 'line' | 'curve'; points: PoseRef[] };
export type NamedBezier = { name: BezierName; points: BezierRef };
export type BezierRef = AnonymousBezier | BezierName;

// Facing (path heading interpolators)
// NYI: Offset; works like reverse, but shifts the bot from the target by a
// fixed amount. Reverse is *mostly* "offset 180" (not for linear)
export type FacingTiming = { start: ValueRef; end: ValueRef };
export type FacingReversed = { type: 'reversed'; facing: FacingRef };
export type FacingTangent = { type: 'tangent' };
export type FacingConstant = { type: 'constant'; heading: HeadingRef };
export type FacingPoint = { type: 'point'; point: PoseRef };
export type FacingLinear = {
  type: 'linear';
  start: HeadingRef;
  end: HeadingRef;
};
export type FacingPiece = { timing: FacingTiming; heading: FacingRef };
export type FacingPieceWise = { type: 'piecewise'; pieces: FacingPiece[] };
export type FacingName = Nominal<string, 'Facing'>;
export type AnonymousFacing =
  | FacingTangent
  | FacingConstant
  | FacingLinear
  | FacingPoint
  | FacingPieceWise
  | FacingReversed;
export type NamedFacing = { name: FacingName; heading: AnonymousFacing };
export type FacingRef = AnonymousFacing | FacingName;

// No such thing as an anonymous PathChain
export type PathChainName = Nominal<string, 'PathChain'>;
// Also: I'm not yet handling global vs. last heading modifiers
export type NamedPathChain = {
  name: PathChainName;
  paths: BezierRef[];
  pathHeading: AnonymousFacing;
};

export type PathChainHelper = {
  name: string; // This should just be a simple variable name
  staticType: string; // This should be the package-local type being assigned
};

export type ClassContainer = { fileName: string } | { className: string };

export type PathChainClass = {
  name: string;
  container: ClassContainer;
  children: Record<string, PathChainClass>;
  values: NamedValue[];
  poses: NamedPose[];
  beziers: NamedBezier[];
  pathChains: NamedPathChain[];
  pathChainHelpers: PathChainHelper[];
};

export const EmptyPathChainClass: PathChainClass = {
  name: '',
  container: { fileName: '' },
  children: {},
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  pathChainHelpers: [],
};

export type MaybePathFile = ErrorOr<PathChainClass>;
export type PathDBKey = [Team, Path];
export type PathDBValue = [string[], PathChainClass];
export type PathDatabase = Map<PathDBKey, PathDBValue>;

export function chkTeamPaths(t: unknown): t is TeamPaths {
  return isRecordOf(t, isString, isArrayOfString);
}

export const isRef = isString;
export const isValueName: typecheck<ValueName> =
  isString as typecheck<ValueName>;
export const isIntValue = chkObjectOfExactType<IntValue>({ int: isNumber });
export const isDoubleValue = chkObjectOfExactType<DoubleValue>({
  double: isNumber,
});
export const isAnonymousValue: typecheck<AnonymousValue> = chkAnyOf(
  isIntValue,
  isDoubleValue,
);
export const isValueRef: typecheck<ValueRef> = chkAnyOf(
  isValueName,
  isAnonymousValue,
);
export const isRadiansRef = chkObjectOfExactType<RadiansRef>({
  radians: isValueRef,
});
export const isNamedValue = chkObjectOfExactType<NamedValue>({
  name: isString,
  value: chkAnyOf(isValueRef, isRadiansRef),
});

export const isHeadingRef: typecheck<HeadingRef> = chkAnyOf(
  isValueRef,
  isRadiansRef,
);

export const isPoseName: typecheck<PoseName> = isString as typecheck<PoseName>;
export const isAnonymousPose = chkObjectOfExactType<AnonymousPose>(
  {
    x: isValueRef,
    y: isValueRef,
  },
  { heading: isHeadingRef },
);
export const isNamedPose = chkObjectOfExactType<NamedPose>({
  name: isString,
  pose: isAnonymousPose,
});
export const isPoseRef: typecheck<PoseRef> = chkAnyOf(
  isPoseName,
  isAnonymousPose,
);

function isBezierTypeName(t: unknown): t is 'line' | 'curve' {
  return t === 'line' || t === 'curve';
}
export const isBezierName: typecheck<BezierName> =
  isString as typecheck<BezierName>;
export const isAnonymousBezier = chkObjectOfExactType<AnonymousBezier>({
  type: isBezierTypeName,
  points: chkArrayOf(isPoseRef),
});
export const isNamedBezier = chkObjectOfExactType<NamedBezier>({
  name: isString,
  points: isAnonymousBezier,
});
export const isBezierRef: typecheck<BezierRef> = chkAnyOf(
  isBezierName,
  isAnonymousBezier,
);

function isTangentFacingType(type: unknown): type is 'tangent' {
  return type === 'tangent';
}
function isConstantFacingType(type: unknown): type is 'constant' {
  return type === 'constant';
}
function isLinearFacingType(type: unknown): type is 'linear' {
  return type === 'linear';
}
function isPointFacingType(type: unknown): type is 'point' {
  return type === 'point';
}
function isReversedFacingType(type: unknown): type is 'reversed' {
  return type === 'reversed';
}
function isPiecewiseFacingType(type: unknown): type is 'piecewise' {
  return type === 'piecewise';
}

export const isTangentFacing = chkObjectOfExactType<FacingTangent>({
  type: isTangentFacingType,
});
export const isConstantFacing = chkObjectOfExactType<FacingConstant>({
  type: isConstantFacingType,
  heading: isHeadingRef,
});
export const isLinearFacing = chkObjectOfExactType<FacingLinear>({
  type: isLinearFacingType,
  start: isHeadingRef,
  end: isHeadingRef,
});
export const isPointFacing = chkObjectOfExactType<FacingPoint>({
  type: isPointFacingType,
  point: isPoseRef,
});
export const isFacingTiming = chkObjectOfExactType<FacingTiming>({
  start: isValueRef,
  end: isValueRef,
});
export const isPiecewiseEntry: typecheck<FacingPiece> =
  chkObjectOfExactType<FacingPiece>({
    timing: isFacingTiming,
    heading: isFacingRef,
  });
export const isPiecewiseFacing = chkObjectOfExactType<FacingPieceWise>({
  type: isPiecewiseFacingType,
  pieces: chkArrayOf(isPiecewiseEntry),
});
export const isReversedFacing: typecheck<FacingReversed> =
  chkObjectOfExactType<FacingReversed>({
    type: isReversedFacingType,
    facing: isFacingRef,
  });
export const isAnonymousFacing: typecheck<AnonymousFacing> = chkAnyOf(
  isTangentFacing,
  isConstantFacing,
  isLinearFacing,
  isPointFacing,
  isPiecewiseFacing,
  isReversedFacing,
);
export function isFacingRef(obj: unknown): obj is FacingRef {
  return chkAnyOf(isAnonymousFacing, isString)(obj);
}

export const isNamedPathChain = chkObjectOfExactType<NamedPathChain>({
  name: isString,
  paths: chkArrayOf(isBezierRef),
  pathHeading: isAnonymousFacing,
});

export const isPathChainHelper = chkObjectOfExactType<PathChainHelper>({
  name: isString,
  staticType: isString,
});

export const isClassContainer: typecheck<ClassContainer> = chkAnyOf(
  chkFieldOf('fileName', isString),
  chkFieldOf('className', isString),
);

// Can't use chkObjOfExactType because recursion...
export function chkPathChainClass(val: unknown): val is PathChainClass {
  let res = hasStrField(val, 'name');
  res = res && hasFieldOf(val, 'container', isClassContainer);
  res = res && hasFieldOf(val, 'values', chkArrayOf(isNamedValue));
  res = res && hasFieldOf(val, 'poses', chkArrayOf(isNamedPose));
  res = res && hasFieldOf(val, 'beziers', chkArrayOf(isNamedBezier));
  res = res && hasFieldOf(val, 'pathChains', chkArrayOf(isNamedPathChain));
  res =
    res && hasFieldOf(val, 'pathChainHelpers', chkArrayOf(isPathChainHelper));
  res =
    res &&
    hasFieldType(val, 'children', chkRecordOf(isString, chkPathChainClass));
  return res;
}
