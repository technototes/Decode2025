import { beforeEach, describe, expect, test } from 'bun:test';
import { useAtom, useAtomValue } from 'jotai';

import { act, renderHook } from '@testing-library/react';

import {
  CtrlPtSizeAtom,
  CtrlPtStyleAtom,
  CtrlPtThicknessAtom,
  HeadingCountAtom,
  HeadingLengthAtom,
  HeadingThicknessAtom,
  PathRenderOptionsAtom,
  PathThicknessAtom,
  ShowBotHeadingAtom,
  ShowFieldAtom,
  ShowFieldKeyAtom,
} from '../state/SavedSettings';
import { CtrlPtStyles, PathRenderOptions } from '../types';

globalThis.IS_REACT_ACT_ENVIRONMENT = true;
// or global.IS_REACT_ACT_ENVIRONMENT = true; depending on your environment
/*
const status = {
  status: 200,
  headers: { 'Content-Type': 'application/json' },
};
*/
const mockStorage: Storage = {
  store: {},
  getItem: (key) => (key in mockStorage.store ? mockStorage.store[key] : null),
  setItem: (key, value) => {
    mockStorage.store[key] = String(value);
  },
  removeItem: (key) => {
    delete mockStorage.store[key];
  },
  clear: () => {
    mockStorage.store = {};
  },
  get length() {
    return Object.keys(mockStorage.store).length;
  },
  key: (i) => Object.keys(mockStorage.store)[i] || null,
};

// Replace global localStorage in your test
Object.defineProperty(globalThis, 'localStorage', {
  value: mockStorage,
  writable: true,
  configurable: true,
});

type Prefs = Record<
  string,
  string | number | boolean | Record<string, number | string | boolean>
>;
let prefs: Prefs = {};
function update(update: Prefs): Prefs {
  prefs = { ...prefs, ...update };
  return prefs;
}

describe('storage atoms', () => {
  test('Team/Path interactions', async () => {
    let pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ShowField: true,
        PathThickness: 0.1,
        ShowCoords: true,
        Heading: {
          ArrowAngle: 0.31416,
          ArrowPercent: 0.15,
          Display: true,
          Count: 6,
          Length: 5,
          Thickness: 0.5,
        },
        ControlPoint: {
          Thickness: 0.4,
          Size: 2,
          Style: CtrlPtStyles.Circle,
        },
      }) as PathRenderOptions,
    );
    const ctlPtSize = await act(() =>
      renderHook(() => useAtom(CtrlPtSizeAtom)),
    );
    expect(ctlPtSize.result).toBeDefined();
    expect(ctlPtSize.result.current[0]).toEqual(2);
    ctlPtSize.result.current[1](3);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ControlPoint: {
          Thickness: 0.4,
          Size: 3,
          Style: CtrlPtStyles.Circle,
        },
      }) as PathRenderOptions,
    );
    const ctlPtStyle = await act(() =>
      renderHook(() => useAtom(CtrlPtStyleAtom)),
    );
    expect(ctlPtStyle.result).toBeDefined();
    expect(ctlPtStyle.result.current[0]).toEqual(CtrlPtStyles.Circle);
    ctlPtStyle.result.current[1](CtrlPtStyles.Square);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ControlPoint: {
          Thickness: 0.4,
          Size: 3,
          Style: CtrlPtStyles.Square,
        },
      }) as PathRenderOptions,
    );
    const ctlPtThickness = await act(() =>
      renderHook(() => useAtom(CtrlPtThicknessAtom)),
    );
    expect(ctlPtThickness.result).toBeDefined();
    expect(ctlPtThickness.result.current[0]).toEqual(0.4);
    ctlPtThickness.result.current[1](1.5);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ControlPoint: {
          Thickness: 1.5,
          Size: 3,
          Style: CtrlPtStyles.Square,
        },
      }) as PathRenderOptions,
    );
    const showBotHeading = await act(() =>
      renderHook(() => useAtom(ShowBotHeadingAtom)),
    );
    expect(showBotHeading.result).toBeDefined();
    expect(showBotHeading.result.current[0]).toEqual(true);
    showBotHeading.result.current[1](false);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        Heading: {
          ArrowAngle: 0.31416,
          ArrowPercent: 0.15,
          Display: false,
          Count: 6,
          Length: 5,
          Thickness: 0.5,
        },
      }) as PathRenderOptions,
    );
    const headingCount = await act(() =>
      renderHook(() => useAtom(HeadingCountAtom)),
    );
    expect(headingCount.result).toBeDefined();
    expect(headingCount.result.current[0]).toEqual(6);
    headingCount.result.current[1](17);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        Heading: {
          ArrowAngle: 0.31416,
          ArrowPercent: 0.15,
          Display: false,
          Count: 17,
          Length: 5,
          Thickness: 0.5,
        },
      }) as PathRenderOptions,
    );
    const headingLength = await act(() =>
      renderHook(() => useAtom(HeadingLengthAtom)),
    );
    expect(headingLength.result).toBeDefined();
    expect(headingLength.result.current[0]).toEqual(5);
    headingLength.result.current[1](10);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        Heading: {
          ArrowAngle: 0.31416,
          ArrowPercent: 0.15,
          Display: false,
          Count: 17,
          Length: 10,
          Thickness: 0.5,
        },
      }) as PathRenderOptions,
    );
    const headingThickness = await act(() =>
      renderHook(() => useAtom(HeadingThicknessAtom)),
    );
    expect(headingThickness.result).toBeDefined();
    expect(headingThickness.result.current[0]).toEqual(0.5);
    headingThickness.result.current[1](1.0);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        Heading: {
          ArrowAngle: 0.31416,
          ArrowPercent: 0.15,
          Display: false,
          Count: 17,
          Length: 10,
          Thickness: 1.0,
        },
      }) as PathRenderOptions,
    );
    const pathThickness = await act(() =>
      renderHook(() => useAtom(PathThicknessAtom)),
    );
    expect(pathThickness.result).toBeDefined();
    expect(pathThickness.result.current[0]).toEqual(0.1);
    pathThickness.result.current[1](0.2);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        PathThickness: 0.2,
      }) as PathRenderOptions,
    );
    const showField = await act(() => renderHook(() => useAtom(ShowFieldAtom)));
    expect(showField.result).toBeDefined();
    expect(showField.result.current[0]).toEqual(true);
    showField.result.current[1](false);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ShowField: false,
      }) as PathRenderOptions,
    );
    const showFieldKey = await act(() =>
      renderHook(() => useAtom(ShowFieldKeyAtom)),
    );
    expect(showFieldKey.result).toBeDefined();
    expect(showFieldKey.result.current[0]).toEqual(true);
    showFieldKey.result.current[1](false);
    pro = await act(() =>
      renderHook(() => useAtomValue(PathRenderOptionsAtom)),
    );
    expect(pro.result).toBeDefined();
    expect(pro.result.current).toEqual(
      update({
        ShowCoords: false,
      }) as PathRenderOptions,
    );
  });
});
