import { atom, WritableAtom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { focusAtom } from 'jotai-optics';
import {
  atomWithRefresh,
  atomWithStorage,
  selectAtom,
  splitAtom,
  unwrap,
} from 'jotai/utils';

import { ErrorOr, isError } from '@freik/typechk';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import {
  AnonymousPathChain,
  BezierName,
  BezierRef,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../CodeTypes';
import {
  ClassFromKey,
  getClassKey,
  getPathKey,
  PathFromKey,
} from '../../IpcTypeCheck';
import {
  ClassKey,
  ClassName,
  Path,
  PathDatabase,
  PathKey,
  Team,
} from '../../IpcTypes';
import { NameLookup, OneFileIndex } from '../types';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import { GetFullDb, LoadAndIndexFile, PutFullDb, UpdateIndexFile } from './API';
import { EmptyMappedFile, GetNameLookup } from './IndexedFile';
import { ThemeAtom } from './SavedSettings';

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
export const FullDatabaseAtom = atomWithRefresh(
  async () => {
    dbCache = await GetFullDb();
    return dbCache;
  },
  async (get, set, val: PathDatabase) => {
    await PutFullDb(val);
  },
);

export const IndexedDatabaseAtom = atomWithRefresh(
  async (get) => {
    const db = await get(FullDatabaseAtom);
    const index = GetNameLookup();
    index.setDb(db);
    return index;
  },
  async (get, set, val: NameLookup) => {
    // TODO: This ain't done, not in the least
    console.error('NYI: IndexedDatabaseAtom set');
    const db = await get(FullDatabaseAtom);
  },
);

export function ClearCache() {
  dbCache = null;
}

export const TeamPathsSelect = selectAtom(
  FullDatabaseAtom,
  async (db) => (await db).TeamPaths,
);
export const PathClassesSelect = selectAtom(
  FullDatabaseAtom,
  async (db) => (await db).PathClasses,
);
export const ParsedClassesSelect = selectAtom(
  FullDatabaseAtom,
  async (db) => (await db).ParsedClasses,
);

export const TeamsAtom = atom(async (get): Promise<Team[]> => {
  const tp = await get(TeamPathsSelect);
  return [...tp.keys()];
});

export const PathKeysForTeamFamily = atomFamily((team: Team) =>
  atom(async (get): Promise<Set<PathKey>> => {
    return (await get(TeamPathsSelect)).get(team) || new Set();
  }),
);

export const PathsForTeamFamily = atomFamily((team: Team) =>
  atom(async (get): Promise<Path[]> => {
    return [...(await get(PathKeysForTeamFamily(team))).keys()].map(
      PathFromKey,
    );
  }),
);

export const ClassKeysForPathKeyFamily = atomFamily((pk: PathKey) =>
  atom(async (get): Promise<Set<ClassKey>> => {
    return (await get(PathClassesSelect)).get(pk) || new Set();
  }),
);

export const ClassesForPathKeyFamily = atomFamily((pk: PathKey) =>
  atom(async (get): Promise<ClassName[]> => {
    return [...(await get(ClassKeysForPathKeyFamily(pk))).keys()].map(
      ClassFromKey,
    );
  }),
);

export const PathKeysForSelectedTeamAtom = atom(
  async (get): Promise<Set<PathKey>> => {
    const selTeam = await get(SelectedTeamAtom);
    return await get(PathKeysForTeamFamily(selTeam));
  },
);

export const PathsForSelectedTeamAtom = atom(async (get): Promise<Path[]> => {
  const selTeam = await get(SelectedTeamAtom);
  return await get(PathsForTeamFamily(selTeam));
});

export const ClassKeysFoSelectedPathAtom = atom(
  async (get): Promise<Set<ClassKey>> => {
    const pathKey = get(SelectedPathKeyAtom);
    return get(ClassKeysForPathKeyFamily(pathKey));
  },
);

export const SelectedTeamBacking = atomWithStorage<Team>(
  'selectedTeam',
  '' as Team,
  undefined,
  { getOnInit: true },
);

export const SelectedTeamAtom = atom(
  (get) => get(SelectedTeamBacking),
  (get, set, val: string | Team) => {
    const cur = get(SelectedTeamBacking);
    // Clear the selected file when the team is changed
    if (cur !== val) {
      set(SelectedPathAtom, '' as Path);
      set(SelectedTeamBacking, val as Team);
    }
  },
);

export const SelectedPathKeyBacking = atomWithStorage<PathKey>(
  'selectedPathKey',
  '' as PathKey,
  undefined,
  { getOnInit: true },
);

export const SelectedPathKeyAtom = atom(
  (get) => get(SelectedPathKeyBacking),
  (get, set, val: PathKey | string) => {
    const pathKey = get(SelectedPathKeyBacking);
    // Clear the selected class when the file is changed
    if (pathKey !== val) {
      set(SelectedClassAtom, '' as ClassName);
      set(SelectedPathKeyBacking, val as PathKey);
    }
  },
);

export const SelectedPathAtom = atom(
  (get) => PathFromKey(get(SelectedPathKeyBacking)),
  (get, set, val: Path | string) => {
    const team = get(SelectedTeamAtom);
    const curKey = get(SelectedPathKeyAtom);
    const key = getPathKey(team, val as Path);
    // Clear the selected class when the file is changed
    if (key !== curKey) {
      set(SelectedClassAtom, '' as ClassName);
      set(SelectedPathKeyBacking, key);
    }
  },
);

export const ClassesForSelectedPathAtom = atom(
  async (get): Promise<ClassName[]> => {
    const key = await get(SelectedPathKeyAtom);
    return await get(ClassesForPathKeyFamily(key));
  },
);

export const SelectedClassKeyAtom = atomWithStorage(
  'selectedClass',
  '' as ClassKey,
  undefined,
  { getOnInit: true },
);

export const SelectedClassAtom = atom(
  (get) => ClassFromKey(get(SelectedClassKeyAtom)),
  async (get, set, val: ClassName | string) => {
    const pathKey = get(SelectedPathKeyAtom);
    const classKey = getClassKey(pathKey, val);
    const curSel = get(SelectedClassKeyAtom);
    if (classKey != curSel) {
      set(SelectedClassKeyAtom, classKey);
    }
  },
);

export const SelectedParsedClassAtom = atom(
  async (get): Promise<ParsedClass> => {
    const db = await get(FullDatabaseAtom);
    const key = await get(SelectedClassKeyAtom);
    return db.ParsedClasses.get(key) || EmptyParsedClass;
  },
  async (get, set, val: ParsedClass) => {},
);

const UnwrappedParsedClass = unwrap(
  SelectedParsedClassAtom,
  () => EmptyParsedClass,
);

const MappedFileBackingAtom = atom(0);
export const MappedFileAtom = atom(
  async (get) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedPathAtom);
    const count = get(MappedFileBackingAtom);
    const fullIndex = get(IndexedDatabaseAtom);
    if (team.length > 0 && file.length > 0) {
      const maybeIdx: ErrorOr<OneFileIndex> = await LoadAndIndexFile(
        team,
        file,
      );
      if (!isError(maybeIdx)) {
        return maybeIdx;
      }
      console.error(maybeIdx.errors().join('\n'));
    }
    return EmptyMappedFile;
  },
  async (get, set, data: OneFileIndex | Promise<OneFileIndex>) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedPathAtom);
    const val = get(MappedFileBackingAtom);
    UpdateIndexFile(team, file, await data);
    set(MappedFileBackingAtom, val + 1);
  },
);

