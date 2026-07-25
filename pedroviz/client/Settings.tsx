import { ReactElement } from 'react';
import { useAtom } from 'jotai';

import {
  Button,
  Dialog,
  DialogActions,
  DialogBody,
  DialogContent,
  DialogSurface,
  DialogTitle,
  DialogTrigger,
  Switch,
  Text,
} from '@fluentui/react-components';
import {
  SettingsFilled,
  WeatherMoonFilled,
  WeatherSunnyRegular,
} from '@fluentui/react-icons';

import { Strings } from './constants';
import { ThemeAtom } from './state/Atoms';

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  return (
    <Dialog modalType="non-modal">
      <DialogTrigger disableButtonEnhancement>
        <Button icon={<SettingsFilled />} appearance="transparent" />
      </DialogTrigger>
      <DialogSurface>
        <DialogBody>
          <DialogTitle>Settings</DialogTitle>
          <DialogContent>
            <div style={{ display: 'grid', gridTemplateColumns: 'auto auto' }}>
              <Text>Show heading</Text>
              <Text>TODO [button]</Text>
              <Text>Heading Spacing</Text>
              <Text>TODO</Text>
              <Text>Clear settings</Text>
              <Button
                onClick={() => {
                  localStorage.clear();
                  window.location.reload();
                }}>
                {Strings.Reset}
              </Button>
              <Text>Theme</Text>
              <span>
                {' '}
                <WeatherSunnyRegular />
                <Switch
                  checked={theTheme === 'dark'}
                  onChange={(_, data) =>
                    setTheme(data.checked ? 'dark' : 'light')
                  }
                />
                <WeatherMoonFilled />
              </span>
            </div>
          </DialogContent>
        </DialogBody>
      </DialogSurface>
    </Dialog>
  );
}
