import { hasField } from '@freik/typechk';
import { atom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { atomWithStorage } from 'jotai/utils';
import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  chkAnonymousBezier,
  chkAnonymousPose,
  chkAnonymousValue,
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
  GetPaths,
  LoadFile,
  namedBeziers,
  namedPathChains,
  namedPoses,
  namedValues,
} from './API';

export const ThemeAtom = atomWithStorage<'dark' | 'light'>('theme', 'light');
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
export const SelectedTeamBackingAtom = atom('');
export const SelectedTeamAtom = atom(
  async (get) => get(SelectedTeamBackingAtom),
  (get, set, val) => {
    const cur = get(SelectedTeamBackingAtom);
    // Clear the selected file when the team is changed
    if (cur !== val) {
      set(SelectedFileBackingAtom, '');
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

export const SelectedFileBackingAtom = atom('');
export const SelectedFileAtom = atom(
  async (get) => {
    return get(SelectedFileBackingAtom);
  },
  // TODO: When you set the file, udpate the file contents
  // automagically. I should be able to actually keep the
  // dependencies "correct" (and potentially much more atomic,
  // resulting in fewer UI updates hopefully)
  async (get, set, val: string) => {
    const prev = get(SelectedFileBackingAtom);
    const team = await get(SelectedTeamAtom);
    set(SelectedFileBackingAtom, val);
    if (val !== prev && val !== '') {
      // TODO: clear any AtomFamiliy cache
      const pcf = await LoadFile(team, val);
      // Set all teh names
      set(NamedValuesAtom, pcf.values);
      set(NamedPosesAtom, pcf.poses);
      set(NamedBeziersAtom, pcf.beziers);
      set(NamedPathChainsAtom, pcf.pathChains);
    }
  },
);

const NamedValuesBackerAtom = atom<Map<string, NamedValue>>(new Map());
export const NamedValuesAtom = atom(
  (get) => get(NamedValuesBackerAtom),
  (get, set, val: Iterable<NamedValue> | NamedValue) => {
    if (chkNamedValue(val)) {
      const nvba = new Map(get(NamedValuesBackerAtom));
      nvba.set(val.name, val);
      set(NamedValuesBackerAtom, nvba);
      namedValues.set(val.name, val);
    } else if (Symbol.iterator in Object(val)) {
      const nv = new Map<string, NamedValue>();
      for (const valItems of val) {
        nv.set(valItems.name, valItems);
      }
      set(NamedValuesBackerAtom, nv);
      namedValues.clear();
      nv.forEach((n) => namedValues.set(n.name, n));
    } else {
      throw new Error('Invalid value passed to NamedValuesAtom setter');
    }
  },
);
export const ValueNamesAtom = atom((get) => [
  ...get(NamedValuesBackerAtom).keys(),
]);
export const ValueAtomFor = atomFamily((name: string) =>
  atom(
    (get) => get(NamedValuesBackerAtom).get(name),
    (_, set, args: NamedValue | AnonymousValue) => {
      set(
        NamedValuesAtom,
        chkAnonymousValue(args) ? { name, value: args } : args,
      );
    },
  ),
);

const NamedPosesBackerAtom = atom<Map<string, NamedPose>>(new Map());
export const NamedPosesAtom = atom(
  (get) => get(NamedPosesBackerAtom),
  (get, set, val: Iterable<NamedPose> | NamedPose) => {
    if (chkNamedPose(val)) {
      const npba = new Map(get(NamedPosesBackerAtom));
      npba.set(val.name, val);
      set(NamedPosesBackerAtom, npba);
      namedPoses.set(val.name, val);
    } else {
      const np = new Map([...val].map((val) => [val.name, val]));
      set(NamedPosesBackerAtom, np);
      namedPoses.clear();
      np.forEach((p) => namedPoses.set(p.name, p));
    }
  },
);
export const PoseNamesAtom = atom((get) => [
  ...get(NamedPosesBackerAtom).keys(),
]);
export const PoseAtomFor = atomFamily((name: string) =>
  atom(
    (get) => get(NamedPosesAtom).get(name),
    (_, set, args: NamedPose | AnonymousPose) =>
      set(NamedPosesAtom, chkAnonymousPose(args) ? { name, pose: args } : args),
  ),
);

export const NamedBeziersBackerAtom = atom<Map<string, NamedBezier>>(new Map());
export const NamedBeziersAtom = atom(
  (get) => get(NamedBeziersBackerAtom),
  (get, set, val: Iterable<NamedBezier> | NamedBezier) => {
    if (chkNamedBezier(val)) {
      const nbba = new Map(get(NamedBeziersBackerAtom));
      nbba.set(val.name, val);
      set(NamedBeziersBackerAtom, nbba);
      namedBeziers.set(val.name, val);
    } else {
      const nb = new Map([...val].map((val) => [val.name, val]));
      set(NamedBeziersBackerAtom, nb);
      namedBeziers.clear();
      nb.forEach((b) => namedBeziers.set(b.name, b));
    }
  },
);
export const BezierNamesAtom = atom((get) => [
  ...get(NamedBeziersBackerAtom).keys(),
]);
export const BezierAtomFor = atomFamily((name: string) =>
  atom(
    (get) => get(NamedBeziersAtom).get(name),
    (_, set, args: NamedBezier | AnonymousBezier) =>
      set(
        NamedBeziersAtom,
        chkAnonymousBezier(args) ? { name, points: args } : args,
      ),
  ),
);

export const NamedPathChainsBackerAtom = atom<Map<string, NamedPathChain>>(
  new Map(),
);
export const NamedPathChainsAtom = atom(
  (get) => get(NamedPathChainsBackerAtom),
  (get, set, val: Iterable<NamedPathChain> | NamedPathChain) => {
    if (chkNamedPathChain(val)) {
      const npcba = new Map(get(NamedPathChainsBackerAtom));
      npcba.set(val.name, val);
      set(NamedPathChainsBackerAtom, npcba);
      namedPathChains.set(val.name, val);
    } else {
      const npc = new Map([...val].map((val) => [val.name, val]));
      set(NamedPathChainsBackerAtom, npc);
      namedPathChains.clear();
      npc.forEach((pc) => namedPathChains.set(pc.name, pc));
    }
  },
);
export const PathChainNamesAtom = atom((get) =>
  get(NamedPathChainsAtom).keys(),
);
export const PathChainAtomFor = atomFamily((name: string) =>
  atom(
    (get) => get(NamedPathChainsAtom).get(name),
    (_, set, args: NamedPathChain) => set(NamedPathChainsAtom, args),
  ),
);

export const AllNamesAtom = atom<Set<string>>(
  (get) =>
    new Set<string>([
      ...get(ValueNamesAtom),
      ...get(PoseNamesAtom),
      ...get(BezierNamesAtom),
      ...get(PathChainNamesAtom),
    ]),
);
