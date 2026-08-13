import { CSSProperties, ReactElement, useCallback } from 'react';
import { useAtomValue } from 'jotai';

import { isDefined } from '@freik/typechk';

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
import { calcBezierRef, calcFacing } from './ExpressionEval';
import {
  ColorsAtom,
  NamedPathChainsAtom,
  SelectedParsedClassAtom,
} from './state/Atoms';
import { PathRenderOptionsAtom, ThemeAtom } from './state/SavedSettings';
import { CtrlPtStyles, PathRenderOptions } from './types';
import { bezierLength, deCasteljau } from './ui-tools/bezier';
import { ResponsiveSquareCanvas } from './ui-tools/ResponsiveSquareCanvas';

const baseStyle: CSSProperties = {
  border: '2px solid #666',
  // boxSizing: 'border-box',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
};

export function FieldRenderer(): ReactElement {
  const opts = useAtomValue(PathRenderOptionsAtom);
  const theme = useAtomValue(ThemeAtom);

  const colors = useAtomValue(ColorsAtom);
  const allPCs = useAtomValue(NamedPathChainsAtom);
  const file = useAtomValue(SelectedParsedClassAtom);
  const points = allPCs.flatMap((npc) =>
    npc.paths.map((br): [Point[], ConcreteHeading] => [
      calcBezierRef(br, file),
      calcFacing(npc.heading, file),
    ]),
  );

  const bgStyle = opts.ShowField
    ? {
        ...baseStyle,
        backgroundImage: `url('/assets/field-${theme}.jpg')`,
      }
    : baseStyle;

  const renderField = useCallback(
    (ctx: CanvasRenderingContext2D, size: number, dpr: number) => {
      // Map logical 144×144 units into square
      const scale = size / 144;

      // Move the origin to the lower left, corner, and scale it up
      // ctx.translate(0, size * dpr);
      // ctx.scale(dpr * scale, -dpr * scale);
      // or just a single line of code:
      ctx.setTransform(dpr * scale, 0, 0, -dpr * scale, 0, size * dpr);
      if (opts.ShowCoords) {
        renderCoordinateLegend(ctx, dpr, scale, theme);
      }

      points.forEach(([ctrlPoints, facing], index) =>
        renderPath(
          ctx,
          ctrlPoints,
          opts.Heading.Display ? facing : false,
          colors[index % colors.length]!,
          opts,
        ),
      );
    },
    [opts, theme, allPCs, colors, points],
  );

  return (
    <ResponsiveSquareCanvas
      anchor={{ x: 'right', y: 'top' }}
      style={bgStyle}
      render={renderField}
    />
  );
}

function outlineText(
  ctx: CanvasRenderingContext2D,
  str: string,
  x: number,
  y: number,
) {
  ctx.strokeText(str, x, y);
  ctx.fillText(str, x, y);
}

