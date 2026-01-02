import { expect, test } from 'bun:test';
import { CheckValidName, CheckValidValueOrName } from '../Displays/Validation';
import { ValidationResult } from '../types';


test("CheckValidName stuff", () => {
  const values = new Map<string, number>([['a',1], ['b',2]]);
  const values2 = new Set<string>(['j','a']);
  expect(CheckValidName(values, 'a', true)).toEqual(ValidationResult('', 'none'));
  expect(CheckValidName(values, 'a', false)).toEqual(ValidationResult('Please enter a new/unique name.', 'error'));
  expect(CheckValidName(values, 'c', true)).toEqual(ValidationResult('Please enter an existing variable.', 'error'));
  expect(CheckValidName(values, 'c', false)).toEqual(ValidationResult('', 'none'));
  expect(CheckValidName(values, 'bad Name', false)).toEqual(ValidationResult('Please enter a valid Java variable name.','error'));
  expect(CheckValidName([values, values2], 'a', true)).toEqual(ValidationResult('', 'none'));
  expect(CheckValidName([values, values2], 'a', false)).toEqual(ValidationResult('Please enter a new/unique name.', 'error'));
  expect(CheckValidName([values, values2], 'j', false)).toEqual(ValidationResult('Please enter a new/unique name.', 'error'));
  expect(CheckValidValueOrName(values, '\t0.0 ', true)).toEqual(ValidationResult('', 'none'));
  expect(CheckValidValueOrName(values, ' 0.0f', true)).toEqual(ValidationResult('Please enter an existing variable.', 'error'));
  expect(CheckValidValueOrName(values, '\t0.0 ', true)).toEqual(ValidationResult('', 'none'));
  expect(CheckValidValueOrName([values,values2], 'foo', true)).toEqual(ValidationResult('Please enter an existing variable.', 'error'));  
  expect(CheckValidValueOrName([values,values2], '-.', false)).toEqual(ValidationResult('Please enter a valid Java variable name.', 'error'));  
});
