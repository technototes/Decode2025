import { expect, test } from 'bun:test';
import {
  chkTeamPaths,
  isAnonymousBezier,
  isAnonymousPose,
  isAnonymousValue,
  isBezierRef,
  isConstantHeading,
  isHeadingRef,
  isHeadingType,
  isInterpolatedHeading,
  isNamedBezier,
  isNamedPathChain,
  isNamedPose,
  isNamedValue,
  isPoseRef,
  isRadiansRef,
  isRef,
  isTangentHeading,
  isValueRef,
} from '../types';

test('Parsed file types validation', () => {
  const aRef = 'asdf';
  const notARef = 1;
  expect(isRef(aRef)).toBeTrue();
  expect(isRef(notARef)).toBeFalse();
  const aTeamPath = { path: ['path1', 'path2/path3'] };
  const notATeamPath = { path: 1 };
  expect(chkTeamPaths(aTeamPath)).toBeTrue();
  expect(chkTeamPaths(notATeamPath)).toBeFalse();
  const anonValI = { int: 1 };
  const anonValD = { double: 1.5 };
  const anonValR = { radians: { double: 23.3 } };
  const badVal = { float: 1.5 };
  const extVal = { radians: { dumb: 1 } };
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
  const anonBezL = { type: 'line', points: ['a', 'b'] };
  const anonBezC = {
    type: 'curve',
    points: ['a', { x: 'a', y: { int: 1 } }, 'b'],
  };
  expect(isAnonymousBezier(anonBezL)).toBeTrue();
  expect(isAnonymousBezier(anonBezC)).toBeTrue();
  expect(isNamedBezier({ name: 'bez', points: anonBezC })).toBeTrue();
  expect(isNamedBezier({ name: 'bez', points: anonBezL })).toBeTrue();
  expect(isBezierRef('a')).toBeTrue();
  expect(isBezierRef(anonBezC)).toBeTrue();
  expect(isBezierRef(Symbol('lol'))).toBeFalse();
  const tangHead = { type: 'tangent' };
  const constHead = { type: 'constant', heading: 'heading' };
  const linHead = {
    type: 'interpolated',
    headings: [{ radians: 'ref' }, anonValI],
  };
  expect(isTangentHeading(tangHead)).toBeTrue();
  expect(isConstantHeading(tangHead)).toBeFalse();
  expect(isInterpolatedHeading(tangHead)).toBeFalse();
  expect(isTangentHeading(constHead)).toBeFalse();
  expect(isConstantHeading(constHead)).toBeTrue();
  expect(isInterpolatedHeading(constHead)).toBeFalse();
  expect(isTangentHeading(linHead)).toBeFalse();
  expect(isConstantHeading(linHead)).toBeFalse();
  expect(isInterpolatedHeading(linHead)).toBeTrue();
  expect(isHeadingType(tangHead)).toBeTrue();
  expect(isHeadingType(constHead)).toBeTrue();
  expect(isHeadingType(linHead)).toBeTrue();
  const npc = { name: 'path1', paths: [anonBezC, 'bezRef'], heading: tangHead };
  expect(isNamedPathChain(npc)).toBeTrue();
  expect(isNamedPathChain({ ...npc, headings: [1] })).toBeFalse();
});
