import {
  chkAnyOf,
  chkObjectOfExactType,
  chkTupleOf,
  isNumber,
  typecheck,
} from '@freik/typechk';

import {
  AnonymousFacing,
  BezierName,
  BezierRef,
  isIntValue,
  isValueName,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../server/types';

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: AnonymousFacing;
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

export type FileIndex = {
  container: ParsedClass;
  namedValues: Map<ValueName, ValueRef | RadiansRef>;
  namedPoses: Map<PoseName, PoseRef>;
  namedBeziers: Map<BezierName, BezierRef>;
  namedPathChains: Map<PathChainName, AnonymousPathChain>;
  staticShortcuts: Map<string, string>;
};

export type NameLookup = {
  registerIndex: (index: FileIndex) => void;
  reset: () => void;
  findValue: (
    val: ValueName,
    context: ParsedClass,
  ) => ValueRef | RadiansRef | undefined;
  findPose: (pose: PoseName, context: ParsedClass) => PoseRef | undefined;
  findBezier: (bez: BezierName, context: ParsedClass) => BezierRef | undefined;
  findPath: (
    pc: PathChainName,
    context: ParsedClass,
  ) => AnonymousPathChain | undefined;
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
  message: string;
  state: ValidationState;
};
export const ValidData: ValidationData = Object.freeze({
  message: '',
  state: 'none',
});
export function ValidationResult(
  message: string,
  state: ValidationState,
): ValidationData {
  return { message, state };
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
