import { describe, expect, test } from 'bun:test';

import { chkFieldOf, isDefined, isNumber, isString } from '@freik/typechk';

import { ValueName } from '../../CodeTypes';
import { GetValueAsString, readConstant } from '../ExpressionEval';

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
});
