import { expect, Matchers } from 'bun:test';

import { TestingLibraryMatchers } from '@testing-library/jest-dom/matchers';
import * as matchers from '@testing-library/jest-dom/matchers';

expect.extend(matchers as any);

// Work around for typescript
/*
import type { TestingLibraryMatchers } from '@testing-library/jest-dom/matchers';
declare module 'bun:test' {
  interface Matchers<T = unknown> extends TestingLibraryMatchers<
    ReturnType<typeof expect.stringContaining>,
    T
  > {}
}
*/

declare module 'bun:test' {
  interface Matchers<T = unknown> extends TestingLibraryMatchers<
    typeof expect.stringContaining,
    T
  > {}
}
declare global {
  var IS_REACT_ACT_ENVIRONMENT: boolean;
}