function renderCoordinateLegend(
  ctx: CanvasRenderingContext2D,
  dpr: number,
  scale: number,
  theme: string,
) {
  ctx.save();
  ctx.setTransform(dpr * scale, 0, 0, dpr * scale, 0, 0);
  ctx.font = '4px sans-serif'; // Set font size and family
  ctx.fillStyle = theme === 'light' ? 'black' : 'white'; // Set fill color for the text
  ctx.strokeStyle = theme === 'light' ? 'white' : 'black';
  ctx.lineWidth = 0.2;

  // Draw the coordinate points
  ctx.textAlign = 'start'; // Set text alignment (e.g., "start", "end", "center")
  ctx.textBaseline = 'middle'; // Set vertical alignment (e.g., "top", "middle", "bottom")
  outlineText(ctx, '(0,0)', 2, 139);
  outlineText(ctx, '(0,144)', 2, 5);
  ctx.textAlign = 'end'; // Set text alignment (e.g., "start", "end", "center")
  outlineText(ctx, '(144,0)', 142, 5);
  outlineText(ctx, '(144,144)', 142, 139);
  ctx.textAlign = 'center'; // Set text alignment (e.g., "start", "end", "center")
  outlineText(ctx, '(72,72)', 72, 72);

  // Label the axis directions
  outlineText(ctx, '+y', 71, 84.5);
  outlineText(ctx, '-y', 71, 104.5);
  outlineText(ctx, '-x', 62, 95);
  outlineText(ctx, '+x', 82, 95);

  // Label the compass angles
  outlineText(ctx, '½π 90°', 72, 40);
  outlineText(ctx, '0°', 81, 48);
  outlineText(ctx, 'π 180°', 58, 48);
  outlineText(ctx, '³/₂π 270°', 72, 56);

  // Draw the axis directions
  ctx.lineCap = 'round';
  ctx.lineWidth = 0.5;
  ctx.strokeStyle = theme === 'light' ? 'black' : 'white';
  ctx.beginPath();
  let xc = 72;
  let yc = 95;
  // Vertical double arrow:
  ctx.moveTo(xc - 2, yc - 5);
  ctx.lineTo(xc, yc - 7);
  ctx.lineTo(xc + 2, yc - 5);
  ctx.moveTo(xc, yc - 7);
  ctx.lineTo(xc, yc + 7);
  ctx.moveTo(xc + 2, yc + 5);
  ctx.lineTo(xc, yc + 7);
  ctx.lineTo(xc - 2, yc + 5);
  // Horizontal double arrow
  ctx.moveTo(xc - 5, yc - 2);
  ctx.lineTo(xc - 7, yc);
  ctx.lineTo(xc - 5, yc + 2);
  ctx.moveTo(xc - 7, yc);
  ctx.lineTo(xc + 7, yc);
  ctx.moveTo(xc + 5, yc + 2);
  ctx.lineTo(xc + 7, yc);
  ctx.lineTo(xc + 5, yc - 2);
  // Arrow for the compass
  ctx.moveTo(77 + 1, 48 + 2);
  ctx.lineTo(77, 48);
  ctx.lineTo(77 - 1.75, 48 + 1.75);
  ctx.stroke();
  ctx.beginPath();
  // Arc for the compass
  ctx.arc(72, 48, 5, Math.PI * -0.25, 0, true);
  ctx.stroke();
  ctx.restore();
}

function ptDiff(a: Point, b: Point): Point {
  return { x: a.x - b.x, y: a.y - b.y };
}

function ptDistance(a: Point, b: Point): number {
  const delta = ptDiff(a, b);
  return Math.sqrt(delta.x * delta.x + delta.y * delta.y);
}

function renderPath(
  ctx: CanvasRenderingContext2D,
  curveControlPoints: Point[],
  heading: ConcreteHeading | false,
  color: string,
  opts: PathRenderOptions,
) {
  if (curveControlPoints.length < 2) {
    return;
  }
  const len = bezierLength(curveControlPoints);
  const pts: Point[] = [];
  for (let t = 0; t <= 1.0; t += 1 / len) {
    pts.push(deCasteljau(curveControlPoints, t));
  }
  const drawPath = opts.PathThickness > 1e-10;
  if (drawPath) {
    ctx.beginPath();
  }
  ctx.lineWidth = opts.PathThickness;
  ctx.strokeStyle = color;
  let approxLen = 0;
  if (drawPath) {
    ctx.moveTo(curveControlPoints[0]!.x, curveControlPoints[0]!.y);
  }
  let lastPt = curveControlPoints[0]!;
  for (const pt of pts) {
    approxLen += ptDistance(lastPt, pt);
    lastPt = pt;
    if (drawPath) {
      ctx.lineTo(pt.x, pt.y);
    }
  }
  approxLen += ptDistance(
    lastPt,
    curveControlPoints[curveControlPoints.length - 1]!,
  );
  if (drawPath) {
    ctx.lineTo(
      curveControlPoints[curveControlPoints.length - 1]!.x,
      curveControlPoints[curveControlPoints.length - 1]!.y,
    );
    ctx.stroke();
  }
  if (
    opts.ControlPoint.Style != 'z' &&
    opts.ControlPoint.Size > 1e-10 &&
    opts.ControlPoint.Thickness > 1e-10
  ) {
    drawControlPoints(ctx, opts, curveControlPoints, color);
  }
  if (heading) {
    drawHeadingLines(
      ctx,
      color,
      approxLen,
      [...pts, curveControlPoints[curveControlPoints.length - 1]!],
      heading,
      opts,
    );
  }

  // These two items wil be useful for animation in the footure
  /*
      const tang = bezierDerivative(curveControlPoints, 0.4);
      const mid = deCasteljau(curveControlPoints, 0.4);
      */
}

