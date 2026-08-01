import { ReactElement, useCallback, useState } from 'react';
import { useAtom } from 'jotai';

import {
  Button,
  Dialog,
  DialogBody,
  DialogContent,
  DialogSurface,
  DialogTitle,
  DialogTrigger,
  Dropdown,
  DropdownProps,
  Label,
  Option,
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
  CtrlPtStyleAtom,
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
import { CtrlPtStyles } from './types';

function getName(s: CtrlPtStyles): string {
  switch (s) {
    case CtrlPtStyles.Circle:
      return 'Circle';
    case CtrlPtStyles.X:
      return 'X';
    case CtrlPtStyles.Crosshair:
      return 'Crosshair';
    case CtrlPtStyles.Triangle:
      return 'Triangle';
    case CtrlPtStyles.Square:
      return 'Square';
    case CtrlPtStyles.None:
      return 'Nothing';
  }
}

const ctrlPtStyles: CtrlPtStyles[] = [
  CtrlPtStyles.Circle,
  CtrlPtStyles.X,
  CtrlPtStyles.Crosshair,
  CtrlPtStyles.Triangle,
  CtrlPtStyles.Square,
  CtrlPtStyles.None,
];

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
  const [ctrlPtStyle, setCtrlPtStyle] = useAtom(CtrlPtStyleAtom);
  const [ctrlPtName, setCtrlPtName] = useState(getName(ctrlPtStyle));
  const onOptionSelect: DropdownProps['onOptionSelect'] = useCallback(
    (ev, data) => {
      if (data.selectedOptions.length > 0) {
        setCtrlPtStyle(data.selectedOptions[0] as CtrlPtStyles);
        setCtrlPtName(data.optionText ?? '');
      }
    },
    [setCtrlPtStyle, setCtrlPtName],
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
                gridTemplateColumns: '1.5fr 1fr .4fr 1.5fr 1fr',
                rowGap: 5,
                columnGap: 1,
                margin: 10,
                alignItems: 'center',
              }}>
              <Label htmlFor="showFieldId">Show field image</Label>
              <Switch
                id="showFieldId"
                checked={showField}
                onChange={(_, data) => setShowField(data.checked)}
              />
              <span />
              <Label htmlFor="showCoordsId">Show field coordinates</Label>
              <Switch
                id="showCoordsId"
                checked={showCoords}
                onChange={(_, data) => setShowCoords(data.checked)}
              />
              <Label htmlFor="pathThicknessId">Path Thickness</Label>
              <SpinButton
                id="pathThicknessId"
                value={pathThickness}
                onChange={changePathThickness}
                step={0.1}
                stepPage={1}
                min={0}
                max={2}
              />
              <span />
              <Label htmlFor="showBotHeadingId">Show robot heading</Label>
              <Switch
                id="showBotHeadingId"
                checked={showBotHeading}
                onChange={(_, data) => setShowBotHeading(data.checked)}
              />
              <Label htmlFor="ctrlPtSizeId">CtrlPt Size</Label>
              <SpinButton
                id="ctrlPtSizeId"
                value={ctrlPtSize}
                onChange={changeCtrlPtSize}
                step={0.25}
                stepPage={1}
                min={0.5}
                max={4}
              />
              <span />
              <Label htmlFor="headingCountId">Heading Indicator Count</Label>
              <SpinButton
                id="headingCountId"
                disabled={!showBotHeading}
                value={headingCount}
                onChange={changeHeadingCount}
                step={1}
                stepPage={5}
                min={1}
                max={25}
              />
              <Label htmlFor="ctrlPtThicknessId">CtrlPt Thickness</Label>
              <SpinButton
                id="ctrlPtThicknessId"
                value={ctrlPtThickness}
                onChange={changeCtrlPtThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
              <span />
              <Label htmlFor="headingThicknessId">Heading thickness</Label>
              <SpinButton
                id="headingThicknessId"
                disabled={!showBotHeading}
                value={headingThickness}
                onChange={changeHeadingThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
              <Label htmlFor="ctrlPtStyleId">CtrlPt Style</Label>
              <Dropdown
                style={{ minWidth: 50 }}
                id="ctrlPtStyleId"
                value={ctrlPtName}
                selectedOptions={[ctrlPtStyle]}
                onOptionSelect={onOptionSelect}>
                {ctrlPtStyles.map((s) => (
                  <Option key={s} text={getName(s)} value={s}>
                    {getName(s)}
                  </Option>
                ))}
              </Dropdown>
              <span />
              <Label htmlFor="headingLengthId">Heading length</Label>
              <SpinButton
                id="headingLengthId"
                disabled={!showBotHeading}
                value={headingLength}
                onChange={changeHeadingLength}
                step={1}
                stepPage={5}
                min={1}
                max={25}
              />
              <Label htmlFor="setThemeId">Theme</Label>
              <span>
                <WeatherSunnyRegular />
                <Switch
                  id="setThemeId"
                  checked={theTheme === 'dark'}
                  onChange={(_, data) =>
                    setTheme(data.checked ? 'dark' : 'light')
                  }
                />
                <WeatherMoonFilled />
              </span>
              <span />
              <Label htmlFor="resetPrefsId">Reset preferences</Label>
              <span>
                <Button
                  id="resetPrefsId"
                  onClick={() => {
                    localStorage.clear();
                    window.location.reload();
                  }}>
                  {Strings.Reset}
                </Button>
                <span />
              </span>
            </div>
          </DialogContent>
        </DialogBody>
      </DialogSurface>
    </Dialog>
  );
}
