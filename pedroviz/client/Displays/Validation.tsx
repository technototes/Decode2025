import { isArray } from '@freik/typechk';
import { ValueName, ValueRef } from '../../server/types';
import { HasItem, ValidationData, ValidData } from '../types';

const validName: RegExp = /^[A-Za-z_][a-zA-Z0-9_]*$/;
const validNumber: RegExp = /^-? *[0-9]*\.?[0-9]*( *e *[-+]? *[0-9]+)?$/;

export function IsValidJavaIdentifier(str: string): boolean {
  return validName.test(str);
}

export function IsValidNumber(str: string): boolean {
  return !Number.isNaN(parseFloat(str)) && validNumber.test(str.trim());
}

function has<T extends string, U extends HasItem<T>>(
  value: T,
  maps: U | U[],
): boolean {
  return (isArray(maps) ? maps : [maps]).some((mp) => mp.has(value));
}

export function CheckValidName<T extends string, U extends HasItem<T>>(
  validNames: U | U[],
  expr: T,
  exists: boolean,
): ValidationData {
  const trimmed = expr.trim();
  if (exists !== has(trimmed, validNames)) {
    return {
      message: exists
        ? 'Please enter an existing variable.'
        : 'Please enter a new/unique name.',
      state: 'error',
    };
  }
  return !IsValidJavaIdentifier(trimmed)
    ? {
        message: 'Please enter a valid Java variable name.',
        state: 'error',
      }
    : ValidData;
}

export function CheckValidValueOrName<T extends string, U extends HasItem<T>>(
  validNames: U | U[],
  expr: T,
  exists: boolean,
): ValidationData {
  return IsValidNumber(expr.trim())
    ? ValidData
    : CheckValidName(validNames, expr, exists);
}

export function ValRefFromString(str: string): ValueRef {
  if (IsValidNumber(str.trim())) {
    const num = parseFloat(str);
    if (Number.isInteger(num)) {
      return { int: num };
    }
    return { double: num };
  }
  return str as ValueName;
}
