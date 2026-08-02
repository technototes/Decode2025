import { atom, WritableAtom } from 'jotai';
import { atomFamily } from 'jotai-family';
import { focusAtom } from 'jotai-optics';
import { atomWithStorage, selectAtom, splitAtom, unwrap } from 'jotai/utils';

import { EmptyParsedClass } from 'CodeTypeCheck';
import { ErrorOr, isError } from '@freik/typechk';

import {
  AnonymousPathChain,
  BezierName,
  BezierRef,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../CodeTypes';
import { Path, PathDatabase, PathDBKey, Team } from '../../IpcTypes';
import { ForEachPathChainIndex } from '../../server/full-database';
import { OneFileIndex } from '../types';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import { GetFullDb, LoadAndIndexFile, UpdateIndexFile } from './API';
import { EmptyMappedFile, GetNameLookup, MakeFileIndex } from './IndexedFile';
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
export const FullDatabaseAtom = atom(async () => {
  if (dbCache === null) {
    dbCache = await GetFullDb();
  }
  return dbCache;
});

export const IndexedDatabaseAtom = atom(async (get) => {
  const db = await get(FullDatabaseAtom);
  const index = GetNameLookup();
  for (const [, [, pc]] of db) {
    ForEachPathChainIndex(pc, (file) =>
      index.registerIndex(MakeFileIndex(file)),
    );
  }
  return index;
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

export const SelectedParsedClassAtom = atom(
  async (get): Promise<ParsedClass> => {
    const db = await get(FullDatabaseAtom);
    const key = await get(SelectedDBKeyAtom);
    const className = await get(SelectedClassAtom);
    const val = db.get(key);
    if (!val) {
      return EmptyParsedClass;
    }
    const which = val[0].indexOf(className);
    if (which < 0) {
      return EmptyParsedClass;
    }
    // Scan the DAG of ParsedClasses to find the selected one.
    let res: ParsedClass = EmptyParsedClass;
    ForEachPathChainIndex(val[1], (pc) => {
      if (pc.name === className) {
        res = pc;
        return true;
      }
    });
    return res;
  },
  async (get, set, val: ParsedClass) => {
    // TODO:
  },
);

const UnwrappedParsedClass = unwrap(
  SelectedParsedClassAtom,
  () => EmptyParsedClass,
);

const MappedFileBackingAtom = atom(0);
export const MappedFileAtom = atom(
  async (get) => {
    const team = await get(SelectedTeamAtom);
    const file = await get(SelectedFileAtom);
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
    const file = await get(SelectedFileAtom);
    const val = get(MappedFileBackingAtom);
    UpdateIndexFile(team, file, await data);
    set(MappedFileBackingAtom, val + 1);
  },
);

type MapAtom<Str, T> = WritableAtom<Promise<Map<Str, T>>, [Map<Str, T>], void>;

export const NamedValuesAtom = selectAtom(
  UnwrappedParsedClass,
  (pc) => pc.values,
);

export const ValuesLookupAtom = atom(
  (get): Map<ValueName, ValueRef | RadiansRef> => {
    const nvs = get(NamedValuesAtom);
    return new Map((nvs || []).map(({ name, value }) => [name, value]));
  },
);

export const SplitValuesAtom = splitAtom(NamedValuesAtom);

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
