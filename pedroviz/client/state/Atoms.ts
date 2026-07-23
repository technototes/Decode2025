import { atom, WritableAtom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { focusAtom } from 'jotai-optics';
import { atomWithStorage } from 'jotai/utils';

import { ErrorOr, isError } from '@freik/typechk';

import {
  BezierName,
  BezierRef,
  NamedPose,
  NamedValue,
  Path,
  PathChainClass,
  PathChainName,
  PathDatabase,
  PathDBKey,
  PoseName,
  PoseRef,
  RadiansRef,
  Team,
  ValueName,
  ValueRef,
} from '../../server/types';
import { AnonymousPathChain, FileIndex } from '../types';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import { GetFullDb, LoadAndIndexFile, UpdateIndexFile } from './API';
import { EmptyMappedFile } from './IndexedFile';

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
let dbCache: PathDatabase | null = null;
export const FullDatabaseAtom = atom(async () => {
  if (dbCache === null) {
    dbCache = await GetFullDb();
  }
  return dbCache;
});

export function ClearCache() {
  dbCache = null;
}

export const TeamsAtom = atom(async (get): Promise<Team[]> => {
  const db = await get(FullDatabaseAtom);
  return [
    ...new Set([...db.keys()].map((dbkey) => dbkey.split('*')[0]!)),
  ] as Team[];
});

export const SelectedTeamBacking = atomWithStorage<Team>(
  'selectedTeam',
  '' as Team,
  undefined,
  { getOnInit: true },
);

export const SelectedTeamAtom = atom(
  async (get) => get(SelectedTeamBacking),
  async (get, set, val: string | Team) => {
    const cur = await get(SelectedTeamBacking);
    // Clear the selected file when the team is changed
    if (cur !== val) {
      set(SelectedFileAtom, '' as Path);
    }
    set(SelectedTeamBacking, val as Team);
  },
);

export const FilesForSelectedTeamAtom = atom(async (get): Promise<Path[]> => {
  const db = await get(FullDatabaseAtom);
  const selTeam = await get(SelectedTeamAtom);
  if (selTeam.length > 0) {
    return [...db.keys()]
      .filter((key) => key.startsWith(`${selTeam}*`))
      .map((val) => val.split('*')[1] as Path);
  }
  return [] as Path[];
});

export const SelectedFileBacking = atomWithStorage<Path>(
  'selectedPath',
  '' as Path,
  undefined,
  { getOnInit: true },
);

export const SelectedFileAtom = atom(
  async (get) => get(SelectedFileBacking),
  async (get, set, val: string | Path) => {
    const cur = await get(SelectedFileAtom);
    // Clear the selected class when the file is changed
    if (cur !== val) {
      set(SelectedClassAtom, '');
    }
    set(SelectedFileBacking, val as Path);
  },
);

export const SelectedDBKeyAtom = atom(async (get): Promise<PathDBKey> => {
  const team = await get(SelectedTeamAtom);
  const file = await get(SelectedFileAtom);
  return `${team}*${file}` as PathDBKey;
});

export const ClassesForSelectedFileAtom = atom(
  async (get): Promise<string[]> => {
    const db = await get(FullDatabaseAtom);
    const key = await get(SelectedDBKeyAtom);
    return (db.get(key) || [[]])[0];
  },
);

export const SelectedClassAtom = atomWithStorage(
  'selectedClass',
  '',
  undefined,
  { getOnInit: true },
);

export const SelectedPathChainClassAtom = atom(
  async (get): Promise<undefined | PathChainClass> => {
    const db = await get(FullDatabaseAtom);
    const key = await get(SelectedDBKeyAtom);
    const className = await get(SelectedClassAtom);
    const val = db.get(key);
    if (!val) {
      return;
    }
    const which = val[0].indexOf(className);
    if (which < 0) {
      return;
    }
    // Scan the DAG of PathChainClasses to find the selected one.
    const work = [val[1]];
    while (work.length !== 0) {
      const item = work.pop()!;
      if (item.name === className) {
        return item;
      }
    }
  },
);

const MappedFileBackingAtom = atom(0);
export const MappedFileAtom = atom(
  async (get) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedFileAtom);
    const count = get(MappedFileBackingAtom);
    if (team.length > 0 && file.length > 0) {
      const maybeIdx: ErrorOr<FileIndex> = await LoadAndIndexFile(team, file);
      if (!isError(maybeIdx)) {
        return maybeIdx;
      }
      console.error(maybeIdx.errors().join('\n'));
    }
    return EmptyMappedFile;
  },
  async (get, set, data: FileIndex | Promise<FileIndex>) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedFileAtom);
    const val = get(MappedFileBackingAtom);
    UpdateIndexFile(team, file, await data);
    set(MappedFileBackingAtom, val + 1);
  },
);

type MapAtom<Str, T> = WritableAtom<Promise<Map<Str, T>>, [Map<Str, T>], void>;

export const NamedValuesAtom = atom(async (get): Promise<NamedValue[]> => {
  const index = await get(SelectedPathChainClassAtom);
  if (index) {
    console.log('Making mapped values', index.fullName);
  }
  return index ? index.values : [];
  // isDefined(index) ? new Map(index.values.map((nv) => [nv.name, nv.value])) : new Map();
});

export const MappedValuesAtom: MapAtom<ValueName, ValueRef | RadiansRef> =
  focusAtom(MappedFileAtom, (optic) => optic.prop('namedValues'));

export const NamedPosesAtom = atom(async (get): Promise<NamedPose[]> => {
  const index = await get(SelectedPathChainClassAtom);
  if (index) {
    console.log('Making mapped poses', index.fullName);
  }
  return index ? index.poses : [];
  // isDefined(index) ? new Map(index.values.map((nv) => [nv.name, nv.value])) : new Map();
});

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
