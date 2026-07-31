import {
  chkAnyOf,
  chkArrayOf,
  chkObjectOfExactType,
  chkTupleOf,
  isNumber,
  isString,
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

export const CtrlPtStyles = Object.freeze({
  Circle: 'o',
  Triangle: 't',
  Square: 's',
  Crosshair: '+',
  X: 'x',
  None: 'z',
} as const);
export type CtrlPtStyles = (typeof CtrlPtStyles)[keyof typeof CtrlPtStyles];

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
    Style: CtrlPtStyles;
  };
};

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: AnonymousFacing;
};

export type Point = { x: number; y: number };
export const chkPoint = chkObjectOfExactType({ x: isNumber, y: isNumber });

export const ConcreteHeadingType = Object.freeze({
  Tangent: 'T',
  Constant: 'C',
  Linear: 'I',
  Point: 'P',
  Reverse: 'R',
  Piecewise: 'L',
} as const);
export type ConcreteHeadingType =
  (typeof ConcreteHeadingType)[keyof typeof ConcreteHeadingType];
export function chkConcreteHeadingType(
  val: unknown,
): val is ConcreteHeadingType {
  return isString(val) && 'TCIPRL'.indexOf(val) >= 0;
}

export type ConcreteTangentHeading = {
  type: typeof ConcreteHeadingType.Tangent;
};
export type ConcreteConstantHeading = {
  type: typeof ConcreteHeadingType.Constant;
  heading: number;
};
export type ConcreteLinearHeading = {
  type: typeof ConcreteHeadingType.Linear;
  headings: [number, number];
};
export type ConcretePointHeading = {
  type: typeof ConcreteHeadingType.Point;
  heading: Point;
};
export type ConcreteReversedHeading = {
  type: typeof ConcreteHeadingType.Reverse;
  heading: ConcreteReversibleHeading;
};
export type ConcretePiece = {
  start: number;
  end: number;
  heading: ConcreteSimpleHeading;
};
export type ConcretePiecewiseHeading = {
  type: typeof ConcreteHeadingType.Piecewise;
  pieces: ConcretePiece[];
};
export type ConcreteReversibleHeading =
  | ConcreteTangentHeading
  | ConcreteConstantHeading
  | ConcreteLinearHeading
  | ConcretePointHeading;
export type ConcreteSimpleHeading =
  ConcreteReversibleHeading | ConcreteReversedHeading;
export type ConcreteHeading = ConcreteSimpleHeading | ConcretePiecewiseHeading;

export const chkConcreteTangentHeading =
  chkObjectOfExactType<ConcreteTangentHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Tangent =>
      t === ConcreteHeadingType.Tangent,
  });
export const chkConcreteConstantHeading =
  chkObjectOfExactType<ConcreteConstantHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Constant =>
      t === ConcreteHeadingType.Constant,
    heading: isNumber,
  });
export const chkConcreteLinearHeading =
  chkObjectOfExactType<ConcreteLinearHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Linear =>
      t === ConcreteHeadingType.Linear,
    headings: chkTupleOf(isNumber, isNumber),
  });
export const chkConcretePointHeading =
  chkObjectOfExactType<ConcretePointHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Point =>
      t === ConcreteHeadingType.Point,
    heading: chkPoint,
  });
export const chkConcreteReversibleHeading: typecheck<ConcreteReversibleHeading> =
  chkAnyOf(
    chkConcreteTangentHeading,
    chkConcreteConstantHeading,
    chkConcreteLinearHeading,
    chkConcretePointHeading,
  );
export const chkConcreteReversedHeading =
  chkObjectOfExactType<ConcreteReversedHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Reverse =>
      t === ConcreteHeadingType.Reverse,
    heading: chkConcreteReversibleHeading,
  });
export const chkConcreteSimpleHeading: typecheck<ConcreteSimpleHeading> =
  chkAnyOf(chkConcreteReversibleHeading, chkConcreteReversedHeading);
export const chkConcretePiece = chkObjectOfExactType({
  start: isNumber,
  end: isNumber,
  heading: chkConcreteSimpleHeading,
});
export const chkConcretePieceWiseHeading =
  chkObjectOfExactType<ConcretePiecewiseHeading>({
    type: (t: unknown): t is typeof ConcreteHeadingType.Piecewise =>
      t === ConcreteHeadingType.Piecewise,
    pieces: chkArrayOf(chkConcretePiece),
  });
export const chkConcreteHeading = chkAnyOf(
  chkConcretePieceWiseHeading,
  chkConcreteSimpleHeading,
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

export const ValidateState = Object.freeze({
  Error: 'error',
  Warning: 'warning',
  Success: 'success',
  None: 'none',
});
export type ValidationState =
  (typeof ValidateState)[keyof typeof ValidateState];
export type ValidationData = {
  message: string;
  state: ValidationState;
};
export const ValidData: ValidationData = Object.freeze({
  message: '',
  state: ValidateState.None,
} as const);
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
