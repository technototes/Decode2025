/// <reference lib="dom" />

import { beforeEach, describe, expect, test } from 'bun:test';
import { ReactElement } from 'react';
import { Provider, useAtom } from 'jotai';

import {
  FluentProvider,
  webDarkTheme,
  webLightTheme,
} from '@fluentui/react-components';
import {
  act,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react';
import { MakeMultiMap } from '@freik/containers';
import { Pickle } from '@freik/typechk';

import '@testing-library/jest-dom';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import {
  AnonymousBezier,
  BezierName,
  BezierType,
  FacingType,
  ParsedClass,
  PathChainName,
  PoseName,
  ValueName,
} from '../../CodeTypes';
import { ClassKey, Path, PathDatabase, PathKey, Team } from '../../IpcTypes';
import { Strings } from '../constants';
import { PathsDataDisplay } from '../PathsDataDisplay';
import { PathSelector } from '../PathSelector';
import {
  ClearCache,
  ColorForNumber,
  ColorsAtom,
  MappedBeziersAtom,
  NamedPosesAtom,
  PathsForSelectedTeamAtom,
  SelectedClassAtom,
  SelectedPathAtom,
  SelectedTeamAtom,
  ValuesLookupAtom,
} from '../state/Atoms';
import { ThemeAtom } from '../state/SavedSettings';
import { getStore } from '../state/Storage';
import { darkOnWhite, lightOnBlack } from '../ui-tools/Colors';

import './jest-dom-types-fix.test';

// Mocks & phony data for my tests:
const teams: Team[] = ['team1' as Team, 'team2' as Team];
//   ['team1' as Team]: ['path1.java' as Path, 'path2.java' as Path],
//   ['team2' as Team]: ['path3.java' as Path, 'path4.java' as Path],
// };

const testParsedClass: ParsedClass = {
  values: [],
  poses: [],
  beziers: [],
  pathChainHelpers: [],
  pathChains: [],
  container: { fileName: '' },
  children: {},
  name: 'path1.java',
  fullName: 'test.path1',
  imports: [],
};

const simpleBez: AnonymousBezier = {
  type: BezierType.Curve,
  points: [
    { x: 'val1' as ValueName, y: 'val1' as ValueName },
    'pose1' as PoseName,
    'pose2' as PoseName,
  ],
};
const fullParsedClass: ParsedClass = {
  name: 'path3.java',
  fullName: 'test.path3',
  imports: [],
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
        type: BezierType.Line,
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
      heading: { type: FacingType.Tangent },
    },
    {
      name: 'pc2' as PathChainName,
      paths: [
        'bez2' as BezierName,
        {
          type: BezierType.Line,
          points: ['pose1' as PoseName, 'pose3' as PoseName],
        },
      ],
      heading: { type: FacingType.Constant, heading: 'pose3' as PoseName },
    },
    {
      name: 'pc3' as PathChainName,
      paths: [
        'bez1' as BezierName,
        {
          type: BezierType.Curve,
          points: [
            'pose1' as PoseName,
            'pose3' as PoseName,
            'pose2' as PoseName,
          ],
        },
      ],
      heading: {
        type: FacingType.Linear,
        start: 'pose2' as PoseName,
        end: { radians: { int: 135 } },
      },
    },
  ],
  // TODO
  container: { fileName: '' },
  children: {},
  pathChainHelpers: [],
};

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
    ['team2*path3.java;c' as ClassKey, fullParsedClass],
    ['team2*path4.java;d' as ClassKey, EmptyParsedClass],
  ]),
};

async function MyFetchFunc(
  key: string | URL | Request,
  init?: RequestInit,
): Promise<Response> {
  switch (key) {
    case '/api/loadpath/team1/path2.java': {
      const body = JSON.stringify(testParsedClass);
      return new Response(body, status);
    }
    case '/api/loadpath/team2/path3.java': {
      const body = JSON.stringify(fullParsedClass);
      return new Response(body, status);
    }
    case '/api/db': {
      const body = Pickle(database);
      return new Response(body, status);
    }
  }
  throw new Error(`Unknown key: ${key}`);
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
      <FluentFixture change={change!!}>{children}</FluentFixture>
    </Provider>
  );
}

beforeEach(async () => {
  // Execute the localStorage clear function within the test environment
  // This approach is common when using test runners that control a browser context
  await window.localStorage.clear();
  ClearCache();
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
    let open = screen.getByText(Strings.select_a_bot);
    expect(open).toBeEnabled();
    let path = screen.getByText(Strings.select_a_file);
    expect(path).toBeDisabled();
    await act(async () => fireEvent.click(open));
    let select = screen.getByText('team2');
    expect(select).toBeEnabled();
    await act(async () => fireEvent.click(select));
    await waitFor(async () => {
      expect(await store.get(SelectedTeamAtom)).toBe('team2' as Team);
    });
    await waitFor(async () => {
      expect(await store.get(SelectedPathAtom)).toBe('' as Path);
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
      expect(await store.get(SelectedPathAtom)).toBe('path3.java' as Path);
    });
    await act(async () => {
      await store.set(SelectedTeamAtom, 'team3');
    });
    await act(async () => {
      expect(await store.get(PathsForSelectedTeamAtom)).toEqual([]);
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
      await store.set(SelectedPathAtom, 'path3.java');
      await store.set(SelectedClassAtom, 'c');
    });
    await act(async () => {
      expect(await store.get(SelectedPathAtom)).toBe('path3.java' as Path);
    });
    expect(await store.get(ValuesLookupAtom)).toBeDefined();
    expect(await store.get(NamedPosesAtom)).toBeDefined();
    expect(await store.get(MappedBeziersAtom)).toBeDefined();
    /*await act(() =>
      store.set(ValueAtomFamily('valX' as ValueName), { int: 42 }),
    );
    await waitFor(async () => {
      expect(
        (await store.get(ValuesLookupAtom)).has('valX' as ValueName),
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
    );*/
  });
});
