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
import { beforeEach, describe, expect, test } from 'bun:test';
import { Provider, useAtom } from 'jotai';
import { ReactElement } from 'react';
import {
  AnonymousBezier,
  BezierName,
  Path,
  PathChainFile,
  PathChainName,
  PoseName,
  Team,
  TeamPaths,
  ValueName,
} from '../../server/types';
import { select_a_bot, select_a_file } from '../constants';
import { PathsDataDisplay } from '../PathsDataDisplay';
import { PathSelector } from '../PathSelector';
import {
  ColorForNumber,
  ColorsAtom,
  FilesForSelectedTeam,
  MappedBeziersAtom,
  MappedPathChainsAtom,
  MappedPosesAtom,
  MappedValuesAtom,
  PoseAtomFamily,
  SelectedFileAtom,
  SelectedTeamAtom,
  ThemeAtom,
  ValueAtomFamily,
} from '../state/Atoms';
import { getStore } from '../state/Storage';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';
import './jest-dom-types-fix.test';

// Mocks & phony data for my tests:
const teamPaths: TeamPaths = {
  ['team1' as Team]: ['path1.java' as Path, 'path2.java' as Path],
  ['team2' as Team]: ['path3.java' as Path, 'path4.java' as Path],
};

const testPathChainFile: PathChainFile = {
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  name: 'path1.java',
};

const simpleBez: AnonymousBezier = {
  type: 'curve',
  points: [
    { x: 'val1' as ValueName, y: 'val1' as ValueName },
    'pose1' as PoseName,
    'pose2' as PoseName,
  ],
};
const fullPathChainFile: PathChainFile = {
  name: 'path3.java',
  values: [
    { name: 'val1' as ValueName, value: { int: 1 } },
    { name: 'val2' as ValueName, value: { double: 2.5 } },
    { name: 'val3' as ValueName, value: { radians: { int: 90 } } },
  ],
  poses: [
    {
      name: 'pose1' as PoseName,
      pose: { x: { double: 2.5 }, y: 'val1' as ValueName },
    },
    {
      name: 'pose2' as PoseName,
      pose: {
        x: 'val2' as ValueName,
        y: 'val1' as ValueName,
        heading: { radians: { int: 60 } },
      },
    },
    {
      name: 'pose3' as PoseName,
      pose: {
        x: 'val1' as ValueName,
        y: 'val2' as ValueName,
        heading: 'val3' as ValueName,
      },
    },
  ],
  beziers: [
    {
      name: 'bez1' as BezierName,
      points: {
        type: 'line',
        points: ['pose1' as PoseName, 'pose2' as PoseName],
      },
    },
    {
      name: 'bez2' as BezierName,
      points: simpleBez,
    },
  ],
  pathChains: [
    {
      name: 'pc1' as PathChainName,
      paths: ['bez1' as BezierName, 'bez2' as BezierName],
      heading: { type: 'tangent' },
    },
    {
      name: 'pc2' as PathChainName,
      paths: [
        'bez2' as BezierName,
        { type: 'line', points: ['pose1' as PoseName, 'pose3' as PoseName] },
      ],
      heading: { type: 'constant', heading: 'pose3' as PoseName },
    },
    {
      name: 'pc3' as PathChainName,
      paths: [
        'bez1' as BezierName,
        {
          type: 'curve',
          points: [
            'pose1' as PoseName,
            'pose3' as PoseName,
            'pose2' as PoseName,
          ],
        },
      ],
      heading: {
        type: 'interpolated',
        headings: ['pose2' as PoseName, { radians: { int: 135 } }],
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

beforeEach(async () => {
  // Execute the localStorage clear function within the test environment
  // This approach is common when using test runners that control a browser context
  await window.localStorage.clear();
});

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
    await act(async () => {
      await store.set(SelectedTeamAtom, 'team2');
      await store.set(SelectedFileAtom, 'path3.java');
    });
    await act(async () => {
      expect(await store.get(SelectedFileAtom)).toBe('path3.java');
    });
    expect(await store.get(MappedValuesAtom)).toBeDefined();
    expect(await store.get(MappedPosesAtom)).toBeDefined();
    expect(await store.get(MappedBeziersAtom)).toBeDefined();
    expect(await store.get(MappedPathChainsAtom)).toBeDefined();
    await act(() =>
      store.set(ValueAtomFamily('valX' as ValueName), { int: 42 }),
    );
    await waitFor(async () => {
      expect(
        (await store.get(MappedValuesAtom)).has('valX' as ValueName),
      ).toBeTrue();
      expect(
        (await store.get(MappedPosesAtom)).has('poseX' as PoseName),
      ).toBeFalse();
    });
    await act(() =>
      store.set(PoseAtomFamily('poseX' as PoseName), {
        x: 'valX' as ValueName,
        y: 'valX' as ValueName,
      }),
    );
  });
});
