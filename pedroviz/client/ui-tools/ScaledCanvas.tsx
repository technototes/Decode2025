import { ReactElement, useEffect, useRef, useState } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import {
  CtrlPtRadiusAtom,
  CtrlPtThicknessAtom,
  HeadingCountAtom,
  HeadingLengthAtom,
  HeadingThicknessAtom,
  PathThicknessAtom,
  ShowBotHeadingAtom,
  ShowFieldAtom,
} from 'client/state/SavedSettings';
import { isDefined } from 'node_modules/@freik/typechk/lib/esm';

import {
  ColorsAtom,
  MappedBeziersAtom,
  MappedFileAtom,
  MappedPathChainsAtom,
  MappedPosesAtom,
  MappedValuesAtom,
} from '../state/Atoms';
import { calcBezierRef, calcFacing } from '../state/IndexedFile';
import {
  chkConcreteConstantHeading,
  chkConcreteLinearHeading,
  chkConcretePointHeading,
  chkConcreteTangentHeading,
  ConcreteHeadingType,
  Point,
} from '../types';
import { bezierLength, deCasteljau } from './bezier';

const Scale = 1;

const fix = 144;

export function ScaledCanvas(): ReactElement {
  const showField = useAtomValue(ShowFieldAtom);
  const showBotHeading = useAtomValue(ShowBotHeadingAtom);
  const ctrlPtThickness = useAtomValue(CtrlPtThicknessAtom);
  const ctrlPtRadius = useAtomValue(CtrlPtRadiusAtom);
  const pathThickness = useAtomValue(PathThicknessAtom);
  const headingCount = useAtomValue(HeadingCountAtom);
  const headingThickness = useAtomValue(HeadingThicknessAtom);
  const headingLength = useAtomValue(HeadingLengthAtom);

  const containerRef = useRef<HTMLDivElement>(null);
  const [size, setSize] = useState(0);
  const colors = useAtomValue(ColorsAtom);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const pathChains = useAtomValue(MappedPathChainsAtom);
  const beziers = useAtomValue(MappedBeziersAtom);
  const poses = useAtomValue(MappedPosesAtom);
  const values = useAtomValue(MappedValuesAtom);
  const file = useAtomValue(MappedFileAtom);
  const points = [
    ...pathChains
      .entries()
      .flatMap(([, apc]) =>
        apc.paths.map((br): [Point[], ConcreteHeadingType] => [
          calcBezierRef(br, file.container),
          calcFacing(apc.heading, file.container),
        ]),
      ),
  ];
  const showColors = false;
  const bgStyle = showField
    ? {
        backgroundImage: "url('/assets/field.png')",
      }
    : {};
  // This makes the canvas resize
  useEffect(() => {
    if (!containerRef.current) return;

    const observer = new ResizeObserver(([entry]) => {
      if (entry) {
        const { width, height } = entry.contentRect;
        // Floor the value to prevent fractional pixel jittering during fast drags
        setSize(Math.floor(Math.min(width, height)));
      }
    });

    observer.observe(containerRef.current);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas || size === 0) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const dpr = window.devicePixelRatio || 1;
    // const rect = canvas.getBoundingClientRect();

    const squareSize = size; // Math.min(rect.width, rect.height);

    canvas.width = squareSize * dpr;
    canvas.height = squareSize * dpr;
    canvas.style.width = `${squareSize}px`;
    canvas.style.height = `${squareSize}px`;

    // Map logical 144×144 units into square
    const scale = squareSize / (fix * Scale);
    // Move the origin to the lower left, corner, and scale it up
    // ctx.translate(0, canvas.height);
    // ctx.scale(dpr * scale, -dpr * scale);
    // or just a single line of code:
    ctx.setTransform(dpr * scale, 0, 0, -dpr * scale, 0, canvas.height);

    points.forEach(([ctrlPoints, facing], index) =>
      renderPath(
        ctx,
        ctrlPoints,
        showBotHeading && facing,
        colors[index % colors.length]!,
        pathThickness,
        ctrlPtRadius,
        ctrlPtThickness,
        headingCount,
        headingThickness,
        headingLength,
      ),
    );
    // Draw the colors
    if (showColors) {
      drawColors(ctx, colors);
    }
  }, [pathChains, beziers, poses, values, canvasRef, size, showBotHeading]);

  return (
    <div
      ref={containerRef}
      style={{
        flexGrow: 1,
        display: 'flex',
        width: '100%',
        height: '100%',
        justifyContent: 'end',
        alignItems: 'start',
      }}>
      {size > 0 && <canvas style={bgStyle} className="field" ref={canvasRef} />}
    </div>
  );
}

function diff(a: Point, b: Point): Point {
  return { x: b.x - a.x, y: b.y - a.y };
}

function distance(a: Point, b: Point): number {
  const delta = diff(a, b);
  return Math.sqrt(delta.x * delta.x + delta.y * delta.y);
}

