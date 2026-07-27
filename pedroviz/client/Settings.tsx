import { ReactElement } from 'react';
import { useAtom, useAtomValue } from 'jotai';

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
import {
  CtrlPtRadiusAtom,
  CtrlPtThicknessAtom,
  HeadingCountAtom,
  HeadingLengthAtom,
  HeadingThicknessAtom,
  PathThicknessAtom,
  ShowBotHeadingAtom,
  ShowFieldAtom,
  ThemeAtom,
} from './state/SavedSettings';

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  const [showField, setShowField] = useAtom(ShowFieldAtom);
  const [showBotHeading, setShowBotHeading] = useAtom(ShowBotHeadingAtom);
  const pathThickness = useAtomValue(PathThicknessAtom);
  const ctrlPtThickness = useAtomValue(CtrlPtThicknessAtom);
  const ctrlPtRadius = useAtomValue(CtrlPtRadiusAtom);
  const headingLength = useAtomValue(HeadingLengthAtom);
  const headingCount = useAtomValue(HeadingCountAtom);
  const headingThickness = useAtomValue(HeadingThicknessAtom);
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
              <Switch
                checked={showField}
                onChange={(_, data) => setShowField(data.checked)}
              />
              <Text>Path Thickness</Text>
              <Text>{pathThickness}</Text>
              <Text>CtrlPt Radius</Text>
              <Text>{ctrlPtRadius}</Text>
              <Text>CtrlPt Thickness</Text>
              <Text>{ctrlPtThickness}</Text>
              <Text>Show robot heading</Text>
              <Switch
                checked={showBotHeading}
                onChange={(_, data) => setShowBotHeading(data.checked)}
              />
              <Text>Heading Indicator Count</Text>
              <Text>{headingCount}</Text>
              <Text>Heading length</Text>
              <Text>{headingLength}</Text>
              <Text>Heading thickness</Text>
              <Text>{headingThickness}</Text>
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
