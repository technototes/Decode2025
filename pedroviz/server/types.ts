import {
  chkAnyOf,
  chkArrayOf,
  chkFieldOf,
  chkMapOf,
  chkObjectOfExactType,
  chkRecordOf,
  chkTupleOf,
  ErrorOr,
  hasFieldOf,
  hasFieldType,
  hasStrField,
  isArrayOfString,
  isNumber,
  isRecordOf,
  isString,
  typecheck,
} from '@freik/typechk';

// This is a Typescript mechanism to allow strings to be constrained a bit more.
// I'm not sure if it's worth the trouble or not...
declare const brand: unique symbol;
export type Nominal<T, Brand extends string> = T & { readonly [brand]: Brand };

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

export const EmptyParsedClass: ParsedClass = {
  name: '',
  fullName: '',
  imports: [],
  container: { fileName: '' },
  children: {},
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  pathChainHelpers: [],
};

export type MaybePathFile = ErrorOr<ParsedClass>;
export type PathDBKey = Nominal<string, 'DBKey'>;
export type PathDBValue = [string[], ParsedClass];
export type PathDatabase = Map<PathDBKey, PathDBValue>;

export function chkPathDBKey(obj: unknown): obj is PathDBKey {
  if (!isString(obj)) {
    return false;
  }
  const pieces = obj.split('*');
  return pieces.length === 2;
}
export const chkPathDBValue: typecheck<PathDBValue> = chkTupleOf(
  isArrayOfString,
  chkParsedClass,
);
export const chkPathDatabase: typecheck<PathDatabase> = chkMapOf(
  chkPathDBKey,
  chkPathDBValue,
);

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

function isBezierTypeName(t: unknown): t is BezierType {
  return t === BezierType.Line || t === BezierType.Curve;
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

function isTangentFacingType(type: unknown): type is typeof FacingType.Tangent {
  return type === FacingType.Tangent;
}
function isConstantFacingType(
  type: unknown,
): type is typeof FacingType.Constant {
  return type === FacingType.Constant;
}
function isLinearFacingType(type: unknown): type is typeof FacingType.Linear {
  return type === FacingType.Linear;
}
function isPointFacingType(type: unknown): type is typeof FacingType.Point {
  return type === FacingType.Point;
}
function isReversedFacingType(
  type: unknown,
): type is typeof FacingType.Reversed {
  return type === FacingType.Reversed;
}
function isPiecewiseFacingType(
  type: unknown,
): type is typeof FacingType.Piecewise {
  return type === FacingType.Piecewise;
}

export function getFacingType(facing: AnonymousFacing): FacingType {
  return facing.type;
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
export const isReversibleFacing: typecheck<FacingReversible> = chkAnyOf(
  isTangentFacing,
  isConstantFacing,
  isLinearFacing,
  isPointFacing,
);
export const isFacingTiming = chkObjectOfExactType<FacingTiming>({
  start: isValueRef,
  end: isValueRef,
});
export const isReversedFacing: typecheck<FacingReversed> =
  chkObjectOfExactType<FacingReversed>({
    type: isReversedFacingType,
    facing: isReversibleFacing,
  });
export const isSimpleFacing = chkAnyOf(isReversibleFacing, isReversedFacing);
export const isPiecewiseEntry: typecheck<FacingPiece> =
  chkObjectOfExactType<FacingPiece>({
    timing: isFacingTiming,
    heading: isSimpleFacing,
  });
export const isPiecewiseFacing = chkObjectOfExactType<FacingPieceWise>({
  type: isPiecewiseFacingType,
  pieces: chkArrayOf(isPiecewiseEntry),
});
export const isAnonymousFacing: typecheck<AnonymousFacing> = (
  obj: unknown,
): obj is AnonymousFacing => {
  if (
    chkAnyOf(
      isTangentFacing,
      isConstantFacing,
      isLinearFacing,
      isPointFacing,
      isPiecewiseFacing,
      isReversedFacing,
    )
  ) {
    return true;
  }
  console.log('Anonymous failure:', obj);
  return false;
};

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
export function chkParsedClass(val: unknown): val is ParsedClass {
  let res = hasStrField(val, 'name');
  res = res && hasFieldOf(val, 'container', isClassContainer);
  res = res && hasFieldOf(val, 'values', chkArrayOf(isNamedValue));
  res = res && hasFieldOf(val, 'poses', chkArrayOf(isNamedPose));
  res = res && hasFieldOf(val, 'beziers', chkArrayOf(isNamedBezier));
  res = res && hasFieldOf(val, 'pathChains', chkArrayOf(isNamedPathChain));
  res =
    res && hasFieldOf(val, 'pathChainHelpers', chkArrayOf(isPathChainHelper));
  res =
    res && hasFieldType(val, 'children', chkRecordOf(isString, chkParsedClass));
  return res;
}
