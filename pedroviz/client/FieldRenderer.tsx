import { ReactElement, useCallback, useRef } from 'react';
import { useAtomValue } from 'jotai';

import { tokens } from '@fluentui/tokens';
import { isDefined, isUndefined } from '@freik/typechk';

import { BezierRef, ParsedClass, PathChainName, PoseRef } from '../CodeTypes';
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
import { calcBezierRef, calcFacing, calcPoseRef } from './ExpressionEval';
import {
  ColorsAtom,
  FocusedCurveAtom,
  FocusedPathAtom,
  FocusedPoseAtom,
  NamedPathChainsAtom,
  SelectedParsedClassAtom,
} from './state/Atoms';
import {
  BotDrawStyleAtom,
  CoordVizPercentAtom,
  CurveOptionsAtom,
  PathCurveOptionsAtom,
  PathHeadingCountAtom,
  PathHeadingOptionsAtom,
  PoseOptionsAtom,
  ShowPathHeadingAtom,
  ThemeAtom,
} from './state/SavedSettings';
import {
  BotDrawStyle,
  BotShapes,
  ControlPointStyle,
  CtrlPtStyles,
  CurveStyle,
  HeadingStyle,
} from './types';
import { bezierLength, deCasteljau } from './ui-tools/bezier';
import { ResponsiveSquareCanvas } from './ui-tools/ResponsiveSquareCanvas';

type BotAnimationState = {
  name: PathChainName;
  pathIndex: number;
  pathPoint: number;
  count: number;
  pathPoints: Point[][];
};

function initState(
  state: BotAnimationState,
  name: PathChainName | undefined,
  path: [Point[], ConcreteHeading][],
) {
  state.pathPoints = path.map(([pts]) => getBezierPoints(pts));
  state.count = 0;
  state.name = name || ('' as PathChainName);
  state.pathPoint = 0;
  state.pathIndex = 0;
}

const countWrap = 4;
const shapes: BotShapes[] = Object.values(BotShapes); //['rectangle', 'ellipse', 'trapezoid', 'triangle'];
function nextState(state: BotAnimationState) {
  state.count = (state.count + 1) % countWrap;
  if (state.count !== 0) {
    return;
  }
  state.pathPoint =
    (state.pathPoint + 1) % state.pathPoints[state.pathIndex]!.length;
  if (state.pathPoint !== 0) {
    return;
  }
  state.pathIndex = (state.pathIndex + 1) % state.pathPoints.length;
}

function animateBot(
  ctx: CanvasRenderingContext2D,
  name: PathChainName | undefined,
  path: [Point[], ConcreteHeading][],
  state: BotAnimationState,
  botStyle: BotDrawStyle,
) {
  if (name !== state.name) {
    // We've got a new selected path chain to draw. Let's do calculations-n-stuff
    initState(state, name, path);
  }
  // Are we in a position where the state can't be rendered?
  if (
    isUndefined(name) ||
    state.pathPoints.length <= state.pathIndex ||
    state.pathPoints[state.pathIndex]!.length <= state.pathPoint
  ) {
    return;
  }
  // Draw the robot at the current location:
  const points = state.pathPoints[state.pathIndex]!;
  const point = points[state.pathPoint]!;
  ctx.strokeStyle = '#ff0000';
  ctx.lineWidth = 1;
  ctx.lineCap = 'round';
  botStyle.Shape =
    shapes[Math.floor((state.pathPoint * 12) / points.length) % 4]!;
  drawBotShape(ctx, point, botStyle);
  nextState(state);
}

