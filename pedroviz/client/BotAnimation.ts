import { isUndefined } from '@freik/typechk';

import { PathChainName } from '../CodeTypes';
import { ConcreteHeading, Point } from './ConcreteEvalTypes';
import { getBezierPoints, rotPt } from './DrawingHelpers';
import { BotDrawStyle, BotShapes } from './types';

export type BotAnimationState = {
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

export function animateBot(
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
  ctx.strokeStyle = '#ff9020';
  ctx.lineWidth = 1;
  ctx.lineCap = 'round';
  drawBotShape(ctx, point, botStyle);
  nextState(state);
}

export function drawBotShape(
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
