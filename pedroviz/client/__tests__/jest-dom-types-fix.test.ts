/// <reference path="../../node_modules/@testing-library/jest-dom/types/bun.d.ts" />
import * as matchers from '@testing-library/jest-dom/matchers';
import { expect } from 'bun:test';

declare global {
  var IS_REACT_ACT_ENVIRONMENT: boolean;
}

expect.extend(matchers as any);
