import { hasField } from '@freik/typechk';
import { atom, WritableAtom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { focusAtom } from 'jotai-optics';
import { atomWithStorage } from 'jotai/utils';
import {
  BezierName,
  BezierRef,
  ErrorOr,
  isError,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import { GetPaths, LoadAndIndexFile, UpdateIndexFile } from './API';
import { EmptyMappedFile } from './IndexedFile';
import { AnonymousPathChain, MappedIndex } from '../types';

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

export const BlurAtom = atom('');

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
      const curPath = await get(SelectedFileAtom);
      if (curPath !== '') {
        const paths = await get(PathsAtom);
        if (hasField(paths, val)) {
          const files = paths[val];
          if (!files.includes(curPath)) {
            set(SelectedFileAtom, '');
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

export const SelectedFileAtom = atomWithStorage<string>(
  'selectedPath',
  '',
  undefined,
  { getOnInit: true },
);

const MappedFileBackingAtom = atom(0);
export const MappedFileAtom = atom(
  async (get) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedFileAtom);
    const count = get(MappedFileBackingAtom);
    if (team.length > 0 && file.length > 0) {
      const maybeIdx: ErrorOr<MappedIndex> = await LoadAndIndexFile(team, file);
      if (!isError(maybeIdx)) {
        return maybeIdx;
      }
      console.error(maybeIdx.errors().join('\n'));
    }
    return EmptyMappedFile;
  },
  async (get, set, data: MappedIndex | Promise<MappedIndex>) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedFileAtom);
    const val = get(MappedFileBackingAtom);
    UpdateIndexFile(team, file, await data);
    set(MappedFileBackingAtom, val + 1);
  },
);

type MapAtom<Str, T> = WritableAtom<Promise<Map<Str, T>>, [Map<Str, T>], void>;

export const MappedValuesAtom: MapAtom<ValueName, ValueRef | RadiansRef> =
  focusAtom(MappedFileAtom, (optic) => optic.prop('namedValues'));
export const MappedPosesAtom: MapAtom<PoseName, PoseRef> = focusAtom(
  MappedFileAtom,
  (optic) => optic.prop('namedPoses'),
);
export const MappedBeziersAtom: MapAtom<BezierName, BezierRef> = focusAtom(
  MappedFileAtom,
  (optic) => optic.prop('namedBeziers'),
);
export const MappedPathChainsAtom: MapAtom<PathChainName, AnonymousPathChain> =
  focusAtom(MappedFileAtom, (optic) => optic.prop('namedPathChains'));

function makeItemFromNameFamily<Str, T>(theAtom: MapAtom<Str, T>) {
  return atomFamily((name: Str) =>
    atom(
      async (get) => (await get(theAtom)).get(name),
      async (get, set, val: T) => {
        const mappedItems = new Map(await get(theAtom));
        mappedItems.set(name, val);
        set(theAtom, mappedItems);
      },
    ),
  );
}

export const ValueAtomFamily = makeItemFromNameFamily(MappedValuesAtom);
export const PoseAtomFamily = makeItemFromNameFamily(MappedPosesAtom);
export const BezierAtomFamily = makeItemFromNameFamily(MappedBeziersAtom);
export const PathChainAtomFamily = makeItemFromNameFamily(MappedPathChainsAtom);
