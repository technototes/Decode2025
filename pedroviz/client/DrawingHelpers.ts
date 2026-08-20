import { Point } from './ConcreteEvalTypes';
import { bezierLength, deCasteljau } from './ui-tools/bezier';

export type CurveDetail = { length: number; points: Point[] };

export function rotPt(offs: Point): Point {
  const cos = Math.cos(offs.h!);
  const sin = Math.sin(offs.h!);
  const x = offs.x * cos - offs.y * sin;
  const y = offs.x * sin + offs.y * cos;
  return { x, y };
}

export function getBezierPoints(curveControlPoints: Point[]): Point[] {
  const len = bezierLength(curveControlPoints);
  const pts: Point[] = [];
  for (let t = 0; t <= 1.0; t += 1 / len) {
    const h = t * Math.PI * 2;
    pts.push({ ...deCasteljau(curveControlPoints, t), h });
  }
  return pts;
}

export function getCurveDetail(curveControlPoints: Point[]): CurveDetail {
  if (curveControlPoints.length < 2) {
    return { length: 0, points: [] };
  }
  const points: Point[] = getBezierPoints(curveControlPoints);
  let length = 0;
  let lastPt = curveControlPoints[0]!;
  for (const pt of points) {
    length += ptDistance(lastPt, pt);
    lastPt = pt;
  }
  length += ptDistance(
    lastPt,
    curveControlPoints[curveControlPoints.length - 1]!,
  );
  return { length, points };
}

export function ptDiff(a: Point, b: Point): Point {
  return { x: a.x - b.x, y: a.y - b.y };
}

export function ptDistance(a: Point, b: Point): number {
  const delta = ptDiff(a, b);
  return Math.sqrt(delta.x * delta.x + delta.y * delta.y);
}
