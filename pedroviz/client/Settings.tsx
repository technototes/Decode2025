import { ReactElement } from 'react';
import { useAtom } from 'jotai';

import {
  Button,
  Dialog,
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
          <DialogTitle style={{ textAlign: 'center' }}>Settings</DialogTitle>
          <DialogContent>
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                rowGap: 10,
                margin: 10,
              }}>
              <Text>Show field image</Text>
              <Text>Switch [TODO]</Text>
              <Text>Path Thickness</Text>
              <Text>TODO: [value]</Text>
              <Text>CtrlPt Thickness</Text>
              <Text>TODO: [value]</Text>
              <Text>Show heading</Text>
              <Text>TODO [button]</Text>
              <Text>Heading Spacing</Text>
              <Text>TODO [value]</Text>
              <Text>Heading length</Text>
              <Text>TODO [value]</Text>
              <Text>Heading thickness</Text>
              <Text>TODO [value]</Text>
              <Text>Reset preferences</Text>
              <span>
                <Button
                  onClick={() => {
                    localStorage.clear();
                    window.location.reload();
                  }}>
                  {Strings.Reset}
                </Button>
                <span />
              </span>
              <Text>Theme</Text>
              <span>
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
