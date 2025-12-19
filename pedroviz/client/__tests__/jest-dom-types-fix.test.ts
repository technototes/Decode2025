import * as matchers from '@testing-library/jest-dom/matchers';
import { expect } from 'bun:test';
// Extend the expect object with custom matchers
expect.extend(matchers as any);

// Work around for typescript
import type { TestingLibraryMatchers } from '@testing-library/jest-dom/matchers';
declare module 'bun:test' {
  interface Matchers<T = unknown> extends TestingLibraryMatchers<
    ReturnType<typeof expect.stringContaining>,
    T
  > {}
}
