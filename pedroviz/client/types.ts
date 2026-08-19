import { CSSProperties } from 'react';

import {
  AnonymousPathChain,
  BezierName,
  BezierRef,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../CodeTypes';
import { PathDatabase } from '../IpcTypes';

export const CtrlPtStyles = Object.freeze({
  Circle: 'o',
  Triangle: 't',
  Square: 's',
  Crosshair: '+',
  X: 'x',
  None: 'z',
} as const);
export type CtrlPtStyles = (typeof CtrlPtStyles)[keyof typeof CtrlPtStyles];

export type ControlPointStyle = {
  Thickness: number;
  Size: number;
  Style: CtrlPtStyles;
};

export type HeadingStyle = {
  Length: number;
  Thickness: number;
  ArrowAngle: number;
  ArrowPercent: number;
};

export type CurveStyle = {
  Thickness: number;
  ShowPoints: boolean;
  ControlPoint: ControlPointStyle;
};

export type PathStyle = {
  HeadingCount: number;
  Heading: HeadingStyle;
  Curves: CurveStyle;
};

export const BotShapes = Object.freeze({
  Trapezoid: 'Trapezoid',
  Triangle: 'Triangle',
  Ellipse: 'Ellipse',
  Rectangle: 'Rectangle',
} as const);
export type BotShapes = (typeof BotShapes)[keyof typeof BotShapes];

export type BotDrawStyle = {
  Shape: BotShapes;
  Width: number;
  Depth: number;
};

export type DisplayOptions = {
  GranularSettings: boolean;
  FieldVisibility: number;
  CoordinateVisibility: number;
  DarkMode: boolean;
  Poses: {
    Points: ControlPointStyle;
    Headings: HeadingStyle;
  };
  Curves: CurveStyle;
  Paths: PathStyle;
  BotDrawing: BotDrawStyle;
};

export type OneFileIndex = {
  container: ParsedClass;
  namedValues: Map<ValueName, ValueRef | RadiansRef>;
  namedPoses: Map<PoseName, PoseRef>;
  namedBeziers: Map<BezierName, BezierRef>;
  namedPathChains: Map<PathChainName, AnonymousPathChain>;
  staticShortcuts: Map<string, string>;
};

export type NameLookup = {
  findValue: (
    val: ValueName,
    context: ParsedClass,
  ) => ValueRef | RadiansRef | undefined;
  findPose: (pose: PoseName, context: ParsedClass) => PoseRef | undefined;
  findBezier: (bez: BezierName, context: ParsedClass) => BezierRef | undefined;
  findPath: (
    pc: PathChainName,
    context: ParsedClass,
  ) => AnonymousPathChain | undefined;
  setDb: (db: PathDatabase) => void;
  db: () => PathDatabase | undefined;
};

export type HasItem<T> = {
  has: (item: T) => boolean;
};

export type HasKeys<T> = HasItem<T> & {
  keys: () => Iterable<T>;
};

export const ValidateState = Object.freeze({
  Error: 'error',
  Warning: 'warning',
  Success: 'success',
  None: 'none',
} as const);
export type ValidationState =
  (typeof ValidateState)[keyof typeof ValidateState];

export type ValidationData = {
  message: string;
  state: ValidationState;
};

export const ValidData: ValidationData = Object.freeze({
  message: '',
  state: ValidateState.None,
} as const);

export function ValidationResult(
  message: string,
  state: ValidationState,
): ValidationData {
  return { message, state };
}

export type ResponsiveAnchor = {
  x: 'left' | 'center' | 'right';
  y: 'top' | 'middle' | 'bottom';
};

export type CanvasRenderFunc = (
  ctx: CanvasRenderingContext2D,
  devicePixelRatio: number,
) => void;

export type ResponsiveSquareCanvasProps = {
  anchor?: ResponsiveAnchor;
  style?: CSSProperties;
  className?: string;
  render: CanvasRenderFunc;
  animate?: CanvasRenderFunc;
};

export type RowData = { offset: number; size: number };
