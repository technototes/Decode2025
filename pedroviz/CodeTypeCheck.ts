import {
  chkAnyOf,
  chkArrayOf,
  chkFieldOf,
  chkObjectOfExactType,
  chkRecordOf,
  hasFieldOf,
  hasFieldType,
  hasStrField,
  isNumber,
  isString,
  typecheck,
} from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousFacing,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  BezierType,
  ClassContainer,
  DoubleValue,
  FacingConstant,
  FacingLinear,
  FacingPiece,
  FacingPieceWise,
  FacingPoint,
  FacingReversed,
  FacingReversible,
  FacingTangent,
  FacingTiming,
  FacingType,
  HeadingRef,
  IntValue,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  ParsedClass,
  PathChainHelper,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from './CodeTypes';

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
/*export*/ const isNamedValue = chkObjectOfExactType<NamedValue>({
  name: isString,
  value: chkAnyOf(isValueRef, isRadiansRef),
});

/*export*/ const isHeadingRef: typecheck<HeadingRef> = chkAnyOf(
  isValueRef,
  isRadiansRef,
);

export const isPoseName: typecheck<PoseName> = isString as typecheck<PoseName>;
/*export*/ const isAnonymousPose = chkObjectOfExactType<AnonymousPose>(
  {
    x: isValueRef,
    y: isValueRef,
  },
  { heading: isHeadingRef },
);
/*export*/ const isNamedPose = chkObjectOfExactType<NamedPose>({
  name: isString,
  pose: isAnonymousPose,
});
/*export*/ const isPoseRef: typecheck<PoseRef> = chkAnyOf(
  isPoseName,
  isAnonymousPose,
);
function isBezierTypeName(t: unknown): t is BezierType {
  return t === BezierType.Line || t === BezierType.Curve;
}
/*export*/ const isBezierName: typecheck<BezierName> =
  isString as typecheck<BezierName>;
/*export*/ const isAnonymousBezier = chkObjectOfExactType<AnonymousBezier>({
  type: isBezierTypeName,
  points: chkArrayOf(isPoseRef),
});
/*export*/ const isNamedBezier = chkObjectOfExactType<NamedBezier>({
  name: isString,
  points: isAnonymousBezier,
});
/*export*/ const isBezierRef: typecheck<BezierRef> = chkAnyOf(
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
/*export*/ const isFacingTiming = chkObjectOfExactType<FacingTiming>({
  start: isValueRef,
  end: isValueRef,
});
export const isReversedFacing: typecheck<FacingReversed> =
  chkObjectOfExactType<FacingReversed>({
    type: isReversedFacingType,
    facing: isReversibleFacing,
  });
/*export*/ const isSimpleFacing = chkAnyOf(
  isReversibleFacing,
  isReversedFacing,
);
/*export*/ const isPiecewiseEntry: typecheck<FacingPiece> =
  chkObjectOfExactType<FacingPiece>({
    timing: isFacingTiming,
    heading: isSimpleFacing,
  });
export const isPiecewiseFacing = chkObjectOfExactType<FacingPieceWise>({
  type: isPiecewiseFacingType,
  pieces: chkArrayOf(isPiecewiseEntry),
});
/*export*/ const isAnonymousFacing: typecheck<AnonymousFacing> = (
  obj: unknown,
): obj is AnonymousFacing => {
  return chkAnyOf(
    isTangentFacing,
    isConstantFacing,
    isLinearFacing,
    isPointFacing,
    isPiecewiseFacing,
    isReversedFacing,
  )(obj);
};

/*export*/ const isNamedPathChain = chkObjectOfExactType<NamedPathChain>({
  name: isString,
  paths: chkArrayOf(isBezierRef),
  heading: isAnonymousFacing,
});

/*export*/ const isPathChainHelper = chkObjectOfExactType<PathChainHelper>({
  name: isString,
  staticType: isString,
});

/*export*/ const isClassContainer: typecheck<ClassContainer> = chkAnyOf(
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
