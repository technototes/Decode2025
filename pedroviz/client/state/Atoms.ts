import { hasField } from '@freik/typechk';
import { atom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { atomWithStorage } from 'jotai/utils';
import {
  chkNamedBezier,
  chkNamedPathChain,
  chkNamedPose,
  chkNamedValue,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
} from '../../server/types';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import {
  EmptyPathChainFile,
  GetPaths,
  LoadFile,
  SetNamedBezier,
  SetNamedPathChain,
  SetNamedPose,
  SetNamedValue,
} from './API';

export const ThemeAtom = atomWithStorage<'dark' | 'light'>(
  'theme',
  'light',
  undefined,
  { getOnInit: true },
);
export const ColorsAtom = atom((get) => {
  const theme = get(ThemeAtom);
  return theme === 'dark' ? lightOnBlack : darkOnWhite;
});
export const ColorForNumber = atomFamily((index: number) =>
  atom((get) => {
    const colors = get(ColorsAtom);
    return colors[index % colors.length];
  }),
);

export const PathsAtom = atom(async () => GetPaths());

export const TeamsAtom = atom(async (get) => {
  const paths = await get(PathsAtom);
  return Object.keys(paths).sort();
});

export const SelectedTeamBackingAtom = atomWithStorage<string>(
  'selectedTeam',
  '',
  undefined,
  { getOnInit: true },
);
export const SelectedTeamAtom = atom(
  async (get) => get(SelectedTeamBackingAtom),
  async (get, set, val: string) => {
    const cur = get(SelectedTeamBackingAtom);
    // Clear the selected file when the team is changed
    if (cur !== val) {
      const curPath = await get(SelectedFileBackingAtom);
      if (curPath !== '') {
        const paths = await get(PathsAtom);
        if (hasField(paths, val)) {
          const files = paths[val];
          if (!files.includes(curPath)) {
            set(SelectedFileBackingAtom, '');
          } else {
            set(SelectedFileAtom, curPath);
          }
        }
      }
    }
    set(SelectedTeamBackingAtom, val);
  },
);

export const FilesForSelectedTeam = atom(async (get) => {
  const selTeam = await get(SelectedTeamAtom);
  const thePaths = await get(PathsAtom);
  if (selTeam === '') {
    return [];
  }
  if (hasField(thePaths, selTeam)) {
    return thePaths[selTeam];
  }
  return [];
});

export const SelectedFileBackingAtom = atomWithStorage<string>(
  'selectedPath',
  '',
  undefined,
  { getOnInit: true },
);
export const SelectedFileAtom = atom(
  async (get) => {
    return get(SelectedFileBackingAtom);
  },
  // TODO: When you set the file, udpate the file contents
  // automagically. I should be able to actually keep the
  // dependencies "correct" (and potentially much more atomic,
  // resulting in fewer UI updates hopefully)
  async (get, set, val: string) => {
    const team = await get(SelectedTeamAtom);
    set(SelectedFileBackingAtom, val);
  },
);

export const FileContentsAtom = atom(
  async (get) => {
    const team = await get(SelectedTeamAtom);
    const path = await get(SelectedFileAtom);
    if (team === '' || path === '') {
      return EmptyPathChainFile;
    }
    return LoadFile(team, path);
  },
  (_, __, val: NamedValue | NamedPose | NamedBezier | NamedPathChain) => {
    if (chkNamedValue(val)) {
      SetNamedValue(val);
    } else if (chkNamedPose(val)) {
      SetNamedPose(val);
    } else if (chkNamedBezier(val)) {
      SetNamedBezier(val);
    } else if (chkNamedPathChain(val)) {
      SetNamedPathChain(val);
    }
  },
);

export const NamedValuesAtom = atom(
  async (get) => (await get(FileContentsAtom)).values,
  async (_, set, val: Iterable<NamedValue> | NamedValue) => {
    if (chkNamedValue(val)) {
      set(FileContentsAtom, val);
    } else if (Symbol.iterator in Object(val)) {
      for (const valItem of val) {
        set(FileContentsAtom, valItem);
      }
    } else {
      throw new Error('Invalid value passed to NamedValuesAtom setter');
    }
  },
);

export const NamedPosesAtom = atom(
  async (get) => (await get(FileContentsAtom)).poses,
  (_, set, val: Iterable<NamedPose> | NamedPose) => {
    if (chkNamedPose(val)) {
      set(FileContentsAtom, val);
    } else {
      for (const posItem of val) {
        set(FileContentsAtom, posItem);
      }
    }
  },
);

export const NamedBeziersAtom = atom(
  async (get) => (await get(FileContentsAtom)).beziers,
  (_, set, val: Iterable<NamedBezier> | NamedBezier) => {
    if (chkNamedBezier(val)) {
      set(FileContentsAtom, val);
    } else {
      for (const bezItem of val) {
        set(FileContentsAtom, bezItem);
      }
    }
  },
);

export const NamedPathChainsAtom = atom(
  async (get) => (await get(FileContentsAtom)).pathChains,
  (_, set, val: Iterable<NamedPathChain> | NamedPathChain) => {
    if (chkNamedPathChain(val)) {
      set(FileContentsAtom, val);
    } else {
      for (const pathChainItem of val) {
        set(FileContentsAtom, pathChainItem);
      }
    }
  },
);

export const AllNamesAtom = atom(
  async (get) =>
    new Set<string>([
      ...(await get(NamedValuesAtom)).map((nv) => nv.name),
      ...(await get(NamedPosesAtom)).map((np) => np.name),
      ...(await get(NamedBeziersAtom)).map((nb) => nb.name),
      ...(await get(NamedPathChainsAtom)).map((npc) => npc.name),
    ]),
);