function drawControlPoints(
  ctx: CanvasRenderingContext2D,
  opts: PathRenderOptions,
  curveControlPoints: Point[],
  color: string,
) {
  ctx.beginPath();
  ctx.lineWidth = opts.ControlPoint.Thickness;
  const half = opts.ControlPoint.Size / 2;
  const shape = opts.ControlPoint.Style;
  for (const pt of curveControlPoints) {
    ctx.strokeStyle = color;
    // TODO: Support more point display styles
    switch (shape) {
      case CtrlPtStyles.Circle:
        ctx.moveTo(pt.x + half, pt.y);
        ctx.arc(pt.x, pt.y, half, 0, 2 * Math.PI);
        break;
      case CtrlPtStyles.Square:
        ctx.rect(
          pt.x - half,
          pt.y - half,
          opts.ControlPoint.Size,
          opts.ControlPoint.Size,
        );
        break;
      case CtrlPtStyles.X:
        ctx.moveTo(pt.x - half, pt.y - half);
        ctx.lineTo(pt.x + half, pt.y + half);
        ctx.moveTo(pt.x - half, pt.y + half);
        ctx.lineTo(pt.x + half, pt.y - half);
        break;
      case CtrlPtStyles.Triangle:
        ctx.moveTo(pt.x - half, pt.y - half);
        ctx.lineTo(pt.x, pt.y + half);
        ctx.lineTo(pt.x + half, pt.y - half);
        ctx.closePath();
        break;
      case CtrlPtStyles.Crosshair:
        ctx.moveTo(pt.x - half, pt.y);
        ctx.lineTo(pt.x + half, pt.y);
        ctx.moveTo(pt.x, pt.y + half);
        ctx.lineTo(pt.x, pt.y - half);
        break;
    }
  }
  ctx.stroke();
}

function drawColors(ctx: CanvasRenderingContext2D, colors: string[]) {
  ctx.save();
  for (let j = 0; j < colors.length; j++) {
    ctx.beginPath();
    ctx.fillStyle = colors[(j + 7) % colors.length]!;
    ctx.fillRect(j * 4, 0, 4, 4);
    ctx.stroke();
  }
  ctx.restore();
}

function magnitude(pt: Point, val: number): Point {
  const mag = Math.sqrt(pt.x * pt.x + pt.y * pt.y);
  return { x: (pt.x * val) / mag, y: (pt.y * val) / mag };
}

function drawHeadingLines(
  ctx: CanvasRenderingContext2D,
  color: string,
  len: number,
  pts: Point[],
  heading: ConcreteHeading,
  opts: PathRenderOptions,
) {
  // for "n" points, I'm not drawing starting/ending headings, so I actually want to split
  // the length into count + 1 pieces, and find the point in between each piece
  const pieceLen = len / (opts.Heading.Count + 1);
  if (opts.Heading.Count <= 0 || pts.length < 3 || pieceLen < 1) {
    return;
  }
  let curPtIndex = 1;
  let lastDelta: Point = { x: 0, y: 0 };
  for (
    let pos = 0;
    pos < opts.Heading.Count && curPtIndex < pts.length;
    pos++
  ) {
    let pathPos = pieceLen * (pos + 1);
    // Walk the length of the path, until we find the heading-draw point
    let point: Point | undefined;
    // This is just laziness. I shouldn't need to recalculate the whole thing, but
    // math is hard...
    for (let curPtIndex = 1; pathPos > 0; curPtIndex++) {
      const prev = pts[curPtIndex - 1]!;
      const cur = pts[curPtIndex]!;
      lastDelta = ptDiff(cur, prev);
      const l = ptDistance(prev, cur);
      if (l >= pathPos) {
        const partWay = magnitude(ptDiff(cur, prev), pathPos);
        point = { x: prev.x + partWay.x, y: prev.y + partWay.y };
      }
      pathPos -= l;
    }
    if (isDefined(point)) {
      // Draw the heading line at this location.
      // Include the tangent, and the portion of the overall path that's complete.
      drawHeadingLine(
        ctx,
        color,
        point,
        lastDelta,
        (pos + 1) / (opts.Heading.Count + 1),
        heading,
        opts,
      );
    }
  }
}

