import { expect, test } from 'bun:test';

import { chkPathKey } from 'IpcTypeCheck';

import {
  getFacingType,
  isAnonymousBezier,
  isAnonymousFacing,
  isAnonymousPose,
  isAnonymousValue,
  isBezierRef,
  isConstantFacing,
  isHeadingRef,
  isLinearFacing,
  isNamedBezier,
  isNamedPathChain,
  isNamedPose,
  isNamedValue,
  isPiecewiseFacing,
  isPointFacing,
  isPoseRef,
  isRadiansRef,
  isRef,
  isReversedFacing,
  isTangentFacing,
  isValueRef,
} from '../../CodeTypeCheck';
import { BezierType, FacingPieceWise, FacingType } from '../../CodeTypes';

test('Parsed file types validation', () => {
  const aRef = 'asdf';
  const notARef = 1;
  expect(isRef(aRef)).toBeTrue();
  expect(isRef(notARef)).toBeFalse();
  const aTeamPath = { path: ['path1', 'path2/path3'] };
  const notATeamPath = { path: 1 };
  const anonValI = { int: 1 };
  const anonValD = { double: 1.5 };
  const anonValR = { radians: { double: 23.3 } };
  const badVal = { float: 1.5 };
  const extVal = { radians: { dumb: 1 } };
  expect(chkPathKey(extVal)).toBeFalse();
  expect(isAnonymousValue(anonValI)).toBeTrue();
  expect(isAnonymousValue(anonValD)).toBeTrue();
  expect(isAnonymousValue(anonValR)).toBeFalse();
  expect(isRadiansRef(anonValR)).toBeTrue();
  expect(isAnonymousValue(badVal)).toBeFalse();
  expect(isAnonymousValue(extVal)).toBeFalse();
  expect(isRadiansRef(extVal)).toBeFalse();
  const namedVal = { name: 'me', value: anonValI };
  const badNamedV = { name: 'me', value: badVal };
  const extNamedV = { ...namedVal, dumb: 1 };
  expect(isNamedValue(namedVal)).toBeTrue();
  expect(isNamedValue(badNamedV)).toBeFalse();
  expect(isNamedValue(extNamedV)).toBeFalse();
  expect(isValueRef(aRef)).toBeTrue();
  expect(isHeadingRef(anonValR)).toBeTrue();
  expect(isValueRef(namedVal)).toBeFalse();
  const radRefR = { radians: 'asdf' };
  const radRefI = { radians: { int: 1 } };
  expect(isRadiansRef(radRefR)).toBeTrue();
  expect(isRadiansRef(radRefI)).toBeTrue();
  expect(isHeadingRef(radRefR)).toBeTrue();
  expect(isHeadingRef(anonValR)).toBeTrue();
  const anonPoseXY = { x: 'a', y: 'b' };
  const anonPoseXYH = { x: 'c', y: 'd', heading: { radians: 'var' } };
  const badAnonPose = { x: 1, y: 'b' };
  expect(isAnonymousPose(anonPoseXY)).toBeTrue();
  expect(isAnonymousPose(anonPoseXYH)).toBeTrue();
  expect(isAnonymousPose(badAnonPose)).toBeFalse();
  const namedPose1 = { name: 'me', pose: anonPoseXYH };
  expect(isNamedPose(namedPose1)).toBeTrue();
  expect(isNamedPose({ ...namedPose1, dumb: 2 })).toBeFalse();
  expect(isPoseRef('ab')).toBeTrue();
  expect(isPoseRef(anonPoseXY)).toBeTrue();
  const anonBezL = { type: BezierType.Line, points: ['a', 'b'] };
  const anonBezC = {
    type: BezierType.Curve,
    points: ['a', { x: 'a', y: { int: 1 } }, 'b'],
  };
  expect(isAnonymousBezier(anonBezL)).toBeTrue();
  expect(isAnonymousBezier(anonBezC)).toBeTrue();
  expect(isNamedBezier({ name: 'bez', points: anonBezC })).toBeTrue();
  expect(isNamedBezier({ name: 'bez', points: anonBezL })).toBeTrue();
  expect(isBezierRef('a')).toBeTrue();
  expect(isBezierRef(anonBezC)).toBeTrue();
  expect(isBezierRef(Symbol('lol'))).toBeFalse();
  const tangHead = { type: FacingType.Tangent };
  const constHead = { type: FacingType.Constant, heading: 'heading' };
  const linHead = {
    type: FacingType.Linear,
    start: { radians: 'ref' },
    end: anonValI,
  };
  const pointHead = {
    type: FacingType.Point,
    point: { x: { int: 3 }, y: { double: 3.5 } },
  };
  expect(isTangentFacing(tangHead)).toBeTrue();
  expect(isConstantFacing(tangHead)).toBeFalse();
  expect(isLinearFacing(tangHead)).toBeFalse();
  expect(isTangentFacing(constHead)).toBeFalse();
  expect(isConstantFacing(constHead)).toBeTrue();
  expect(isLinearFacing(constHead)).toBeFalse();
  expect(isTangentFacing(linHead)).toBeFalse();
  expect(isConstantFacing(linHead)).toBeFalse();
  expect(isLinearFacing(linHead)).toBeTrue();
  expect(isPointFacing(pointHead)).toBeTrue();
  expect(isPointFacing(linHead)).toBeFalse();
  expect(isAnonymousFacing(tangHead)).toBeTrue();
  expect(isAnonymousFacing(constHead)).toBeTrue();
  expect(isAnonymousFacing(linHead)).toBeTrue();
  const revHead = {
    type: FacingType.Reversed,
    facing: pointHead,
  };
  expect(isReversedFacing(revHead)).toBeTrue();
  expect(isReversedFacing(pointHead)).toBeFalse();
  const pieceHead: FacingPieceWise = {
    type: FacingType.Piecewise,
    pieces: [
      { timing: { start: { int: 0 }, end: { double: 0.5 } }, heading: revHead },
      {
        timing: { start: { double: 0.5 }, end: { int: 1 } },
        heading: pointHead,
      },
    ],
  };
  expect(isPiecewiseFacing(pieceHead)).toBeTrue();
  const notPiece = { ...pieceHead, nope: false };
  expect(isPiecewiseFacing(notPiece)).toBeFalse();
  expect(getFacingType(pieceHead)).toEqual(FacingType.Piecewise);
  expect(isAnonymousFacing(revHead)).toBeTrue();
  expect(isAnonymousFacing(anonBezC)).toBeFalse();
  const npc = {
    name: 'path1',
    paths: [anonBezC, 'bezRef'],
    pathHeading: tangHead,
  };
  expect(isNamedPathChain(npc)).toBeTrue();
  expect(isNamedPathChain({ ...npc, headings: [1] })).toBeFalse();
});
