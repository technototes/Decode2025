import { ReactElement, useCallback, useState } from 'react';
import { useAtom, WritableAtom } from 'jotai';

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
} from '@fluentui/react-components';
import {
  SettingsFilled,
  WeatherMoonFilled,
  WeatherSunnyRegular,
} from '@fluentui/react-icons';

import { Strings } from './constants';
import {
  PathHeadingCountAtom,
  PathHeadingLengthAtom,
  PathHeadingThicknessAtom,
  PathPointSizeAtom,
  PathPointStyleAtom,
  PathPointThicknessAtom,
  PathThicknessAtom,
  ShowFieldAtom,
  ShowFieldKeyAtom,
  ShowPathHeadingAtom,
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

function useSpinnerAtom(
  atom: WritableAtom<number, [number], void>,
): [
  number,
  (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => void,
] {
  const [val, setVal] = useAtom(atom);
  const callback = useCallback(
    (_ev: SpinButtonChangeEvent, data: SpinButtonOnChangeData) => {
      if (
        data.value !== undefined &&
        data.value !== null &&
        !Number.isNaN(data.value)
      ) {
        setVal(data.value);
      }
    },
    [setVal],
  );
  return [val, callback];
}

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  const [showField, setShowField] = useAtom(ShowFieldAtom);
  const [showBotHeading, setShowBotHeading] = useAtom(ShowPathHeadingAtom);
  const [showCoords, setShowCoords] = useAtom(ShowFieldKeyAtom);
  const [pathThickness, changePathThickness] =
    useSpinnerAtom(PathThicknessAtom);
  const [ctrlPtThickness, changeCtrlPtThickness] = useSpinnerAtom(
    PathPointThicknessAtom,
  );
  const [ctrlPtSize, changeCtrlPtSize] = useSpinnerAtom(PathPointSizeAtom);
  const [headingLength, changeHeadingLength] = useSpinnerAtom(
    PathHeadingLengthAtom,
  );
  const [headingCount, changeHeadingCount] =
    useSpinnerAtom(PathHeadingCountAtom);
  const [headingThickness, changeHeadingThickness] = useSpinnerAtom(
    PathHeadingThicknessAtom,
  );
  const [ctrlPtStyle, setCtrlPtStyle] = useAtom(PathPointStyleAtom);
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
            <div className="settings">
              <Label className="left-label" htmlFor="showFieldId">
                Show field image
              </Label>
              <Switch
                className="left-field"
                id="showFieldId"
                checked={showField}
                onChange={(_, data) => setShowField(data.checked)}
              />
              <Label className="right-label" htmlFor="showCoordsId">
                Show field coordinates
              </Label>
              <Switch
                id="showCoordsId"
                checked={showCoords}
                onChange={(_, data) => setShowCoords(data.checked)}
              />
              <Label className="left-label" htmlFor="pathThicknessId">
                Path Thickness
              </Label>
              <SpinButton
                className="left-field"
                id="pathThicknessId"
                value={pathThickness}
                onChange={changePathThickness}
                step={0.1}
                stepPage={1}
                min={0}
                max={2}
              />
              <Label className="right-label" htmlFor="showBotHeadingId">
                Show robot heading
              </Label>
              <Switch
                id="showBotHeadingId"
                checked={showBotHeading}
                onChange={(_, data) => setShowBotHeading(data.checked)}
              />
              <Label className="left-label" htmlFor="ctrlPtSizeId">
                CtrlPt Size
              </Label>
              <SpinButton
                className="left-field"
                id="ctrlPtSizeId"
                value={ctrlPtSize}
                onChange={changeCtrlPtSize}
                step={0.25}
                stepPage={1}
                min={0.5}
                max={4}
              />
              <Label className="right-label" htmlFor="headingCountId">
                Heading Indicator Count
              </Label>
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
              <Label className="left-label" htmlFor="ctrlPtThicknessId">
                CtrlPt Thickness
              </Label>
              <SpinButton
                className="left-field"
                id="ctrlPtThicknessId"
                value={ctrlPtThickness}
                onChange={changeCtrlPtThickness}
                step={0.1}
                stepPage={1}
                min={0.1}
                max={2}
              />
              <Label className="right-label" htmlFor="headingThicknessId">
                Heading thickness
              </Label>
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
              <Label className="left-label" htmlFor="ctrlPtStyleId">
                CtrlPt Style
              </Label>
              <Dropdown
                className="left-field"
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
              <Label className="right-label" htmlFor="headingLengthId">
                Heading length
              </Label>
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
              <Label id="setThemeLabelId" htmlFor="setThemeId">
                Theme
              </Label>
              <span id="setThemeSpanId">
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
              <Label className="right-label" htmlFor="resetPrefsId">
                Reset preferences
              </Label>
              <span>
                <Button
                  id="resetPrefsId"
                  onClick={() => {
                    localStorage.clear();
                    window.location.reload();
                  }}>
                  {Strings.Reset}
                </Button>
              </span>
            </div>
          </DialogContent>
        </DialogBody>
      </DialogSurface>
    </Dialog>
  );
}
