import {
  FluentProvider,
  webDarkTheme,
  webLightTheme,
} from '@fluentui/react-components';
import { Provider, useAtomValue } from 'jotai';
import { ReactElement, Suspense } from 'react';
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
        <div className="header">
          <div className="header-left">
            <PathSelector />
          </div>
          <div className="header-center">Viz4Pedro</div>
          <div className="header-right">
            <Settings />
          </div>
        </div>
        <div className="sidebar">
          <PathsDataDisplay />
        </div>
        <div className="display">
          <ScaledCanvas />
        </div>
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
