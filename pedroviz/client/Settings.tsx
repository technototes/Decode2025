import { ReactElement, useCallback } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import {
  Button,
  Dialog,
  DialogBody,
  DialogContent,
  DialogSurface,
  DialogTitle,
  DialogTrigger,
  SpinButton,
  SpinButtonChangeEvent,
  SpinButtonOnChangeData,
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
  CtrlPtSizeAtom,
  CtrlPtThicknessAtom,
  HeadingCountAtom,
  HeadingLengthAtom,
  HeadingThicknessAtom,
  PathThicknessAtom,
  ShowBotHeadingAtom,
  ShowFieldAtom,
  ShowFieldKeyAtom,
  ThemeAtom,
} from './state/SavedSettings';

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  const [showField, setShowField] = useAtom(ShowFieldAtom);
  const [showBotHeading, setShowBotHeading] = useAtom(ShowBotHeadingAtom);
  const [pathThickness, setPathThickness] = useAtom(PathThicknessAtom);
  const [showCoords, setShowCoords] = useAtom(ShowFieldKeyAtom);
  const changePathThickness = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setPathThickness(data.value);
      }
    },
    [setPathThickness],
  );
  const [ctrlPtThickness, setCtrlPtThickness] = useAtom(CtrlPtThicknessAtom);
  const changeCtrlPtThickness = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setCtrlPtThickness(data.value);
      }
    },
    [setCtrlPtThickness],
  );
  const [ctrlPtSize, setCtrlPtSize] = useAtom(CtrlPtSizeAtom);
  const changeCtrlPtSize = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setCtrlPtSize(data.value);
      }
    },
    [setCtrlPtSize],
  );
  const [headingLength, setHeadingLength] = useAtom(HeadingLengthAtom);
  const changeHeadingLength = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setHeadingLength(data.value);
      }
    },
    [setHeadingLength],
  );
  const [headingCount, setHeadingCount] = useAtom(HeadingCountAtom);
  const changeHeadingCount = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setHeadingCount(Math.round(data.value));
      }
    },
    [setHeadingCount],
  );
  const [headingThickness, setHeadingThickness] = useAtom(HeadingThicknessAtom);
  const changeHeadingThickness = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setHeadingThickness(data.value);
      }
    },
    [setHeadingThickness],
  );
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
              <Text>Show field coordinates</Text>
              <Switch
                checked={showCoords}
                onChange={(_, data) => setShowCoords(data.checked)}
              />
              <Text>Path Thickness</Text>
              <SpinButton
                value={pathThickness}
                onChange={changePathThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
              <Text>CtrlPt Size</Text>
              <SpinButton
                value={ctrlPtSize}
                onChange={changeCtrlPtSize}
                step={0.25}
                stepPage={1}
                min={0.5}
                max={4}
              />
              <Text>CtrlPt Thickness</Text>
              <SpinButton
                value={ctrlPtThickness}
                onChange={changeCtrlPtThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
              <Text>Show robot heading</Text>
              <Switch
                checked={showBotHeading}
                onChange={(_, data) => setShowBotHeading(data.checked)}
              />
              <Text>Heading Indicator Count</Text>
              <SpinButton
                disabled={!showBotHeading}
                value={headingCount}
                onChange={changeHeadingCount}
                step={1}
                stepPage={5}
                min={1}
                max={25}
              />
              <Text>Heading length</Text>
              <SpinButton
                disabled={!showBotHeading}
                value={headingLength}
                onChange={changeHeadingLength}
                step={1}
                stepPage={5}
                min={1}
                max={25}
              />
              <Text>Heading thickness</Text>
              <SpinButton
                disabled={!showBotHeading}
                value={headingThickness}
                onChange={changeHeadingThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
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
