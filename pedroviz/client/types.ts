import {
  chkAnyOf,
  chkObjectOfExactType,
  chkTupleOf,
  isNumber,
  typecheck,
} from '@freik/typechk';
import {
  BezierName,
  BezierRef,
  HeadingType,
  isIntValue,
  isValueName,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../server/types';

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: HeadingType;
};

export type ConcreteTangentHeading = { htype: 'T' };
export const chkConreteTangentHeading =
  chkObjectOfExactType<ConcreteTangentHeading>({
    htype: (t: unknown): t is 'T' => t === 'T',
  });
export type ConcreteConstantHeading = {
  htype: 'C';
  heading: number;
};
export const chkConcreteConstantHeading =
  chkObjectOfExactType<ConcreteConstantHeading>({
    htype: (t: unknown): t is 'C' => t === 'C',
    heading: isNumber,
  });
export type ConcreteInterpolatedHeading = {
  htype: 'I';
  headings: [number, number];
};
export const chkConcreteInterpolatedHeading =
  chkObjectOfExactType<ConcreteInterpolatedHeading>({
    htype: (t: unknown): t is 'I' => t === 'I',
    headings: chkTupleOf(isNumber, isNumber),
  });
export type ConcreteHeadingType =
  | ConcreteTangentHeading
  | ConcreteConstantHeading
  | ConcreteInterpolatedHeading;
export const chkConcreteHeadingType: typecheck<ConcreteHeadingType> = chkAnyOf(
  chkConreteTangentHeading,
  chkConcreteConstantHeading,
  chkConcreteInterpolatedHeading,
);

export type MappedIndex = {
  namedValues: Map<ValueName, ValueRef | RadiansRef>;
  namedPoses: Map<PoseName, PoseRef>;
  namedBeziers: Map<BezierName, BezierRef>;
  namedPathChains: Map<PathChainName, AnonymousPathChain>;
};

export type Point = { x: number; y: number };

export type HasItem<T> = {
  has: (item: T) => boolean;
};

export type HasKeys<T> = HasItem<T> & {
  keys: () => Iterable<T>;
};

export type ValidationState = 'error' | 'warning' | 'success' | 'none';
export type ValidationData = {
  message: string,
  state: ValidationState;
};
export const ValidData: ValidationData = Object.freeze({ message: '', state: 'none' });
export function ValidationResult(message: string, state: ValidationState): ValidationData {
  return { message, state };
}

export function GetValueAsString(vr: ValueRef): string {
  if (isValueName(vr))  {
    return vr;
  }
  if (isIntValue(vr)) {
    return vr.int.toFixed(0);
  }
  return vr.double.toFixed(2);
}
