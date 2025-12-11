import { expect, test } from 'bun:test';
import { darkOnWhite, GenerateColors, lightOnBlack } from '../ui-tools/Colors';

test('Color generation', () => {
  expect(darkOnWhite.length).toEqual(lightOnBlack.length);
  const colors = GenerateColors(63);
  expect(colors.length).toEqual(63);
  const seen: Set<string> = new Set();
  for (const color of colors) {
    expect(color.length).toEqual(7);
    expect(color[0]).toBe('#');
    expect(seen.has(color)).toBeFalse();
    seen.add(color);
  }
});
