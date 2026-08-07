import { ReactElement, Suspense } from 'react';
import { Provider, useAtomValue } from 'jotai';

import {
  FluentProvider,
  webDarkTheme,
  webLightTheme,
} from '@fluentui/react-components';
import { Group, Panel, Separator } from 'react-resizable-panels';

import { PathsDataDisplay } from './PathsDataDisplay';
import { PathSelector } from './PathSelector';
import { Settings } from './Settings';
import { ThemeAtom } from './state/SavedSettings';
import { getStore } from './state/Storage';

import './index.css';

import { FieldRenderer } from './FieldRenderer';

export function MyApp(): ReactElement {
  return (
    <Suspense>
      <Group className="main">
        <Panel className="sidebar">
          <div className="header-left">
            <PathSelector />
          </div>
          <div className="header-right">
            <Settings />
          </div>
          <PathsDataDisplay />
        </Panel>
        <Separator id="view-separator" />
        <Panel className="display">
          <FieldRenderer />
        </Panel>
      </Group>
    </Suspense>
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
