import {
  chkConcreteLinearHeading,
  chkConcreteSimpleHeading,
} from './ConcreteEvalTypeCheck';
import {
  ConcreteHeading,
  ConcreteHeadingType,
  ConcreteSimpleHeading,
  Point,
} from './ConcreteEvalTypes';
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

// TODO: Figure out how Pedro decides what percentage of the curve applies to
// heading pieces and linear interpolations
export function addHeadingToCurve(
  curve: CurveDetail,
  heading: ConcreteHeading,
): void {
  // for "n" points, I'm not drawing starting/ending headings, so I actually want to split
  // the length into count + 1 pieces, and find the point in between each piece
  let curDist = 0;
  if (curve.points.length < 3) {
    return;
  }
  let prevPoint: Point | null = null;
  for (let ptIndex = 0; ptIndex < curve.points.length; ptIndex++) {
    const cur = curve.points[ptIndex]!;
    curDist += prevPoint ? ptDistance(prevPoint, cur) : 0;
    addHeadingToPoint(
      prevPoint,
      cur,
      ptIndex + 1 < curve.points.length ? curve.points[ptIndex + 1]! : null,
      heading,
      curDist / curve.length,
    );
    // Keep going
    prevPoint = cur;
  }
}

function addHeadingToPoint(
  prev: Point | null,
  point: Point,
  next: Point | null,
  heading: ConcreteHeading,
  percent: number,
) {
  if (chkConcreteSimpleHeading(heading)) {
    point.h = calcSimpleHeading(heading, prev, point, next, percent);
  } else /* if (heading.type == ConcreteHeadingType.Piecewise)*/ {
    // Find the piece that this percentage is contained by
    // I think a linear search is fine, here..
    for (const piece of heading.pieces) {
      if (percent <= piece.end && percent >= piece.start) {
        // Found the right piece, rescale percentage accordingly, and
        // get the heading for the piece
        point.h = calcSimpleHeading(
          piece.heading,
          prev,
          point,
          next,
          (percent - piece.start) / (piece.end - piece.start),
        );
      }
    }
  }
}

export function ptDiff(a: Point, b: Point): Point {
  return { x: a.x - b.x, y: a.y - b.y };
}

export function ptDistance(a: Point, b: Point): number {
  const delta = ptDiff(a, b);
  return Math.sqrt(delta.x * delta.x + delta.y * delta.y);
}

export function vector(angle: number, mag: number): Point {
  return { x: Math.cos(angle) * mag, y: Math.sin(angle) * mag };
}

function getAngle(pt: Point): number {
  return Math.atan2(pt.y, pt.x);
}

export function calcSimpleHeading(
  heading: ConcreteSimpleHeading,
  prev: Point | null,
  cur: Point,
  nxt: Point | null,
  percent: number,
): number {
  if (prev === null && nxt === null) {
    throw new Error("Can't have both prev and next points be null");
  }
  switch (heading.type) {
    case ConcreteHeadingType.Tangent:
      return getAngle(prev === null ? ptDiff(nxt!, cur) : ptDiff(cur, prev));
    case ConcreteHeadingType.Constant:
      return heading.heading;
    case ConcreteHeadingType.Linear:
      return linearRangeRadians(
        heading.headings[0],
        heading.headings[1],
        percent,
      );
    case ConcreteHeadingType.Point:
      return getAngle(ptDiff(cur, heading.heading));
    case ConcreteHeadingType.Reverse:
      // Get the target point, then flip it the other direction, unless it's linear.
      // For Linear, it travels the opposite direction of the normal linear heading.
      const lin = chkConcreteLinearHeading(heading.heading);
      const toReverse = calcSimpleHeading(
        heading.heading,
        prev,
        cur,
        nxt,
        lin ? -percent : percent,
      );
      return lin ? toReverse : normalizeRadian(toReverse + Math.PI);
  }
}

export function CloseTo(a: number, b: number): boolean {
  return Math.abs(a - b) < 1e-8;
}

function normalizeRadian(a: number) {
  const result = a % (2 * Math.PI);
  return result >= 0 ? result : result + 2 * Math.PI;
}

export function linearRangeRadians(
  start: number,
  end: number,
  percent: number,
): number {
  // First, push the values until they're all positive:
  const s = normalizeRadian(start);
  const e = normalizeRadian(end);
  let range = Math.abs(e - s);
  if (range > Math.PI && percent >= 0) {
    range = Math.PI * 2 - range;
  } else if (range < Math.PI && percent < 0) {
    range = Math.PI * 2 - range;
  }
  const flipped = !CloseTo(normalizeRadian(s + range), e);
  const target = normalizeRadian(
    s + range * (flipped ? -1 : 1) * Math.abs(percent),
  );
  return target;
}
