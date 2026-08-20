import { atom } from 'jotai';
import { focusAtom } from 'jotai-optics';
import { atomWithStorage } from 'jotai/utils';

import {
  BotDrawStyle,
  BotShapes,
  ControlPointStyle,
  CtrlPtStyles,
  CurveStyle,
  DisplayOptions,
  HeadingStyle,
  PathStyle,
} from '../types';

const defaultPathStyle: PathStyle = {
  HeadingCount: 6,
  Heading: {
    Length: 5,
    Thickness: 0.5,
    ArrowAngle: 0.31416,
    ArrowPercent: 0.15,
  },

  Curves: {
    Thickness: 0.2,
    ShowPoints: true,
    ControlPoint: {
      Thickness: 0.4,
      Size: 2,
      Style: CtrlPtStyles.Circle,
    },
  },
};

const defaultBotStyle: BotDrawStyle = {
  Shape: BotShapes.Trapezoid,
  Width: 16 / 2,
  Depth: 18 / 2,
};

function getContrastingStyle(s: CtrlPtStyles): CtrlPtStyles {
  switch (s) {
    case CtrlPtStyles.Crosshair:
    case CtrlPtStyles.X:
      return CtrlPtStyles.Square;
    default:
      return CtrlPtStyles.Crosshair;
  }
}

function autoControlPoint(opts: ControlPointStyle): ControlPointStyle {
  return {
    Thickness: opts.Thickness * 2,
    Size: opts.Size * 2.5,
    Style: getContrastingStyle(opts.Style),
  };
}

function autoHeading(opts: HeadingStyle): HeadingStyle {
  return {
    Length: opts.Length * 1.5,
    Thickness: opts.Thickness * 2,
    ArrowAngle: opts.ArrowAngle,
    ArrowPercent: opts.ArrowPercent,
  };
}

function autoCurves(opts: CurveStyle): CurveStyle {
  return {
    Thickness: opts.Thickness * 2,
    ShowPoints: opts.ShowPoints,
    ControlPoint: autoControlPoint(opts.ControlPoint),
  };
}

export const DisplayOptionsAtom = atomWithStorage<DisplayOptions>(
  'DisplayOptions',
  {
    GranularSettings: false,
    FieldVisibility: 1.0,
    CoordinateVisibility: 1.0,
    DarkMode: true,
    Poses: {
      Points: autoControlPoint(defaultPathStyle.Curves.ControlPoint),
      Headings: autoHeading(defaultPathStyle.Heading),
    },
    Curves: autoCurves(defaultPathStyle.Curves),
    Paths: defaultPathStyle,
    BotDrawing: defaultBotStyle,
  },
  undefined,
  { getOnInit: true },
);

export type ThemeType = 'dark' | 'light';

const DarkThemeAtom = focusAtom(DisplayOptionsAtom, (optic) =>
  optic.prop('DarkMode'),
);
export const ThemeAtom = atom<ThemeType, [ThemeType], void>(
  (get) => (get(DarkThemeAtom) ? 'dark' : 'light'),
  (_get, set, val: ThemeType) => set(DarkThemeAtom, val === 'dark'),
);
const RawFieldVisibilityAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('FieldVisibility'),
);
export const FieldVisibilityAtom = atom(
  (get) => 100 * get(RawFieldVisibilityAtom),
  (_, set, val: number) => {
    set(RawFieldVisibilityAtom, Math.max(0, Math.min(1, val / 100)));
  },
);
export const FieldVizPercentAtom = atom((get) => get(RawFieldVisibilityAtom));

const RawCoordinateVisibilityAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('CoordinateVisibility'),
);
export const CoordinateVisibilityAtom = atom(
  (get) => 100 * get(RawCoordinateVisibilityAtom),
  (_, set, val: number) => {
    set(RawCoordinateVisibilityAtom, Math.max(0, Math.min(1, val / 100)));
  },
);
export const CoordVizPercentAtom = atom((get) =>
  get(RawCoordinateVisibilityAtom),
);

