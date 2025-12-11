import { expect, test } from 'bun:test';
import { Point } from '../state/types';
import {
  bezierDerivative,
  bezierLength,
  deCasteljau,
} from '../ui-tools/bezier';

// I never really followed through on math enough to do more than cover
// the bare essentials of this thing, but here's a test:
test('2 point bezier', () => {
  const start: Point = { x: 0, y: 0 };
  const end: Point = { x: 1, y: 1 };
  const line = [start, end];
  const curve = deCasteljau(line, 0.5);
  expect(curve).toEqual({ x: 0.5, y: 0.5 });
  const slope = bezierDerivative(line, 0.5);
  expect(slope.x).toEqual(slope.y);
  const len = bezierLength(line, 10);
  expect(Math.abs(Math.sqrt(2) - len)).toBeLessThan(1e-5);
});