export function FieldRenderer(): ReactElement {
  const theme = useAtomValue(ThemeAtom);
  const coordViz = useAtomValue(CoordVizPercentAtom);

  const showPathHeadings = useAtomValue(ShowPathHeadingAtom);
  const pathHeadingCount = useAtomValue(PathHeadingCountAtom);
  const pathCurveStyle = useAtomValue(PathCurveOptionsAtom);
  const pathHeadingStyle = useAtomValue(PathHeadingOptionsAtom);

  const curveOpts = useAtomValue(CurveOptionsAtom);
  const poseOpts = useAtomValue(PoseOptionsAtom);
  const botStyle = useAtomValue(BotDrawStyleAtom);

  const colors = useAtomValue(ColorsAtom);
  const allPCs = useAtomValue(NamedPathChainsAtom);
  const file = useAtomValue(SelectedParsedClassAtom);

  const focusedPose = useAtomValue(FocusedPoseAtom);
  const focusedCurve = useAtomValue(FocusedCurveAtom);
  const focusedPath = useAtomValue(FocusedPathAtom);

  const concretePaths = new Map(
    allPCs.map((npc) => [
      npc.name,
      npc.paths.map((br): [Point[], ConcreteHeading] => [
        calcBezierRef(br, file),
        calcFacing(npc.heading, file),
      ]),
    ]),
  );

  const points = [...concretePaths.values()].flatMap((val) => val);
  /*allPCs.flatMap((npc) =>
    npc.paths.map((br): [Point[], ConcreteHeading] => [
      calcBezierRef(br, file),

      calcFacing(npc.heading, file),
    ]),
  );*/

  const renderField = useCallback(
    (ctx: CanvasRenderingContext2D, dpr: number) => {
      // Map logical 144×144 units into square
      const size = ctx.canvas.width;
      const scale = size / 144;

      // Move the origin to the lower left, corner, and scale it up
      // ctx.translate(0, size * dpr);
      // ctx.scale(dpr * scale, -dpr * scale);
      // or just a single line of code:
      ctx.setTransform(scale, 0, 0, -scale, 0, size);
      ctx.globalAlpha = coordViz;
      renderCoordinateLegend(ctx, 1, scale, theme);
      ctx.globalAlpha = 1.0;

      points.forEach(([ctrlPoints, facing], index) =>
        renderPath(
          ctx,
          ctrlPoints,
          showPathHeadings ? facing : false,
          colors[index % colors.length]!,
          pathHeadingCount,
          pathHeadingStyle,
          pathCurveStyle,
        ),
      );

      if (isDefined(focusedPose)) {
        drawFocusedPose(poseOpts, ctx, focusedPose.pose, file);
      }
      if (isDefined(focusedCurve)) {
        drawFocusedCurve(curveOpts, ctx, focusedCurve.points, file);
      }
    },
    [
      coordViz,
      theme,
      showPathHeadings,
      pathHeadingCount,
      pathHeadingStyle,
      pathCurveStyle,
      focusedCurve,
      focusedPose,
      poseOpts,
      curveOpts,
      allPCs,
      colors,
      points,
      focusedPose,
      focusedCurve,
      file,
    ],
  );

  const animationStateRef = useRef<BotAnimationState>({
    name: '' as PathChainName,
    pathIndex: 0,
    pathPoint: 0,
    count: 60,
    pathPoints: [],
  });
  const animate = useCallback(
    (ctx: CanvasRenderingContext2D, dpr: number) => {
      const size = ctx.canvas.width;
      const scale = size / 144;
      const selectedConcretePath = concretePaths.get(focusedPath!.name);
      if (!selectedConcretePath) {
        return;
      }
      ctx.save();
      ctx.setTransform(scale, 0, 0, -scale, 0, size);
      animateBot(
        ctx,
        focusedPath?.name,
        selectedConcretePath,
        animationStateRef.current,
        botStyle,
      );
      ctx.restore();
    },
    [focusedPath],
  );

  return (
    <ResponsiveSquareCanvas
      anchor={{ x: 'right', y: 'top' }}
      render={renderField}
      animate={focusedPath && animate}
    />
  );
}

function drawFocusedCurve(
  opts: CurveStyle,
  ctx: CanvasRenderingContext2D,
  focusedCurve: BezierRef,
  file: ParsedClass,
) {
  const br = calcBezierRef(focusedCurve, file);
  renderCurve(ctx, br, '#fff', opts);
}

