import { Nominal } from './TypeHelpers';

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
export const BezierType = Object.freeze({
  Line: 'line',
  Curve: 'curve',
} as const);
export type BezierType = (typeof BezierType)[keyof typeof BezierType];
export type AnonymousBezier = { type: BezierType; points: PoseRef[] };
export type NamedBezier = { name: BezierName; points: BezierRef };
export type BezierRef = AnonymousBezier | BezierName;

// Facing (path heading interpolators)
// NYI: Offset; works like reverse, but shifts the bot from the target by a
// fixed amount. Reverse is *mostly* "offset 180" (not for linear)
export type FacingTiming = { start: ValueRef; end: ValueRef };

export const FacingType = Object.freeze({
  Reversed: 'reversed',
  Tangent: 'tangent',
  Constant: 'constant',
  Linear: 'linear',
  Point: 'point',
  Piecewise: 'piecewise',
} as const);
export type FacingType = (typeof FacingType)[keyof typeof FacingType];
export type FacingReversed = {
  type: typeof FacingType.Reversed;
  facing: FacingReversible;
};
export type FacingTangent = { type: typeof FacingType.Tangent };
export type FacingConstant = {
  type: typeof FacingType.Constant;
  heading: HeadingRef;
};
export type FacingPoint = { type: typeof FacingType.Point; point: PoseRef };
export type FacingLinear = {
  type: typeof FacingType.Linear;
  start: HeadingRef;
  end: HeadingRef;
};
export type FacingReversible =
  FacingTangent | FacingConstant | FacingLinear | FacingPoint;
export type FacingSimple = FacingReversible | FacingReversed;
export type FacingPiece = { timing: FacingTiming; heading: FacingSimple };
export type FacingPieceWise = {
  type: typeof FacingType.Piecewise;
  pieces: FacingPiece[];
};
export type AnonymousFacing =
  | FacingTangent
  | FacingConstant
  | FacingLinear
  | FacingPoint
  | FacingPieceWise
  | FacingReversed;

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

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: AnonymousFacing;
};

export type ClassContainer = { fileName: string } | { className: string };

export type ParsedClass = {
  name: string;
  fullName: string;
  imports: string[];
  container: ClassContainer;
  children: Record<string, ParsedClass>;
  values: NamedValue[];
  poses: NamedPose[];
  beziers: NamedBezier[];
  pathChains: NamedPathChain[];
  pathChainHelpers: PathChainHelper[];
};
