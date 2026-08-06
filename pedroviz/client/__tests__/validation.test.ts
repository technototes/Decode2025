import { describe, expect, test } from 'bun:test';

import { ValueName } from 'CodeTypes';

import {
  CheckValidName,
  CheckValidValueOrName,
  ValRefFromString,
} from '../Displays/Validation';
import { ValidateState, ValidationResult } from '../types';

describe('Validation', () => {
  test('CheckValidName stuff', () => {
    const values = new Map<string, number>([
      ['a', 1],
      ['b', 2],
    ]);
    const values2 = new Set<string>(['j', 'a']);
    expect(CheckValidName(values, 'a', true)).toEqual(
      ValidationResult('', ValidateState.None),
    );
    expect(CheckValidName(values, 'a', false)).toEqual(
      ValidationResult('Please enter a new/unique name.', ValidateState.Error),
    );
    expect(CheckValidName(values, 'c', true)).toEqual(
      ValidationResult(
        'Please enter an existing variable.',
        ValidateState.Error,
      ),
    );
    expect(CheckValidName(values, 'c', false)).toEqual(
      ValidationResult('', ValidateState.None),
    );
    expect(CheckValidName(values, 'bad Name', false)).toEqual(
      ValidationResult(
        'Please enter a valid Java variable name.',
        ValidateState.Error,
      ),
    );
    expect(CheckValidName([values, values2], 'a', true)).toEqual(
      ValidationResult('', ValidateState.None),
    );
    expect(CheckValidName([values, values2], 'a', false)).toEqual(
      ValidationResult('Please enter a new/unique name.', ValidateState.Error),
    );
    expect(CheckValidName([values, values2], 'j', false)).toEqual(
      ValidationResult('Please enter a new/unique name.', ValidateState.Error),
    );
    expect(CheckValidValueOrName(values, '\t0.0 ', true)).toEqual(
      ValidationResult('', ValidateState.None),
    );
    expect(CheckValidValueOrName(values, ' 0.0f', true)).toEqual(
      ValidationResult(
        'Please enter an existing variable.',
        ValidateState.Error,
      ),
    );
    expect(CheckValidValueOrName(values, '\t0.0 ', true)).toEqual(
      ValidationResult('', ValidateState.None),
    );
    expect(CheckValidValueOrName([values, values2], 'foo', true)).toEqual(
      ValidationResult(
        'Please enter an existing variable.',
        ValidateState.Error,
      ),
    );
    expect(CheckValidValueOrName([values, values2], '-.', false)).toEqual(
      ValidationResult(
        'Please enter a valid Java variable name.',
        ValidateState.Error,
      ),
    );
  });
  test('ValRefFromString', () => {
    const nam = ValRefFromString('fifty');
    expect(nam).toEqual('fifty' as ValueName);
    const dbl = ValRefFromString(' 1.1e-5 ');
    expect(dbl).toEqual({ double: 0.000011 });
    const int = ValRefFromString('235');
    expect(int).toEqual({ int: 235 });
  });
});
