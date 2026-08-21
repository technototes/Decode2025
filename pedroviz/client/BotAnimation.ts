import { isDefined, isUndefined } from '@freik/typechk';

import { PathChainName } from '../CodeTypes';
import { ConcreteHeading, Point } from './ConcreteEvalTypes';
import {
  addHeadingToCurve,
  CurveDetail,
  getCurveDetail,
  rotPt,
} from './DrawingHelpers';
import { BotDrawStyle, BotShapes } from './types';

export type BotAnimationState = {
  name: PathChainName;
  curveIndex: number;
  curvePoint: number;
  count: number;
  curves: CurveDetail[];
};

const countDelay = 1;

function initState(
  state: BotAnimationState,
  name: PathChainName | undefined,
  path: [Point[], ConcreteHeading][],
) {
  state.curves = path.map(([pts, ch]) => {
    const cd = getCurveDetail(pts);
    addHeadingToCurve(cd, ch);
    return cd;
  });
  state.count = 0;
  state.name = name || ('' as PathChainName);
  state.curvePoint = 0;
  state.curveIndex = 0;
}

function nextState(state: BotAnimationState) {
  state.count = (state.count + 1) % countDelay;
  if (state.count !== 0) {
    return;
  }
  state.curvePoint =
    (state.curvePoint + 1) % state.curves[state.curveIndex]!.points.length;
  if (state.curvePoint !== 0) {
    return;
  }
  state.curveIndex = (state.curveIndex + 1) % state.curves.length;
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
    state.curves.length <= state.curveIndex ||
    state.curves[state.curveIndex]!.length <= state.curvePoint
  ) {
    return;
  }
  // Draw the robot at the current location:
  const points = state.curves[state.curveIndex]!.points;
  const point = points[state.curvePoint];
  if (!isDefined(point)) {
    console.log('Index', state.curveIndex, 'item', state.curvePoint);
  } else {
    ctx.strokeStyle = '#ff9020';
    ctx.lineWidth = 1;
    ctx.lineCap = 'round';
    drawBotShape(ctx, point, botStyle);
  }
  nextState(state);
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