type MapAtom<Str, T> = WritableAtom<Promise<Map<Str, T>>, [Map<Str, T>], void>;

export const ValuesAtoms = Object.freeze(
  (() => {
    const List = selectAtom(UnwrappedParsedClass, (pc) => pc.values);
    const Lookup = atom((get): Map<ValueName, ValueRef | RadiansRef> => {
      const nvs = get(NamedValuesAtom);
      return new Map((nvs || []).map(({ name, value }) => [name, value]));
    });
    const Items = splitAtom(List, (nv) => nv.name);
    return {
      List,
      Lookup,
      Items,
    };
  })(),
);

export const NamedValuesAtom = ValuesAtoms.List;
export const ValuesLookupAtom = ValuesAtoms.Lookup;
export const SplitValuesAtom = ValuesAtoms.Items;

export const MappedValuesAtom: MapAtom<ValueName, ValueRef | RadiansRef> =
  focusAtom(MappedFileAtom, (optic) => optic.prop('namedValues'));

export const NamedPosesAtom = atom(async (get): Promise<NamedPose[]> => {
  const index = await get(SelectedParsedClassAtom);
  return index ? index.poses : [];
});

export const MappedPosesAtom: MapAtom<PoseName, PoseRef> = focusAtom(
  MappedFileAtom,
  (optic) => optic.prop('namedPoses'),
);

export const NamedBeziersAtom = atom(async (get): Promise<NamedBezier[]> => {
  const index = await get(SelectedParsedClassAtom);
  return index ? index.beziers : [];
});

export const NamedPathChainsAtom = atom(
  async (get): Promise<NamedPathChain[]> => {
    const index = await get(SelectedParsedClassAtom);
    return index?.pathChains || [];
  },
);
export const MappedBeziersAtom: MapAtom<BezierName, BezierRef> = focusAtom(
  MappedFileAtom,
  (optic) => optic.prop('namedBeziers'),
);

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
