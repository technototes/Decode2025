/// <reference lib="dom" />

import {
  FluentProvider,
  webDarkTheme,
  webLightTheme,
} from '@fluentui/react-components';
import '@testing-library/jest-dom';
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react';
import { describe, expect, test } from 'bun:test';
import { Provider, useAtom } from 'jotai';
import { ReactElement } from 'react';
import { AnonymousBezier, PathChainFile, TeamPaths } from '../../server/types';
import { select_a_bot, select_a_file } from '../constants';
import { PathsDataDisplay } from '../PathsDataDisplay';
import { PathSelector } from '../PathSelector';
import { EmptyPathChainFile } from '../state/API';
import {
  ColorForNumber,
  ColorsAtom,
  FilesForSelectedTeam,
  NamedBeziersAtom,
  NamedPathChainsAtom,
  NamedPosesAtom,
  NamedValuesAtom,
  SelectedFileAtom,
  SelectedTeamAtom,
  ThemeAtom,
} from '../state/Atoms';
import { getStore } from '../state/Storage';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import './jest-dom-types-fix';

// Mocks & phony data for my tests:
const teamPaths: TeamPaths = {
  team1: ['path1.java', 'path2.java'],
  team2: ['path3.java', 'path4.java'],
};

const testPathChainFile: PathChainFile = {
  ...EmptyPathChainFile,
  name: 'path1.java',
};

const simpleBez: AnonymousBezier = {
  type: 'curve',
  points: [{ x: 'val1', y: 'val1' }, 'pose1', 'pose2'],
};
const fullPathChainFile: PathChainFile = {
  name: 'path3.java',
  values: [
    { name: 'val1', value: { type: 'int', value: 1 } },
    { name: 'val2', value: { type: 'double', value: 2.5 } },
    { name: 'val3', value: { type: 'radians', value: 90 } },
  ],
  poses: [
    { name: 'pose1', pose: { x: { type: 'double', value: 2.5 }, y: 'val1' } },
    {
      name: 'pose2',
      pose: { x: 'val2', y: 'val1', heading: { type: 'radians', value: 60 } },
    },
    {
      name: 'pose3',
      pose: { x: 'val1', y: 'val2', heading: 'val3' },
    },
  ],
  beziers: [
    { name: 'bez1', points: { type: 'line', points: ['pose1', 'pose2'] } },
    {
      name: 'bez2',
      points: simpleBez,
    },
  ],
  pathChains: [
    {
      name: 'pc1',
      paths: ['bez1', 'bez2'],
      heading: { type: 'tangent' },
    },
    {
      name: 'pc2',
      paths: ['bez2', { type: 'line', points: ['pose1', 'pose3'] }],
      heading: { type: 'constant', heading: 'pose3' },
    },
    {
      name: 'pc3',
      paths: ['bez1', { type: 'curve', points: ['pose1', 'pose3', 'pose2'] }],
      heading: {
        type: 'interpolated',
        headings: ['pose2', { radians: { type: 'int', value: 135 } }],
      },
    },
  ],
};

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
      const body = JSON.stringify(teamPaths);
      return new Response(body, status);
    }
    case '/api/loadpath/team1/path2.java': {
      const body = JSON.stringify(testPathChainFile);
      return new Response(body, status);
    }
    case '/api/loadpath/team2/path3.java': {
      const body = JSON.stringify(fullPathChainFile);
      return new Response(body, status);
    }
  }
  return new Response('ERROR', { status: 404 });
}
MyFetchFunc.preconnect = () => {};

function FluentFixture({
  change,
  children,
}: {
  change: boolean;
  children: ReactElement;
}): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  const theme = theTheme === 'dark' ? webDarkTheme : webLightTheme;
  if (change && theTheme === 'light') {
    setTimeout(() => setTheme('dark'), 0);
  }
  return <FluentProvider theme={theme}>{children}</FluentProvider>;
}

function JotaiProvider({
  children,
  change,
}: {
  children: ReactElement;
  change?: boolean;
}): ReactElement {
  const store = getStore();
  return (
    <Provider store={store}>
      <FluentFixture change={false || change}>{children}</FluentFixture>
    </Provider>
  );
}

