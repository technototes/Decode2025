import { Point } from '../state/types';

// Note to self: A lof of this stuff looks like a bit of memoization
// could be *very* helpful, particularly given that I might be animating
// these things a fair bit.

// De Casteljau algorithm for Bezier curves (2D)
export function deCasteljau(controlPoints: Point[], t: number): Point {
  let points = controlPoints.map((p) => ({ ...p }));

  for (let r = 1; r < points.length; r++) {
    for (let i = 0; i < points.length - r; i++) {
      points[i] = {
        x: (1 - t) * points[i].x + t * points[i + 1].x,
        y: (1 - t) * points[i].y + t * points[i + 1].y,
      };
    }
  }
  return points[0];
}

// Compute derivative control points (2D)
function derivativeControlPoints(controlPoints: Point[]): Point[] {
  const d = controlPoints.length - 1;
  const result: Point[] = [];

  for (let i = 0; i < d; i++) {
    result.push({
      x: d * (controlPoints[i + 1].x - controlPoints[i].x),
      y: d * (controlPoints[i + 1].y - controlPoints[i].y),
    });
  }
  return result;
}

// Evaluate derivative at t (2D)
export function bezierDerivative(controlPoints: Point[], t: number): Point {
  const derivPoints = derivativeControlPoints(controlPoints);
  return deCasteljau(derivPoints, t);
}

// Arc length via Simpson's rule (2D)
export function bezierLength(
  controlPoints: Point[],
  intervals: number = 100,
): number {
  const h = 1 / intervals;
  let sum = 0;

  function speed(t: number): number {
    const d = bezierDerivative(controlPoints, t);
    return Math.sqrt(d.x * d.x + d.y * d.y);
  }

  for (let i = 0; i <= intervals; i++) {
    const t = i * h;
    const coeff = i === 0 || i === intervals ? 1 : i % 2 === 0 ? 2 : 4;
    sum += coeff * speed(t);
  }

  return (h / 3) * sum;
}

/*
// Cubic Bezier in 2D
const controlPoints: Point[] = [
  { x: 0, y: 0 },
  { x: 50, y: 100 },
  { x: 150, y: -50 },
  { x: 200, y: 200 },
];

// console.log('Point at t=0.5:', deCasteljau(controlPoints, 0.5));
// console.log('Derivative at t=0.5:', bezierDerivative(controlPoints, 0.5));
// console.log('Arc length:', bezierLength(controlPoints, 200));
*/