export const GranularSettingsAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('GranularSettings'),
);
export const PathOptionsAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('Paths'),
);
export const PathCurveOptionsAtom = focusAtom(PathOptionsAtom, (o) =>
  o.prop('Curves'),
);
const PathPointOptionsAtom = focusAtom(PathCurveOptionsAtom, (o) =>
  o.prop('ControlPoint'),
);
export const PathHeadingOptionsAtom = focusAtom(PathOptionsAtom, (o) =>
  o.prop('Heading'),
);
export const PathPointSizeAtom = focusAtom(PathPointOptionsAtom, (o) =>
  o.prop('Size'),
);
export const PathPointStyleAtom = focusAtom(PathPointOptionsAtom, (o) =>
  o.prop('Style'),
);
export const PathPointThicknessAtom = focusAtom(PathPointOptionsAtom, (o) =>
  o.prop('Thickness'),
);
export const PathThicknessAtom = focusAtom(PathCurveOptionsAtom, (o) =>
  o.prop('Thickness'),
);

export const PathHeadingCountAtom = focusAtom(PathOptionsAtom, (o) =>
  o.prop('HeadingCount'),
);
export const PathHeadingLengthAtom = focusAtom(PathHeadingOptionsAtom, (o) =>
  o.prop('Length'),
);
export const PathHeadingThicknessAtom = focusAtom(PathHeadingOptionsAtom, (o) =>
  o.prop('Thickness'),
);
export const ShowPathHeadingAtom = atom(
  (get) => get(PathHeadingCountAtom) > 0,
  (get, set, val: boolean) => {
    const hc = get(PathHeadingCountAtom);
    if (val === hc > 0) return;
    set(PathHeadingCountAtom, Math.abs(hc) * (val ? 1 : -1));
  },
);
const RawCurveOptionsAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('Curves'),
);
export const CurveOptionsAtom = atom(
  (get) => {
    return get(GranularSettingsAtom)
      ? get(RawCurveOptionsAtom)
      : autoCurves(get(PathCurveOptionsAtom));
  },
  (get, set, val: CurveStyle) => {
    const opts = structuredClone(get(DisplayOptionsAtom));
    opts.Curves = val;
    set(RawCurveOptionsAtom, val);
  },
);

const RawCurveThicknessAtom = focusAtom(RawCurveOptionsAtom, (o) =>
  o.prop('Thickness'),
);
const RawCurveShowPointAtom = focusAtom(RawCurveOptionsAtom, (o) =>
  o.prop('ShowPoints'),
);
const RawCurvePointSizeAtom = focusAtom(RawCurveOptionsAtom, (o) =>
  o.prop('ControlPoint').prop('Size'),
);
const RawCurvePointStyleAtom = focusAtom(RawCurveOptionsAtom, (o) =>
  o.prop('ControlPoint').prop('Style'),
);
const RawCurvePointThicknessAtom = focusAtom(RawCurveOptionsAtom, (o) =>
  o.prop('ControlPoint').prop('Thickness'),
);

const RawPoseOptionsAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('Poses'),
);

export const PoseOptionsAtom = atom(
  (get) => {
    return get(GranularSettingsAtom)
      ? get(RawPoseOptionsAtom)
      : {
          Points: autoControlPoint(get(PathPointOptionsAtom)),
          Headings: autoHeading(get(PathHeadingOptionsAtom)),
        };
  },
  (get, set, val: CurveStyle) => {
    const opts = structuredClone(get(DisplayOptionsAtom));
    opts.Curves = val;
    set(RawCurveOptionsAtom, val);
  },
);
const RawPosePointSizeAtom = focusAtom(RawPoseOptionsAtom, (o) =>
  o.prop('Points').prop('Size'),
);
const RawPosePointStyleAtom = focusAtom(RawPoseOptionsAtom, (o) =>
  o.prop('Points').prop('Style'),
);
const RawPosePointThicknessAtom = focusAtom(RawPoseOptionsAtom, (o) =>
  o.prop('Points').prop('Thickness'),
);
const RawPoseHeadingLengthAtom = focusAtom(RawPoseOptionsAtom, (o) =>
  o.prop('Headings').prop('Length'),
);
const RawPoseHeadingThicknessAtom = focusAtom(RawPoseOptionsAtom, (o) =>
  o.prop('Headings').prop('Thickness'),
);

export const BotDrawStyleAtom = focusAtom(DisplayOptionsAtom, (o) =>
  o.prop('BotDrawing'),
);
