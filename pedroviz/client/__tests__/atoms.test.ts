import { act, renderHook } from '@testing-library/react';
import { describe, expect, test } from 'bun:test';
import { useAtom, useAtomValue } from 'jotai';
import { SelectedFileAtom, SelectedTeamAtom, TeamsAtom } from '../state/Atoms';

globalThis.IS_REACT_ACT_ENVIRONMENT = true;
// or global.IS_REACT_ACT_ENVIRONMENT = true; depending on your environment

const status = {
  status: 200,
  headers: { 'Content-Type': 'application/json' },
};

async function MyFetchFunc(
  key: string | URL | Request,
  init?: RequestInit,
): Promise<Response> {
  switch (key) {
    case '/api/getpaths': {
      const body = JSON.stringify({
        team1: ['path1.java', 'path2.java'],
        team2: ['path3.java', 'path4.java'],
      });
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
    expect(paths).toEqual(['team1', 'team2']);
    const setTeam = await act(() =>
      renderHook(() => useAtom(SelectedTeamAtom)),
    );
    await act(() => setTeam.result.current[1]('team1'));
    const selectedTeam = await act(() =>
      renderHook(() => useAtomValue(SelectedTeamAtom)),
    );
    expect(selectedTeam.result.current).toEqual('team1');
    const setFile = await act(() =>
      renderHook(() => useAtom(SelectedFileAtom)),
    );
    await act(() => setFile.result.current[1]('path1.java'));
    const selectedFile = await act(() =>
      renderHook(() => useAtomValue(SelectedFileAtom)),
    );
    expect(selectedFile.result.current).toEqual('path1.java');
    await act(() => setTeam.result.current[1]('team2'));
    const selectedTeam2 = await act(() =>
      renderHook(() => useAtomValue(SelectedTeamAtom)),
    );
    expect(selectedTeam2.result.current).toEqual('team2');
  });
});
