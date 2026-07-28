import {
  chkAnyOf,
  chkArrayOf,
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

export type PathRenderOptions = {
  ShowField: boolean;
  PathThickness: number;
  ShowCoords: boolean;
  Heading: {
    Display: boolean;
    Count: number;
    Length: number;
    Thickness: number;
  };
  ControlPoint: {
    Thickness: number;
    Size: number;
    Style: 'circle' | 'triangle' | 'square' | 'cross';
  };
};

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: AnonymousFacing;
};

export type Point = { x: number; y: number };
export const chkPoint = chkObjectOfExactType({ x: isNumber, y: isNumber });

export type ConcreteTangentHeading = { type: 'T' };
export type ConcreteConstantHeading = {
  type: 'C';
  heading: number;
};
export type ConcreteLinearHeading = {
  type: 'I';
  headings: [number, number];
};
export type ConcretePointHeading = {
  type: 'P';
  heading: Point;
};
export type ConcreteReversedHeading = {
  type: 'R';
  heading: ConcreteReversibleHeadingType;
};
export type ConcretePiece = {
  start: number;
  end: number;
  heading: ConcreteSimpleHeadingType;
};
export type ConcretePieceWiseHeading = {
  type: 'L';
  pieces: ConcretePiece[];
};
export type ConcreteReversibleHeadingType =
  | ConcreteTangentHeading
  | ConcreteConstantHeading
  | ConcreteLinearHeading
  | ConcretePointHeading;
export type ConcreteSimpleHeadingType =
  ConcreteReversibleHeadingType | ConcreteReversedHeading;
export type ConcreteHeadingType =
  ConcreteSimpleHeadingType | ConcretePieceWiseHeading;

export const chkConcreteTangentHeading =
  chkObjectOfExactType<ConcreteTangentHeading>({
    type: (t: unknown): t is 'T' => t === 'T',
  });
export const chkConcreteConstantHeading =
  chkObjectOfExactType<ConcreteConstantHeading>({
    type: (t: unknown): t is 'C' => t === 'C',
    heading: isNumber,
  });
export const chkConcreteLinearHeading =
  chkObjectOfExactType<ConcreteLinearHeading>({
    type: (t: unknown): t is 'I' => t === 'I',
    headings: chkTupleOf(isNumber, isNumber),
  });
export const chkConcretePointHeading =
  chkObjectOfExactType<ConcretePointHeading>({
    type: (t: unknown): t is 'P' => t === 'P',
    heading: chkPoint,
  });
export const chkConcreteReversibleHeadingType: typecheck<ConcreteReversibleHeadingType> =
  chkAnyOf(
    chkConcreteTangentHeading,
    chkConcreteConstantHeading,
    chkConcreteLinearHeading,
    chkConcretePointHeading,
  );
export const chkConcreteReversedHeading =
  chkObjectOfExactType<ConcreteReversedHeading>({
    type: (t: unknown): t is 'R' => t === 'R',
    heading: chkConcreteReversibleHeadingType,
  });
export const chkConcreteSimpleHeadingType: typecheck<ConcreteSimpleHeadingType> =
  chkAnyOf(chkConcreteReversedHeading, chkConcreteReversedHeading);
export const chkConcretePiece = chkObjectOfExactType({
  start: isNumber,
  end: isNumber,
  heading: chkConcreteSimpleHeadingType,
});
export const chkConcretePieceWiseHeading =
  chkObjectOfExactType<ConcretePieceWiseHeading>({
    type: (t: unknown): t is 'R' => t === 'R',
    pieces: chkArrayOf(chkConcretePiece),
  });
export const chkConcreteHeadingType = chkAnyOf(
  chkConcretePieceWiseHeading,
  chkConcreteSimpleHeadingType,
);

export type OneFileIndex = {
  container: ParsedClass;
  namedValues: Map<ValueName, ValueRef | RadiansRef>;
  namedPoses: Map<PoseName, PoseRef>;
  namedBeziers: Map<BezierName, BezierRef>;
  namedPathChains: Map<PathChainName, AnonymousPathChain>;
  staticShortcuts: Map<string, string>;
};

export type NameLookup = {
  registerIndex: (index: OneFileIndex) => void;
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