function drawFocusedPose(
  poseStyle: { Points: ControlPointStyle; Headings: HeadingStyle },
  ctx: CanvasRenderingContext2D,
  focusedPose: PoseRef,
  file: ParsedClass,
) {
  const pt = calcPoseRef(focusedPose, file);
  ctx.beginPath();
  ctx.strokeStyle = tokens.colorNeutralForeground1; // TODO: Update this
  drawPoint(ctx, pt, poseStyle.Points);
  ctx.stroke();
  if (isDefined(pt.h)) {
    drawHeadingLine(
      ctx,
      poseStyle.Headings,
      tokens.colorNeutralForeground1,
      pt,
      {
        x: Math.cos(pt.h),
        y: Math.sin(pt.h),
      },
    );
  }
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

function renderCurve(
  ctx: CanvasRenderingContext2D,
  curveControlPoints: Point[],
  color: string,
  opts: CurveStyle,
): [number, Point[]] {
  if (curveControlPoints.length < 2) {
    return [0, []];
  }
  const pts: Point[] = getBezierPoints(curveControlPoints);
  const drawPath = opts.Thickness > 1e-10;
  if (drawPath) {
    ctx.beginPath();
  }
  ctx.lineWidth = opts.Thickness;
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
  return [approxLen, pts];
}

function getBezierPoints(curveControlPoints: Point[]) {
  const len = bezierLength(curveControlPoints);
  const pts: Point[] = [];
  for (let t = 0; t <= 1.0; t += 1 / len) {
    const h = t * Math.PI * 2;
    pts.push({ ...deCasteljau(curveControlPoints, t), h });
  }
  return pts;
}

function renderPath(
  ctx: CanvasRenderingContext2D,
  curveControlPoints: Point[],
  heading: ConcreteHeading | false,
  color: string,
  headingCount: number,
  headingStyle: HeadingStyle,
  curveStyle: CurveStyle,
) {
  const [approxLen, pts] = renderCurve(
    ctx,
    curveControlPoints,
    color,
    curveStyle,
  );
  if (
    curveStyle.ControlPoint.Style != 'z' &&
    curveStyle.ControlPoint.Size > 1e-10 &&
    curveStyle.ControlPoint.Thickness > 1e-10
  ) {
    drawControlPoints(ctx, curveStyle.ControlPoint, curveControlPoints, color);
  }
  if (heading) {
    drawHeadingLines(
      ctx,
      color,
      approxLen,
      [...pts, curveControlPoints[curveControlPoints.length - 1]!],
      heading,
      headingCount,
      headingStyle,
    );
  }

  // These two items wil be useful for animation in the footure
  /*
      const tang = bezierDerivative(curveControlPoints, 0.4);
      const mid = deCasteljau(curveControlPoints, 0.4);
      */
}

function rotPt(offs: Point): Point {
  const cos = Math.cos(offs.h!);
  const sin = Math.sin(offs.h!);
  const x = offs.x * cos - offs.y * sin;
  const y = offs.x * sin + offs.y * cos;
  return { x, y };
}

function drawBotShape(
  ctx: CanvasRenderingContext2D,
  center: Point,
  bot: BotDrawStyle,
) {
  const w = Math.min(bot.Width, 9);
  const l = Math.min(bot.Depth, 9);
  const to = w / 5; // trapezoid offset
  ctx.beginPath();
  const front = rotPt({ x: l, y: 0, h: center.h! });
  const br = rotPt({ x: -l, y: -w, h: center.h });
  const bl = rotPt({ x: -l, y: w, h: center.h });

  switch (bot.Shape) {
    case BotShapes.Rectangle:
      {
        const fl = rotPt({ x: l, y: w, h: center.h });
        const fr = rotPt({ x: l, y: -w, h: center.h });
        ctx.moveTo(center.x + fl.x, center.y + fl.y);
        ctx.lineTo(center.x + fr.x, center.y + fr.y);
        ctx.lineTo(center.x + br.x, center.y + br.y);
        ctx.lineTo(center.x + bl.x, center.y + bl.y);
        ctx.closePath();
      }
      break;
    case BotShapes.Trapezoid:
      {
        const fl = rotPt({ x: l, y: w - to, h: center.h });
        const fr = rotPt({ x: l, y: -w + to, h: center.h });
        ctx.moveTo(center.x + fl.x, center.y + fl.y);
        ctx.lineTo(center.x + fr.x, center.y + fr.y);
        ctx.lineTo(center.x + br.x, center.y + br.y);
        ctx.lineTo(center.x + bl.x, center.y + bl.y);
        ctx.closePath();
      }
      break;
    case BotShapes.Ellipse:
      ctx.ellipse(center.x, center.y, l, w, center.h!, 0, Math.PI * 2);
      break;
    case BotShapes.Triangle:
      ctx.moveTo(center.x + front.x, center.y + front.y);
      ctx.lineTo(center.x + br.x, center.y + br.y);
      ctx.lineTo(center.x + bl.x, center.y + bl.y);
      ctx.lineTo(center.x + front.x, center.y + front.y);
  }
  ctx.moveTo(center.x + front.x, center.y + front.y);
  ctx.lineTo(center.x, center.y);
  ctx.stroke();
}

function drawPoint(
  ctx: CanvasRenderingContext2D,
  pt: Point,
  style: ControlPointStyle,
) {
  ctx.lineWidth = style.Thickness;
  const half = style.Size / 2;
  const shape = style.Style;
  switch (shape) {
    case CtrlPtStyles.Circle:
      ctx.moveTo(pt.x + half, pt.y);
      ctx.arc(pt.x, pt.y, half, 0, 2 * Math.PI);
      break;
    case CtrlPtStyles.Square:
      ctx.rect(pt.x - half, pt.y - half, style.Size, style.Size);
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

function drawControlPoints(
  ctx: CanvasRenderingContext2D,
  opts: ControlPointStyle,
  curveControlPoints: Point[],
  color: string,
) {
  ctx.beginPath();
  ctx.strokeStyle = color;
  for (const pt of curveControlPoints) {
    drawPoint(ctx, pt, opts);
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
  headingCount: number,
  headingStyle: HeadingStyle,
) {
  // for "n" points, I'm not drawing starting/ending headings, so I actually want to split
  // the length into count + 1 pieces, and find the point in between each piece
  const pieceLen = len / (headingCount + 1);
  if (headingCount <= 0 || pts.length < 3 || pieceLen < 1) {
    return;
  }
  let curPtIndex = 1;
  let lastDelta: Point = { x: 0, y: 0 };
  for (let pos = 0; pos < headingCount && curPtIndex < pts.length; pos++) {
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
      drawPathHeadingLine(
        ctx,
        color,
        point,
        lastDelta,
        (pos + 1) / (headingCount + 1),
        heading,
        headingStyle,
      );
    }
  }
}

function drawPathHeadingLine(
  ctx: CanvasRenderingContext2D,
  color: string,
  point: Point,
  tangent: Point,
  percentage: number,
  heading: ConcreteHeading,
  opts: HeadingStyle,
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
  drawHeadingLine(ctx, opts, color, point, targetPoint);
}

function drawHeadingLine(
  ctx: CanvasRenderingContext2D,
  style: HeadingStyle,
  color: string,
  point: Point,
  targetPoint: Point,
) {
  ctx.beginPath();
  ctx.lineCap = 'round';
  ctx.lineWidth = style.Thickness;
  ctx.strokeStyle = color;
  ctx.moveTo(point.x, point.y);
  const displacement = magnitude(targetPoint, style.Length);
  const endx = point.x + displacement.x;
  const endy = point.y + displacement.y;
  ctx.lineTo(endx, endy);
  ctx.stroke();
  // Draw a little arrow point:
  const angle = Math.atan2(displacement.y, displacement.x);
  const headSize = style.Length * style.ArrowPercent;
  ctx.beginPath();
  ctx.lineCap = 'square';
  ctx.moveTo(
    endx - headSize * Math.cos(angle - style.ArrowAngle),
    endy - headSize * Math.sin(angle - style.ArrowAngle),
  );
  ctx.lineTo(endx, endy);
  ctx.lineTo(
    endx - headSize * Math.cos(angle + style.ArrowAngle),
    endy - headSize * Math.sin(angle + style.ArrowAngle),
  );
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
      // Get the target point, then flip it the other direction, unless it's linear.
      // For Linear, it travels the opposite direction of the normal linear heading.
      const lin = chkConcreteLinearHeading(heading.heading);
      let pct = lin ? -percent : percent;
      const toReverse = calcSimpleHeading(heading.heading, point, tangent, pct);
      return lin ? toReverse : ptDiff(point, ptDiff(toReverse, point));
  }
}

function CloseTo(a: number, b: number): boolean {
  return Math.abs(a - b) < 1e-7;
}

function normalizeRadian(a: number) {
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
  const flipped = !CloseTo(normalizeRadian(s + range), e);
  const target = normalizeRadian(
    s + range * (flipped ? -1 : 1) * Math.abs(percent),
  );
  return target;
}