describe('Simplest UI validation', () => {
  test('Themes & colors', async () => {
    const store = getStore();
    render(
      <JotaiProvider>
        <div />
      </JotaiProvider>,
    );
    expect(store.get(ThemeAtom)).toEqual('light');
    await waitFor(() => {});
    expect(store.get(ThemeAtom)).toEqual('light');
    render(
      <JotaiProvider change={true}>
        <div />
      </JotaiProvider>,
    );
    const beforeColors = store.get(ColorsAtom);
    expect(beforeColors).toBe(darkOnWhite);
    expect(store.get(ThemeAtom)).toEqual('light');
    await waitFor(() => {
      expect(store.get(ThemeAtom)).toEqual('dark');
    });
    expect(store.get(ColorsAtom)).toBe(lightOnBlack);
    for (let i = 0; i < lightOnBlack.length * 2; i++) {
      const color = store.get(ColorForNumber(i));
      expect(color).toBe(lightOnBlack[i % lightOnBlack.length]);
    }
  });
  test('File/Path Selection Atoms', async () => {
    globalThis.fetch = MyFetchFunc;
    const store = getStore();
    await act(async () => {
      render(
        <JotaiProvider>
          <PathSelector />
        </JotaiProvider>,
      );
    });
    // Need to cover Paths & Teams atoms
    let open = screen.getByText(select_a_bot);
    expect(open).toBeEnabled();
    let path = screen.getByText(select_a_file);
    expect(path).toBeDisabled();
    await act(async () => fireEvent.click(open));
    let select = screen.getByText('team2');
    expect(select).toBeEnabled();
    await act(async () => fireEvent.click(select));
    await waitFor(async () => {
      expect(await store.get(SelectedTeamAtom)).toBe('team2');
    });
    await waitFor(async () => {
      expect(await store.get(SelectedFileAtom)).toBe('');
    });
    // The second menu should now be enabled
    expect(path).toBeEnabled();
    await act(async () => fireEvent.click(path));
    // This is where I'm stuck, now (this doesn't work yet)
    let selectFile = screen.getByText('path3.java');
    expect(selectFile).toBeDefined();
    expect(selectFile).toBeEnabled();
    await act(async () => fireEvent.click(selectFile));
    await waitFor(async () => {
      expect(await store.get(SelectedFileAtom)).toBe('path3.java');
    });
    await act(async () => {
      await store.set(SelectedTeamAtom, 'team3');
    });
    await act(async () => {
      expect(await store.get(FilesForSelectedTeam)).toEqual([]);
    });
  });
});

describe('SchemaAtom tests', () => {
  test('PathDataDisplay atoms', async () => {
    globalThis.fetch = MyFetchFunc;
    const store = getStore();
    await act(async () => {
      render(
        <JotaiProvider>
          <PathsDataDisplay expand={true} />
        </JotaiProvider>,
      );
    });
    store.set(SelectedTeamAtom, 'team2');
    store.set(SelectedFileAtom, 'path3.java');
    await waitFor(async () => {
      expect(await store.get(SelectedFileAtom)).toBe('path3.java');
    });
    expect(store.get(NamedValuesAtom)).toBeDefined();
    expect(store.get(NamedPosesAtom)).toBeDefined();
    expect(store.get(NamedBeziersAtom)).toBeDefined();
    expect(store.get(NamedPathChainsAtom)).toBeDefined();
    await act(() =>
      store.set(NamedValuesAtom, {
        name: 'valX',
        value: { type: 'int', value: 42 },
      }),
    );
    waitFor(async () => {
      expect(await store.get(NamedValuesAtom)).toHaveProperty('valX');
      expect(await store.get(NamedPosesAtom)).toHaveProperty('poseX');
    });
    await act(() =>
      store.set(NamedPosesAtom, {
        name: 'poseX',
        pose: { x: 'valX', y: 'valX' },
      }),
    );
  });
});
