import { expect, test } from 'bun:test';
import {
  chkAnonymousBezier,
  chkAnonymousPose,
  chkAnonymousValue,
  chkBezierRef,
  chkConstantHeading,
  chkHeadingRef,
  chkHeadingType,
  chkInterpolatedHeading,
  chkNamedBezier,
  chkNamedPathChain,
  chkNamedPose,
  chkNamedValue,
  chkPoseRef,
  chkRadiansRef,
  chkTangentHeading,
  chkTeamPaths,
  chkValueRef,
  isRef,
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
  const anonValI = { type: 'int', value: 1 };
  const anonValD = { type: 'double', value: 1.5 };
  const anonValR = { type: 'radians', value: 23.3 };
  const badVal = { type: 'float', value: 1.5 };
  const extVal = { type: 'radians', value: 1, dumb: true };
  expect(chkAnonymousValue(anonValI)).toBeTrue();
  expect(chkAnonymousValue(anonValD)).toBeTrue();
  expect(chkAnonymousValue(anonValR)).toBeTrue();
  expect(chkAnonymousValue(badVal)).toBeFalse();
  expect(chkAnonymousValue(extVal)).toBeFalse();
  const namedVal = { name: 'me', value: anonValI };
  const badNamedV = { name: 'me', value: badVal };
  const extNamedV = { ...namedVal, dumb: 1 };
  expect(chkNamedValue(namedVal)).toBeTrue();
  expect(chkNamedValue(badNamedV)).toBeFalse();
  expect(chkNamedValue(extNamedV)).toBeFalse();
  expect(chkValueRef(aRef)).toBeTrue();
  expect(chkValueRef(anonValR)).toBeTrue();
  expect(chkValueRef(namedVal)).toBeFalse();
  const radRefR = { radians: 'asdf' };
  const radRefI = { radians: { type: 'int', value: 1 } };
  expect(chkRadiansRef(radRefR)).toBeTrue();
  expect(chkRadiansRef(radRefI)).toBeTrue();
  expect(chkHeadingRef(radRefR)).toBeTrue();
  expect(chkHeadingRef(anonValR)).toBeTrue();
  const anonPoseXY = { x: 'a', y: 'b' };
  const anonPoseXYH = { x: 'c', y: 'd', heading: { radians: 'var' } };
  const badAnonPose = { x: 1, y: 'b' };
  expect(chkAnonymousPose(anonPoseXY)).toBeTrue();
  expect(chkAnonymousPose(anonPoseXYH)).toBeTrue();
  expect(chkAnonymousPose(badAnonPose)).toBeFalse();
  const namedPose1 = { name: 'me', pose: anonPoseXYH };
  expect(chkNamedPose(namedPose1)).toBeTrue();
  expect(chkNamedPose({ ...namedPose1, dumb: 2 })).toBeFalse();
  expect(chkPoseRef('ab')).toBeTrue();
  expect(chkPoseRef(anonPoseXY)).toBeTrue();
  const anonBezL = { type: 'line', points: ['a', 'b'] };
  const anonBezC = {
    type: 'curve',
    points: ['a', { x: 'a', y: { type: 'int', value: 1 } }, 'b'],
  };
  expect(chkAnonymousBezier(anonBezL)).toBeTrue();
  expect(chkAnonymousBezier(anonBezC)).toBeTrue();
  expect(chkNamedBezier({ name: 'bez', points: anonBezC })).toBeTrue();
  expect(chkNamedBezier({ name: 'bez', points: anonBezL })).toBeTrue();
  expect(chkBezierRef('a')).toBeTrue();
  expect(chkBezierRef(anonBezC)).toBeTrue();
  expect(chkBezierRef(Symbol('lol'))).toBeFalse();
  const tangHead = { type: 'tangent' };
  const constHead = { type: 'constant', heading: 'heading' };
  const linHead = {
    type: 'interpolated',
    headings: [{ radians: 'ref' }, anonValI],
  };
  expect(chkTangentHeading(tangHead)).toBeTrue();
  expect(chkConstantHeading(tangHead)).toBeFalse();
  expect(chkInterpolatedHeading(tangHead)).toBeFalse();
  expect(chkTangentHeading(constHead)).toBeFalse();
  expect(chkConstantHeading(constHead)).toBeTrue();
  expect(chkInterpolatedHeading(constHead)).toBeFalse();
  expect(chkTangentHeading(linHead)).toBeFalse();
  expect(chkConstantHeading(linHead)).toBeFalse();
  expect(chkInterpolatedHeading(linHead)).toBeTrue();
  expect(chkHeadingType(tangHead)).toBeTrue();
  expect(chkHeadingType(constHead)).toBeTrue();
  expect(chkHeadingType(linHead)).toBeTrue();
  const npc = { name: 'path1', paths: [anonBezC, 'bezRef'], heading: tangHead };
  expect(chkNamedPathChain(npc)).toBeTrue();
  expect(chkNamedPathChain({ ...npc, headings: [1] })).toBeFalse();
});
