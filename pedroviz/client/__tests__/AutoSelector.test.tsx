/// <reference lib="dom" />

import { FluentProvider, webLightTheme } from '@fluentui/react-components';
import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, mock, test } from 'bun:test';
import { act } from 'react';
import { AutoSelector } from '../ui-tools/AutoSelector';
import './jest-dom-types-fix';

// I'm *really* new to all this UI testing. These are all *terrible* tests,
// but they're a start.
describe('AutoSelector tests', () => {
  test('one item', async () => {
    const setSel = mock((val: string) => {});
    await act(() =>
      render(
        <FluentProvider theme={webLightTheme}>
          <AutoSelector
            prompt="Test"
            items={['1']}
            selected=""
            setSelected={setSel}
          />
        </FluentProvider>,
      ),
    );
    const item = screen.getAllByRole('button');
    expect(item[0]).toBeEnabled();
    await waitFor(() => expect(setSel).toBeCalledWith('1'));
  });

  test('two items', async () => {
    const setSel = mock((val: string) => {});
    await act(() =>
      render(
        <FluentProvider theme={webLightTheme}>
          <AutoSelector
            prompt="Test"
            items={['1', '2']}
            selected=""
            setSelected={setSel}
          />
        </FluentProvider>,
      ),
    );
    const item = screen.getAllByRole('button');
    expect(item[0]).toBeEnabled();
    await waitFor(() => expect(setSel).toBeCalledTimes(0));
  });

  test('two items, default', async () => {
    let selItem = '';
    const setSel = mock((val: string) => {
      selItem = val;
    });
    await act(() =>
      render(
        <FluentProvider theme={webLightTheme}>
          <AutoSelector
            prompt="Test"
            items={['1', '2']}
            selected=""
            setSelected={setSel}
            default="2"
          />
        </FluentProvider>,
      ),
    );
    const item = screen.getAllByRole('button');
    expect(item.length).toBe(3);
    expect(item[0]).toBeEnabled();
    expect(selItem).toEqual('');
    await waitFor(() => expect(setSel).toBeCalledTimes(0));
    expect(selItem).toEqual('2');
    await waitFor(() => expect(setSel).toBeCalledTimes(1));
  });

  test('no items', async () => {
    const setSel = mock((val: string) => {});
    await act(() =>
      render(
        <FluentProvider theme={webLightTheme}>
          <AutoSelector
            prompt="Test"
            items={[]}
            selected=""
            setSelected={setSel}
          />
        </FluentProvider>,
      ),
    );
    const items = screen.getAllByText('Test');
    expect(items.length).toBe(2);
    expect(items[0]).toBeDisabled();
    await waitFor(() => expect(setSel).toBeCalledTimes(0));
  });
});
