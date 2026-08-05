import { beforeEach, describe, expect, test } from 'bun:test';
import { useAtom, useAtomValue } from 'jotai';

import { act, renderHook } from '@testing-library/react';
import { MakeMultiMap } from '@freik/containers';
import { Pickle } from '@freik/typechk';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import { ParsedClass } from '../../CodeTypes';
import { ClassKey, Path, PathDatabase, PathKey, Team } from '../../IpcTypes';
import {
  ClearCache,
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
    ['team1*path1.java' as PathKey, ['team1*path1.java;' as ClassKey]],
    ['team1*path2.java' as PathKey, ['team1*path2.java;' as ClassKey]],
    ['team2*path3.java' as PathKey, ['team2*path3.java;' as ClassKey]],
    ['team2*path4.java' as PathKey, ['team2*path4.java;' as ClassKey]],
  ]),
  ParsedClasses: new Map<ClassKey, ParsedClass>([
    ['team1*path1.java;' as ClassKey, EmptyParsedClass],
    ['team1*path2.java;' as ClassKey, EmptyParsedClass],
    ['team2*path3.java;' as ClassKey, EmptyParsedClass],
    ['team2*path4.java;' as ClassKey, EmptyParsedClass],
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
  }
  throw new Error(`Unknown key: ${key}`);
}
MyFetchFunc.preconnect = () => {};

describe('Atom Capabilities', () => {
  test('Team/Path interactions', async () => {
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
  });
});
