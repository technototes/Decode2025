import { beforeEach, describe, expect, test } from 'bun:test';
import { useAtom, useAtomValue } from 'jotai';

import { act, renderHook } from '@testing-library/react';
import { MakeMultiMap } from '@freik/containers';
import {
  isDefined,
  isNull,
  isUndefined,
  Pickle,
  SafelyUnpickle,
} from '@freik/typechk';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import { ParsedClass } from '../../CodeTypes';
import { chkPathDatabase } from '../../IpcTypeCheck';
import {
  ClassKey,
  ClassName,
  Path,
  PathDatabase,
  PathKey,
  Team,
} from '../../IpcTypes';
import {
  ClassKeysForSelectedPathAtom,
  ClearCache,
  FullDatabaseAtom,
  IndexedDatabaseAtom,
  PathKeysForSelectedTeamAtom,
  PathsForSelectedTeamAtom,
  SelectedClassAtom,
  SelectedPathAtom,
  SelectedTeamAtom,
  TeamsAtom,
} from '../state/Atoms';

globalThis.IS_REACT_ACT_ENVIRONMENT = true;
// or global.IS_REACT_ACT_ENVIRONMENT = true; depending on your environment

beforeEach(ClearCache);

const status = {
  status: 200,
  headers: { 'Content-Type': 'application/json' },
};
const database: PathDatabase = {
  TeamPaths: MakeMultiMap<Team, PathKey>([
    [
      'team1' as Team,
      ['team1*path1.java' as PathKey, 'team1*path2.java' as PathKey],
    ],
    [
      'team2' as Team,
      ['team2*path3.java' as PathKey, 'team2*path4.java' as PathKey],
    ],
  ]),
  PathClasses: MakeMultiMap<PathKey, ClassKey>([
    ['team1*path1.java' as PathKey, ['team1*path1.java;a' as ClassKey]],
    ['team1*path2.java' as PathKey, ['team1*path2.java;b' as ClassKey]],
    ['team2*path3.java' as PathKey, ['team2*path3.java;c' as ClassKey]],
    ['team2*path4.java' as PathKey, ['team2*path4.java;d' as ClassKey]],
  ]),
  ParsedClasses: new Map<ClassKey, ParsedClass>([
    ['team1*path1.java;a' as ClassKey, EmptyParsedClass],
    ['team1*path2.java;b' as ClassKey, EmptyParsedClass],
    ['team2*path3.java;c' as ClassKey, EmptyParsedClass],
    ['team2*path4.java;d' as ClassKey, EmptyParsedClass],
  ]),
};

async function MyFetchFunc(
  key: string | URL | Request,
  init?: RequestInit,
): Promise<Response> {
  switch (key) {
    case '/api/db': {
      const body = Pickle(database);
      return new Response(body, status);
    }
    case '/api/putdb': {
      if (isDefined(init) && !isNull(init.body) && isDefined(init.body)) {
        const db = SafelyUnpickle(init.body.toString(), chkPathDatabase);
        database.ParsedClasses =
          db?.ParsedClasses || new Map<ClassKey, ParsedClass>();
        database.PathClasses =
          db?.PathClasses || MakeMultiMap<PathKey, ClassKey>();
        database.TeamPaths = db?.TeamPaths || MakeMultiMap<Team, PathKey>();
      }
      return new Response('', status);
    }
  }
  throw new Error(`Unknown key: ${key}`);
}
MyFetchFunc.preconnect = () => {};

describe('Atom Capabilities', () => {
  test('Team/Path/Class interactions', async () => {
    globalThis.fetch = MyFetchFunc;
    const teams = await act(() => renderHook(() => useAtomValue(TeamsAtom)));
    const paths = teams.result.current;
    // First one that failes:
    expect(paths).toEqual(['team1', 'team2'] as Team[]);
    const setTeam = await act(() =>
      renderHook(() => useAtom(SelectedTeamAtom)),
    );
    await act(() => setTeam.result.current[1]('team1'));
    const selectedTeam = await act(() =>
      renderHook(() => useAtomValue(SelectedTeamAtom)),
    );
    expect(selectedTeam.result.current).toEqual('team1' as Team);
    const setFile = await act(() =>
      renderHook(() => useAtom(SelectedPathAtom)),
    );
    await act(() => setFile.result.current[1]('path1.java'));
    const selectedFile = await act(() =>
      renderHook(() => useAtomValue(SelectedPathAtom)),
    );
    expect(selectedFile.result.current).toEqual('path1.java' as Path);
    await act(() => setTeam.result.current[1]('team2'));
    const selectedTeam2 = await act(() =>
      renderHook(() => useAtomValue(SelectedTeamAtom)),
    );
    expect(selectedTeam2.result.current).toEqual('team2' as Team);
    const sel = await act(() =>
      renderHook(() => useAtomValue(PathKeysForSelectedTeamAtom)),
    );
    expect(sel.result.current).toEqual(
      new Set(['team2*path3.java' as PathKey, 'team2*path4.java' as PathKey]),
    );
    const selPaths = await act(() =>
      renderHook(() => useAtom(PathsForSelectedTeamAtom)),
    );
    expect(selPaths.result.current[0]).toEqual([
      'path3.java',
      'path4.java',
    ] as Path[]);
    const selPath = await act(() =>
      renderHook(() => useAtom(SelectedPathAtom)),
    );
    expect(selPath.result.current[0]).toEqual('' as Path);
    selPath.result.current[1]('path3.java');
    const selPath2 = await act(() =>
      renderHook(() => useAtomValue(SelectedPathAtom)),
    );
    expect(selPath2.result.current).toEqual('path3.java' as Path);
    const selClassKeys = await act(() =>
      renderHook(() => useAtomValue(ClassKeysForSelectedPathAtom)),
    );
    expect(selClassKeys.result.current).toEqual(
      new Set(['team2*path3.java;c' as ClassKey]),
    );
    const selClass = await act(() =>
      renderHook(() => useAtom(SelectedClassAtom)),
    );
    expect(selClass.result.current[0]).toEqual('' as ClassName);
    selClass.result.current[1]('c');
    const selClass2 = await act(() =>
      renderHook(() => useAtomValue(SelectedClassAtom)),
    );
    expect(selClass2.result.current).toEqual('c' as ClassName);
  });
  test('Database interactions', async () => {
    globalThis.fetch = MyFetchFunc;
    const db = await act(() => renderHook(() => useAtom(FullDatabaseAtom)));
    expect(db.result).toBeDefined();
    expect(db.result.current[0]).toBeDefined();
    const dbVal = SafelyUnpickle(Pickle(db.result.current[0]), chkPathDatabase);
    expect(dbVal).toBeDefined();
    if (isUndefined(dbVal)) {
      return;
    }
    expect(JSON.parse(Pickle(dbVal))).toEqual(JSON.parse(Pickle(database)));
    database.ParsedClasses.clear();
    database.PathClasses.clear();
    database.TeamPaths.clear();
    db.result.current[1](dbVal);
    expect(database.ParsedClasses.size).toEqual(4);
    expect(database.PathClasses.size()).toEqual(4);
    expect(database.TeamPaths.size()).toEqual(2);
    const idb = await act(() =>
      renderHook(() => useAtomValue(IndexedDatabaseAtom)),
    );
    expect(idb.result).toBeDefined();
    const idbdb = idb.result.current.db();
    expect(idbdb).toBeDefined();
  });
});
