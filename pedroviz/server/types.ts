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
} from '@freik/typechk';

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
    isString(error) ? [error] : isError(error) ? error.errors() : error,
  );
  if (isDefined(more)) {
    errors.push(isString(more) ? [more] : isError(more) ? more.errors() : more);
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
export type TeamPaths = Record<string, string[]>;

export type AnonymousValue = {
  type: 'int' | 'double' | 'radians';
  value: number;
};
export type NamedValue = { name: string; value: AnonymousValue };
export type ValueRef = AnonymousValue | string;
export type RadiansRef = { radians: ValueRef };
export type HeadingRef = RadiansRef | ValueRef;

export type AnonymousPose = { x: ValueRef; y: ValueRef; heading?: HeadingRef };
export type NamedPose = { name: string; pose: AnonymousPose };
export type PoseRef = AnonymousPose | string;

export type AnonymousBezier = { type: 'line' | 'curve'; points: PoseRef[] };
export type NamedBezier = { name: string; points: AnonymousBezier };
export type BezierRef = AnonymousBezier | string;

export type TangentHeading = { type: 'tangent' };
export type ConstantHeading = { type: 'constant'; heading: HeadingRef };
export type InterpolatedHeading = {
  type: 'interpolated';
  headings: [HeadingRef, HeadingRef];
};
export type HeadingType =
  | TangentHeading
  | ConstantHeading
  | InterpolatedHeading;

// No such thing as an anonymous PathChain
export type NamedPathChain = {
  name: string;
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

export type MaybePathFile = ErrorOr<PathChainFile>;

export function chkTeamPaths(t: unknown): t is TeamPaths {
  return isRecordOf(t, isString, isArrayOfString);
}

export const isRef = isString;

function isValueTypeName(t: unknown): t is 'int' | 'double' | 'radians' {
  return t === 'int' || t === 'double' || t === 'radians';
}

export const chkAnonymousValue = chkObjectOfExactType<AnonymousValue>({
  type: isValueTypeName,
  value: isNumber,
});
export const chkNamedValue = chkObjectOfExactType<NamedValue>({
  name: isString,
  value: chkAnonymousValue,
});
export const chkValueRef = chkAnyOf(isString, chkAnonymousValue);
export const chkRadiansRef = chkObjectOfExactType<RadiansRef>({
  radians: chkValueRef,
});

export const chkHeadingRef = chkAnyOf(chkValueRef, chkRadiansRef);

export const chkAnonymousPose = chkObjectOfExactType<AnonymousPose>(
  {
    x: chkValueRef,
    y: chkValueRef,
  },
  { heading: chkHeadingRef },
);
export const chkNamedPose = chkObjectOfExactType<NamedPose>({
  name: isString,
  pose: chkAnonymousPose,
});
export const chkPoseRef = chkAnyOf(isString, chkAnonymousPose);

function isBezierTypeName(t: unknown): t is 'line' | 'curve' {
  return t === 'line' || t === 'curve';
}
export const chkAnonymousBezier = chkObjectOfExactType<AnonymousBezier>({
  type: isBezierTypeName,
  points: chkArrayOf(chkPoseRef),
});
export const chkNamedBezier = chkObjectOfExactType<NamedBezier>({
  name: isString,
  points: chkAnonymousBezier,
});
export const chkBezierRef = chkAnyOf(isString, chkAnonymousBezier);

function isTangentHeadingType(type: unknown): type is 'tangent' {
  return type === 'tangent';
}
function isConstantHeadingType(type: unknown): type is 'constant' {
  return type === 'constant';
}
function isInterpolatedHeadingType(type: unknown): type is 'interpolated' {
  return type === 'interpolated';
}
export const chkTangentHeading = chkObjectOfExactType<TangentHeading>({
  type: isTangentHeadingType,
});
export const chkConstantHeading = chkObjectOfExactType<ConstantHeading>({
  type: isConstantHeadingType,
  heading: chkHeadingRef,
});
export const chkInterpolatedHeading = chkObjectOfExactType<InterpolatedHeading>(
  {
    type: isInterpolatedHeadingType,
    headings: chkTupleOf(chkHeadingRef, chkHeadingRef),
  },
);
export const chkHeadingType = chkAnyOf(
  chkTangentHeading,
  chkConstantHeading,
  chkInterpolatedHeading,
);

export const chkNamedPathChain = chkObjectOfExactType<NamedPathChain>({
  name: isString,
  paths: chkArrayOf(chkBezierRef),
  heading: chkHeadingType,
});

export const chkPathChainFile = chkObjectOfExactType<PathChainFile>({
  name: isString,
  values: chkArrayOf(chkNamedValue),
  poses: chkArrayOf(chkNamedPose),
  beziers: chkArrayOf(chkNamedBezier),
  pathChains: chkArrayOf(chkNamedPathChain),
});
