import { ReactElement, Suspense } from 'react';
import { Provider, useAtomValue } from 'jotai';

import {
  FluentProvider,
  webDarkTheme,
  webLightTheme,
} from '@fluentui/react-components';
import { Group, Panel, Separator } from 'react-resizable-panels';

import { Strings } from './constants';
import { PathsDataDisplay } from './PathsDataDisplay';
import { PathSelector } from './PathSelector';
import { Settings } from './Settings';
import { ThemeAtom } from './state/Atoms';
import { getStore } from './state/Storage';
import { ScaledCanvas } from './ui-tools/ScaledCanvas';

import './index.css';

export function MyApp(): ReactElement {
  return (
    <div className="app">
      <Suspense>
        <div className="header-left">
          <PathSelector />
        </div>
        <div className="header-center">{Strings.Viz4Pedro}</div>
        <div className="header-right">
          <Settings />
        </div>
        <Group className="main">
          <Panel className="sidebar">
            <PathsDataDisplay />
          </Panel>
          <Separator id="view-separator" />
          <Panel className="display">
            <ScaledCanvas />
          </Panel>
        </Group>
      </Suspense>
    </div>
  );
}

export function FluentApp(): ReactElement {
  const theTheme = useAtomValue(ThemeAtom);
  const theme = theTheme === 'dark' ? webDarkTheme : webLightTheme;

  return (
    <FluentProvider theme={theme}>
      <MyApp />
    </FluentProvider>
  );
}

export const App = () => (
  <Provider store={getStore()}>
    <FluentApp />
  </Provider>
);
