import {
  chkAnyOf,
  chkArrayOf,
  chkObjectOfExactType,
  chkTupleOf,
  isArrayOfString,
  isDefined,
  isFunction,
  isNumber,
  isRecordOf,
  isString,
  typecheck,
} from '@freik/typechk';

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
  const errors = [];
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
export type IntValue = { int: number };
export type DoubleValue = { double: number };
export type AnonymousValue = IntValue | DoubleValue;
export type ValueName = Nominal<string, 'Value'>;
export type NamedValue = { name: ValueName; value: ValueRef | RadiansRef };
export type ValueRef = AnonymousValue | ValueName;
export type RadiansRef = { radians: ValueRef };
export type HeadingRef = RadiansRef | ValueRef | PoseName;

export type PoseName = Nominal<string, 'Pose'>;
export type AnonymousPose = { x: ValueRef; y: ValueRef; heading?: HeadingRef };
export type NamedPose = { name: PoseName; pose: PoseRef };
export type PoseRef = AnonymousPose | PoseName;

export type BezierName = Nominal<string, 'Bezier'>;
export type AnonymousBezier = { type: 'line' | 'curve'; points: PoseRef[] };
export type NamedBezier = { name: BezierName; points: BezierRef };
export type BezierRef = AnonymousBezier | BezierName;

// Reversed headings are not yet handled
export type ReversedHeading = { reversed?: boolean };
// Heading timing isn't yet handled
export type HeadingTiming = { start?: ValueRef; end?: ValueRef };
// FacingPoint heading's are yet handled, either...
export type FacingHeading = { type: 'facing'; facing: PoseRef };

export type TangentHeading = { type: 'tangent' };
export type ConstantHeading = { type: 'constant'; heading: HeadingRef };
export type InterpolatedHeading = {
  type: 'interpolated';
  headings: [HeadingRef, HeadingRef];
};
export type HeadingType = // ReversedHeading & HeadingTiming ( FacingHeading | ...
  TangentHeading | ConstantHeading | InterpolatedHeading;

// No such thing as an anonymous PathChain
export type PathChainName = Nominal<string, 'PathChain'>;
// Also: I'm not yet handling global vs. last heading modifiers
export type NamedPathChain = {
  name: PathChainName;
  paths: BezierRef[];
  heading: HeadingType;
};

export type PathChainFile = {
  name: string;
  values: NamedValue[];
  poses: NamedPose[];
  beziers: NamedBezier[];
  pathChains: NamedPathChain[];
};

export const EmptyPathChainFile = {
  name: '',
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
};

export type MaybePathFile = ErrorOr<PathChainFile>;

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

function isTangentHeadingType(type: unknown): type is 'tangent' {
  return type === 'tangent';
}
function isConstantHeadingType(type: unknown): type is 'constant' {
  return type === 'constant';
}
function isInterpolatedHeadingType(type: unknown): type is 'interpolated' {
  return type === 'interpolated';
}
export const isTangentHeading = chkObjectOfExactType<TangentHeading>({
  type: isTangentHeadingType,
});
export const isConstantHeading = chkObjectOfExactType<ConstantHeading>({
  type: isConstantHeadingType,
  heading: isHeadingRef,
});
export const isInterpolatedHeading = chkObjectOfExactType<InterpolatedHeading>({
  type: isInterpolatedHeadingType,
  headings: chkTupleOf(isHeadingRef, isHeadingRef),
});
export const isHeadingType = chkAnyOf(
  isTangentHeading,
  isConstantHeading,
  isInterpolatedHeading,
);

export const isNamedPathChain = chkObjectOfExactType<NamedPathChain>({
  name: isString,
  paths: chkArrayOf(isBezierRef),
  heading: isHeadingType,
});

export const chkPathChainFile = chkObjectOfExactType<PathChainFile>({
  name: isString,
  values: chkArrayOf(isNamedValue),
  poses: chkArrayOf(isNamedPose),
  beziers: chkArrayOf(isNamedBezier),
  pathChains: chkArrayOf(isNamedPathChain),
});

// Not used yet, but these are the results of evaluating the various types
export type RealValue = Nominal<number, 'Value'>;
export type RealPoint = { x: RealValue; y: RealValue };
export type RealBezier = RealPoint[];
export type RealPathChain = {
  paths: RealBezier[];
  // Tangent, Constant, Linear, FacingPoint:
  heading: 'tangent' | RealValue | [RealValue, RealValue] | RealPoint;
};

export const MakeRealValue = (n: number) => n as RealValue;
export function MakeRealPoint(x: RealValue, y: RealValue): RealPoint {
  return { x, y };
}
