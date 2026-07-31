import { atom, useAtom, useAtomValue } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

import { CtrlPtStyles, PathRenderOptions } from 'client/types';

export const ThemeAtom = atomWithStorage<'dark' | 'light'>(
  'theme',
  'light',
  undefined,
  { getOnInit: true },
);

export const PathRenderOptionsAtom = atomWithStorage<PathRenderOptions>(
  'PathRenderOptions',
  {
    ShowField: true,
    PathThickness: 0.1,
    ShowCoords: true,
    Heading: {
      Display: true,
      Count: 6,
      Length: 5,
      Thickness: 0.5,
    },
    ControlPoint: {
      Thickness: 0.4,
      Size: 2,
      Style: CtrlPtStyles.Circle,
    },
  },
  undefined,
  { getOnInit: true },
);

export const CtrlPtSizeAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.ControlPoint.Size;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.ControlPoint.Size = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const CtrlPtStyleAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.ControlPoint.Style;
  },
  (get, set, val: CtrlPtStyles) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.ControlPoint.Style = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const CtrlPtThicknessAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.ControlPoint.Thickness;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.ControlPoint.Thickness = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const ShowBotHeadingAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.Heading.Display;
  },
  (get, set, val: boolean) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.Heading.Display = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const HeadingCountAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.Heading.Count;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.Heading.Count = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const HeadingLengthAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.Heading.Length;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.Heading.Length = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const HeadingThicknessAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.Heading.Thickness;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.Heading.Thickness = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const PathThicknessAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.PathThickness;
  },
  (get, set, val: number) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.PathThickness = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const ShowFieldAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.ShowField;
  },
  (get, set, val: boolean) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.ShowField = val;
    set(PathRenderOptionsAtom, newVal);
  },
);

export const ShowFieldKeyAtom = atom(
  (get) => {
    const opts = get(PathRenderOptionsAtom);
    return opts.ShowCoords;
  },
  (get, set, val: boolean) => {
    const newVal = structuredClone(get(PathRenderOptionsAtom));
    newVal.ShowCoords = val;
    set(PathRenderOptionsAtom, newVal);
  },
);
