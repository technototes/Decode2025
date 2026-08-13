import { describe, expect, test } from 'bun:test';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import { ValueName } from '../../CodeTypes';
import { calcValue, GetValueAsString, readConstant } from '../ExpressionEval';

describe('Expression Evaluation', () => {
  test('Constants', () => {
    expect(readConstant('123')).toEqual(undefined);
    expect(readConstant('123.456')).toEqual(undefined);
    expect(readConstant('Math.PI')).toEqual(Math.PI);
    expect(readConstant('Math.E')).toEqual(Math.E);
  });
  test('Stringification', () => {
    expect(GetValueAsString({ int: 123 })).toEqual('123');
    expect(GetValueAsString({ double: 123.456 })).toEqual('123.46');
    expect(GetValueAsString('varName' as ValueName)).toEqual('varName');
  });
  test('calcValue', () => {
    expect(calcValue({ int: 123 }, EmptyParsedClass)).toEqual(123);
    expect(calcValue({ double: 12.3 }, EmptyParsedClass)).toEqual(12.3);
    expect(calcValue({ radians: { double: 180 } }, EmptyParsedClass)).toEqual(
      3.141592653589793,
    );
  });
});