function renderPath(
  ctx: CanvasRenderingContext2D,
  curveControlPoints: Point[],
  heading: ConcreteHeadingType | false,
  color: string,
  pathThickness: number,
  ctrlPtRadius: number,
  ctrlPtThickness: number,
  headingCount: number,
  headingThickness: number,
  headingLength: number,
) {
  if (curveControlPoints.length < 2) {
    return;
  }
  const len = bezierLength(curveControlPoints);
  const pts: Point[] = [];
  for (let t = 0; t <= 1.0; t += 1 / len) {
    pts.push(deCasteljau(curveControlPoints, t));
  }
  /*
      ctx.save();
      ctx.setTransform(dpr * scale, 0, 0, dpr * scale, 0, 0);
      ctx.font = '3px Arial'; // Set font size and family
      ctx.fillStyle = 'blue'; // Set fill color for the text
      ctx.textAlign = 'center'; // Set text alignment (e.g., "start", "end", "center")
      ctx.textBaseline = 'middle'; // Set vertical alignment (e.g., "top", "middle", "bottom")
      ctx.fillText(`Text${i}`, 45 + 15 * i++, fix - (80 + 5 * i));
      ctx.restore();
      */
  ctx.beginPath();
  ctx.lineWidth = pathThickness;
  ctx.strokeStyle = color;
  let approxLen = 0;
  ctx.moveTo(
    curveControlPoints[0]!.x * Scale,
    curveControlPoints[0]!.y * Scale,
  );
  let lastPt = curveControlPoints[0]!;
  for (const pt of pts) {
    approxLen += distance(lastPt, pt);
    lastPt = pt;
    ctx.lineTo(pt.x * Scale, pt.y * Scale);
  }
  approxLen += distance(
    lastPt,
    curveControlPoints[curveControlPoints.length - 1]!,
  );
  ctx.lineTo(
    curveControlPoints[curveControlPoints.length - 1]!.x * Scale,
    curveControlPoints[curveControlPoints.length - 1]!.y * Scale,
  );
  ctx.stroke();
  ctx.beginPath();
  ctx.lineWidth = ctrlPtThickness;
  for (const pt of curveControlPoints) {
    ctx.strokeStyle = color;
    ctx.moveTo((pt.x + ctrlPtRadius) * Scale, pt.y * Scale);
    ctx.arc(pt.x * Scale, pt.y * Scale, ctrlPtRadius * Scale, 0, 2 * Math.PI);
  }
  ctx.stroke();
  if (heading) {
    drawHeadingLines(
      ctx,
      color,
      approxLen,
      [...pts, curveControlPoints[curveControlPoints.length - 1]!],
      heading,
      headingCount,
      headingThickness,
      headingLength,
    );
  }

  // These two items wil be useful for animation in the footure
  /*
      const tang = bezierDerivative(curveControlPoints, 0.4);
      const mid = deCasteljau(curveControlPoints, 0.4);
      */
  /*
      ctx.beginPath();
      ctx.lineWidth = 0.1;
      ctx.strokeStyle = 'red';
      ctx.moveTo(
        mid.x * Scale - (tang.x * Scale) / 4,
        mid.y * Scale - (tang.y * Scale) / 4,
      );
      ctx.lineTo(
        mid.x * Scale + (tang.x * Scale) / 4,
        mid.y * Scale + (tang.y * Scale) / 4,
      );
      ctx.stroke();*/
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
  heading: ConcreteHeadingType,
  count: number,
  headingThickness: number,
  headingLength: number,
) {
  // for "n" points, I'm not drawing starting/ending headings, so I actually want to split
  // the length into count + 1 pieces, and find the point in between each piece
  const pieceLen = len / (count + 1);
  if (count <= 0 || pts.length < 3 || pieceLen < 1) {
    return;
  }
  let curPtIndex = 1;
  let lastDelta: Point = { x: 0, y: 0 };
  for (let pos = 0; pos < count && curPtIndex < pts.length; pos++) {
    let pathPos = pieceLen * (pos + 1);
    // Walk the length of the path, until we find the heading-draw point
    let point: Point | undefined;
    // This is just laziness. I shouldn't need to recalculate the whole thing, but
    // math is hard...
    for (let curPtIndex = 1; pathPos > 0; curPtIndex++) {
      const prev = pts[curPtIndex - 1]!;
      const cur = pts[curPtIndex]!;
      lastDelta = diff(prev, cur);
      const l = distance(prev, cur);
      if (l >= pathPos) {
        const partWay = magnitude(diff(prev, cur), pathPos);
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
        (pos + 1) / (count + 1),
        heading,
        headingThickness,
        headingLength,
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
  heading: ConcreteHeadingType,
  lineThickness: number,
  headingLength: number,
) {
  let disp: Point = { x: 0, y: 0 };
  let actualColor = color;
  if (chkConcreteTangentHeading(heading)) {
    disp = magnitude(tangent, headingLength);
    actualColor = '#777';
  } else if (chkConcreteConstantHeading(heading)) {
    disp = magnitude(
      { x: Math.cos(heading.heading), y: Math.sin(heading.heading) },
      headingLength,
    );
    actualColor = '#70f';
  } else if (chkConcreteLinearHeading(heading)) {
    const radians =
      (heading.headings[0] + (heading.headings[1] - heading.headings[0])) *
      percentage;
    disp = magnitude(
      { x: Math.cos(radians), y: Math.sin(radians) },
      headingLength,
    );
    actualColor = '#F07';
  } else if (chkConcretePointHeading(heading)) {
    disp = magnitude(diff(heading.heading, point), headingLength);
    actualColor = '#07F';
  } else {
    // We don't handle others yet...
    disp = magnitude(tangent, 0.1);
    actualColor = '#0f7';
  }
  ctx.beginPath();
  ctx.lineWidth = lineThickness;
  ctx.strokeStyle = actualColor;
  ctx.moveTo(point.x * Scale, point.y * Scale);
  ctx.lineTo((point.x + disp.x) * Scale, (point.y + disp.y) * Scale);
  ctx.stroke();
}
