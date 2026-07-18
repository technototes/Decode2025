import { ReactElement } from 'react';
import { useAtom } from 'jotai';

import { Button, Switch } from '@fluentui/react-components';
import { WeatherMoonFilled, WeatherSunnyRegular } from '@fluentui/react-icons';

import { ThemeAtom } from './state/Atoms';

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  return (
    <span className="vcentered">
      <Button
        onClick={() => {
          localStorage.clear();
          window.location.reload();
        }}>
        Reset
      </Button>
      <span style={{ width: '10px' }} />
      <WeatherSunnyRegular />
      <Switch
        checked={theTheme === 'dark'}
        onChange={(_, data) => setTheme(data.checked ? 'dark' : 'light')}
      />
      <WeatherMoonFilled />
      <span style={{ width: '10px' }} />
    </span>
  );
}
