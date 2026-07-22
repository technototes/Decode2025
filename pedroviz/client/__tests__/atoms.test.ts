import { beforeEach, describe, expect, test } from 'bun:test';
import { useAtom, useAtomValue } from 'jotai';

import { act, renderHook } from '@testing-library/react';
import { Pickle } from 'node_modules/@freik/typechk/lib/esm';

import { makeKey } from '../../server/full-database';
import {
  EmptyPathChainClass,
  Path,
  PathDBKey,
  PathDBValue,
  Team,
} from '../../server/types';
import {
  ClearCache,
  SelectedFileAtom,
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

const database = new Map<PathDBKey, PathDBValue>([
  [makeKey('team1' as Team, 'path1.java' as Path), [[], EmptyPathChainClass]],
  [makeKey('team1' as Team, 'path2.java' as Path), [[], EmptyPathChainClass]],
  [makeKey('team2' as Team, 'path3.java' as Path), [[], EmptyPathChainClass]],
  [makeKey('team2' as Team, 'path4.java' as Path), [[], EmptyPathChainClass]],
]);

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
      renderHook(() => useAtom(SelectedFileAtom)),
    );
    await act(() => setFile.result.current[1]('path1.java'));
    const selectedFile = await act(() =>
      renderHook(() => useAtomValue(SelectedFileAtom)),
    );
    expect(selectedFile.result.current).toEqual('path1.java' as Path);
    await act(() => setTeam.result.current[1]('team2'));
    const selectedTeam2 = await act(() =>
      renderHook(() => useAtomValue(SelectedTeamAtom)),
    );
    expect(selectedTeam2.result.current).toEqual('team2' as Team);
  });
});
