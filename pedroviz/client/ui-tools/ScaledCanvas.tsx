import { useAtomValue } from 'jotai';
import { ReactElement, useEffect, useRef } from 'react';
import { NamedPathChain } from '../../server/types';
import { getBezierPoints } from '../state/API';
import {
  ColorsAtom,
  NamedBeziersAtom,
  NamedPathChainsAtom,
  NamedPosesAtom,
  NamedValuesAtom,
} from '../state/Atoms';
import { Point } from '../state/types';
import { bezierLength, deCasteljau } from './bezier';

const Scale = 1;
const PointRadius = 1;

const fix = 144;

export function ScaledCanvas(): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  // const curPathChainFile = useAtomValue(CurPathChainAtom);
  const pathChains = useAtomValue(NamedPathChainsAtom);
  const beziers = useAtomValue(NamedBeziersAtom);
  const poses = useAtomValue(NamedPosesAtom);
  const values = useAtomValue(NamedValuesAtom);
  const points = [
    ...pathChains
      .values()
      .map((npc: NamedPathChain) => npc.paths.map(getBezierPoints)),
  ].flat(1);

  useEffect(() => {
    // const start = performance.now();
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();

    const squareSize = Math.min(rect.width, rect.height);

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

    ctx.clearRect(0, 0, fix * Scale, fix * Scale);

    let count = 0;
    points.forEach((colorCurve) => {
      const curveControlPoints = colorCurve[1].map(([, pt]) => pt);
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
      ctx.lineWidth = 0.25;
      ctx.strokeStyle = colors[colorCurve[0] % colors.length];
      count++;
      ctx.moveTo(
        curveControlPoints[0].x * Scale,
        curveControlPoints[0].y * Scale,
      );
      for (const pt of pts) {
        ctx.lineTo(pt.x * Scale, pt.y * Scale);
      }
      ctx.lineTo(
        curveControlPoints[curveControlPoints.length - 1].x * Scale,
        curveControlPoints[curveControlPoints.length - 1].y * Scale,
      );
      ctx.stroke();
      ctx.beginPath();
      ctx.lineWidth = 0.5;
      for (const [col, pt] of colorCurve[1]) {
        ctx.strokeStyle = colors[col % colors.length];
        ctx.moveTo(pt.x + PointRadius, pt.y);
        ctx.arc(pt.x, pt.y, PointRadius, 0, 2 * Math.PI);
      }
      ctx.stroke();
      // These two items wil be usefil for animation in the footure
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
    });
    // const time = performance.now() - start;
    /*
    ctx.save();
    ctx.setTransform(dpr * scale, 0, 0, dpr * scale, 0, 0);
    ctx.font = '5px Arial'; // Set font size and family
    ctx.fillStyle = 'green'; // Set fill color for the text
    ctx.textAlign = 'center'; // Set text alignment (e.g., "start", "end", "center")
    ctx.textBaseline = 'middle'; // Set vertical alignment (e.g., "top", "middle", "bottom")
    ctx.fillText(`Time: ${time}`, 50, 20);
    ctx.restore();
    // Draw the colors
    for (let j = 0; j < darkOnWhite.length; j++) {
      ctx.save();
      ctx.beginPath()
      ctx.fillStyle = darkOnWhite[(j + 7) % darkOnWhite.length];
      ctx.fillRect(j*4, 0, 4, 4);
      ctx.stroke();
      ctx.beginPath()
      ctx.fillStyle = lightOnBlack[(j+7)% lightOnBlack.length];
      ctx.fillRect(j*4, 5, 4, 4);
      ctx.stroke();
      ctx.beginPath()
      ctx.restore();
    }
    */
  }, [pathChains, beziers, poses, values, canvasRef]);

  return <canvas className="field" ref={canvasRef} />;
}
