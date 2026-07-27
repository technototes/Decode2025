import { atomWithStorage } from 'node_modules/jotai/esm/utils.mjs';

export const ThemeAtom = atomWithStorage<'dark' | 'light'>(
  'theme',
  'light',
  undefined,
  { getOnInit: true },
);

export const ShowFieldAtom = atomWithStorage<boolean>(
  'ShowField',
  true,
  undefined,
  { getOnInit: true },
);

export const ShowBotHeadingAtom = atomWithStorage<boolean>(
  'ShowBotHeading',
  true,
  undefined,
  { getOnInit: true },
);

export const PathThicknessAtom = atomWithStorage<number>(
  'PathThickness',
  0.1,
  undefined,
  { getOnInit: true },
);

export const CtrlPtRadiusAtom = atomWithStorage<number>(
  'CtrlPtRadius',
  1,
  undefined,
  { getOnInit: true },
);

export const CtrlPtThicknessAtom = atomWithStorage<number>(
  'CtrlPtThicknss',
  1,
  undefined,
  { getOnInit: true },
);

export const HeadingCountAtom = atomWithStorage<number>(
  'HeadingCount',
  6,
  undefined,
  { getOnInit: true },
);

export const HeadingThicknessAtom = atomWithStorage<number>(
  'HeadingThickness',
  0.5,
  undefined,
  { getOnInit: true },
);

export const HeadingLengthAtom = atomWithStorage<number>(
  'HeadingLength',
  5,
  undefined,
  { getOnInit: true },
);
