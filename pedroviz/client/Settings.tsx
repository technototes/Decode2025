import { Switch } from '@fluentui/react-components';
import { useAtom } from 'jotai';
import { ReactElement } from 'react';
import { ThemeAtom } from './state/Atoms';

export function Settings(): ReactElement {
  const [theTheme, setTheme] = useAtom(ThemeAtom);
  return (
    <Switch
      checked={theTheme === 'dark'}
      onChange={(_, data) => setTheme(data.checked ? 'dark' : 'light')}
      label="Dark Mode"
    />
  );
}