function drawHeadingLine(
  ctx: CanvasRenderingContext2D,
  color: string,
  point: Point,
  tangent: Point,
  percentage: number,
  heading: ConcreteHeading,
  opts: PathRenderOptions,
) {
  let targetPoint: Point = { x: 0, y: 0 };
  if (chkConcreteSimpleHeading(heading)) {
    targetPoint = calcSimpleHeading(heading, point, tangent, percentage);
  } else /* if (heading.type == ConcreteHeadingType.Piecewise)*/ {
    // Find the piece that this percentage is contained by
    // I think a linear search is fine, here..
    for (const piece of heading.pieces) {
      if (percentage <= piece.end && percentage >= piece.start) {
        // Found the right piece, rescale percentage accordingly, and
        // get the heading for the piece
        targetPoint = calcSimpleHeading(
          piece.heading,
          point,
          tangent,
          (percentage - piece.start) / (piece.end - piece.start),
        );
      }
    }
  }
  ctx.beginPath();
  ctx.lineCap = 'round';
  ctx.lineWidth = opts.Heading.Thickness;
  ctx.strokeStyle = color;
  ctx.moveTo(point.x, point.y);
  const displacement = magnitude(targetPoint, opts.Heading.Length);
  ctx.lineTo(point.x + displacement.x, point.y + displacement.y);
  ctx.stroke();
}

function calcSimpleHeading(
  heading: ConcreteSimpleHeading,
  point: Point,
  tangent: Point,
  percent: number,
): Point {
  switch (heading.type) {
    case ConcreteHeadingType.Tangent:
      return tangent;
    case ConcreteHeadingType.Constant:
      return {
        x: Math.cos(heading.heading),
        y: Math.sin(heading.heading),
      };
    case ConcreteHeadingType.Linear:
      const radians = linearRangeRadians(
        heading.headings[0],
        heading.headings[1],
        percent,
      );
      return { x: Math.cos(radians), y: Math.sin(radians) };
    case ConcreteHeadingType.Point:
      return ptDiff(point, heading.heading);
    case ConcreteHeadingType.Reverse:
      // Get the target point, then flip it the other direction
      // TODO: Fix this; it only reverses for Constant, Tangent, and Point.
      // For Linear, it's supposed to go the 'other' direction.
      const lin = chkConcreteLinearHeading(heading.heading);
      let pct = lin ? -percent : percent;
      const toReverse = calcSimpleHeading(heading.heading, point, tangent, pct);
      return lin ? toReverse : ptDiff(point, ptDiff(toReverse, point));
  }
}

function CloseTo(a: number, b: number): boolean {
  return Math.abs(a - b) < 1e-7;
}

function normalizeRadian(a) {
  const result = a % (2 * Math.PI);
  return result >= 0 ? result : result + 2 * Math.PI;
}

function linearRangeRadians(
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
  if (CloseTo(s, (285 * Math.PI) / 180)) {
    console.log(s);
  }
  const flipped = !CloseTo(normalizeRadian(s + range), e);
  const target = normalizeRadian(
    s + range * (flipped ? -1 : 1) * Math.abs(percent),
  );
  const sd = Math.round((s * 180) / Math.PI);
  const ed = Math.round((e * 180) / Math.PI);
  const td = Math.round((target * 180) / Math.PI);
  const r = Math.round((180 * range) / Math.PI);
  console.log('s', sd, 'e', ed, 't', td, 'range', r);
  return target;
}
