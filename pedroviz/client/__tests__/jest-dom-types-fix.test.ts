/// <reference path="../../node_modules/@testing-library/jest-dom/types/bun.d.ts" />
import { expect } from 'bun:test';

import * as matchers from '@testing-library/jest-dom/matchers';

declare global {
  var IS_REACT_ACT_ENVIRONMENT: boolean;
}

expect.extend(matchers as any);
