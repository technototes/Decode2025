import {
  Button,
  Dialog,
  DialogActions,
  DialogBody,
  DialogContent,
  DialogSurface,
  DialogTitle,
  DialogTrigger,
  Field,
  Input,
  InputProps,
  Radio,
  RadioGroup,
  RadioGroupProps,
  Select,
} from '@fluentui/react-components';
import { useAtomValue } from 'jotai';
import { useAtomCallback } from 'jotai/utils';
import { ReactElement, useState } from 'react';
import {
  AnonymousValue,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { MappedValuesAtom, ValueAtomFamily } from '../state/Atoms';
import { ValidationData, ValidData } from '../types';
import { CheckValidName } from './Validation';

type ValType = 'int' | 'double' | 'degrees';

export function NewValue(): ReactElement {
  const [name, setName] = useState<ValueName>('newValName' as ValueName);
  const [isVal, setIsVal] = useState<boolean>(true);
  const [valStr, setValStr] = useState('0.000');
  const [varStr, setVarStr] = useState('');
  const [valType, setValType] = useState<ValType>('double');
  const allNames = useAtomValue(MappedValuesAtom);
  const setNamedValue = useAtomCallback((_, set, val: ValueRef | RadiansRef) =>
    set(ValueAtomFamily(name), val),
  );

  const checkValue = (vl: string): ValidationData => {
    if (valType !== 'int' && isNaN(Number.parseFloat(vl))) {
      return {
        message: 'Please enter a valid floating point number',
        state: 'error',
      };
    } else if (valType === 'int' && isNaN(Number.parseInt(vl))) {
      return { message: 'Please enter a valid integer', state: 'error' };
    }
    return ValidData;
  };

  const { message: validNameMessage, state: nameValidationState } =
    CheckValidName(allNames, name.trim() as ValueName, false);
  const { message: validValueMessage, state: valueValidationState } =
    checkValue(valStr);

  const saveEnabled =
    nameValidationState === 'none' && valueValidationState === 'none';
  const typeChange: RadioGroupProps['onChange'] = (_, data) => {
    switch (data.value) {
      case 'int':
      case 'double':
      case 'degrees':
        setValType(data.value);
        setValStr(formatNum(data.value, Number.parseFloat(valStr)));
    }
  };
  const valueChange: InputProps['onChange'] = (_, data) => {
    setValStr(data.value);
  };
  const nameChange: InputProps['onChange'] = (_, data) => {
    setName(data.value as ValueName);
  };

  const formatNum = (valType: ValType, val: number): string => {
    val = isNaN(val) ? 0 : val;
    switch (valType) {
      case 'int':
        return val.toFixed(0);
      case 'double':
        return val.toFixed(2);
      case 'degrees':
        return val.toFixed(1);
    }
  };

  const saveValue = () => {
    if (isVal) {
      const value = Number.parseFloat(valStr);
      const obj: AnonymousValue = Number.isInteger(value)
        ? { int: value }
        : { double: value };
      if (valType === 'degrees') {
        setNamedValue({ radians: obj });
      } else {
        setNamedValue(obj);
      }
    } else {
      if (valType === 'degrees') {
        setNamedValue({ radians: varStr as ValueName });
      } else {
        setNamedValue(varStr as ValueName);
      }
    }
  };

  const label = (
    <Select
      value={isVal ? 'Value' : 'Variable'}
      onChange={(_, data) => setIsVal(data.value === 'Value')}
    >
      <option>Value</option>
      <option>Variable</option>
    </Select>
  );

  const valOrVar = isVal ? (
    <Input value={valStr} onChange={valueChange} />
  ) : (
    <Select value={varStr} onChange={(_, data) => setVarStr(data.value)}>
      {[...allNames.keys().map((nv) => <option key={nv}>{nv}</option>)]}
    </Select>
  );

  return (
    <Dialog>
      <DialogTrigger disableButtonEnhancement>
        <Button style={{ margin: 10 }}>New Value</Button>
      </DialogTrigger>
      <DialogSurface>
        <DialogBody>
          <DialogTitle>New value field</DialogTitle>
          <DialogContent>
            <div className="col3div">
              <Field className="col1" label="Type">
                <RadioGroup value={valType} onChange={typeChange}>
                  <Radio value="double" label="double" />
                  <Radio value="int" label="int" />
                  <Radio value="degrees" label="degrees" />
                </RadioGroup>
              </Field>
              <Field
                className="col2"
                label="Name"
                validationMessage={validNameMessage}
                validationState={nameValidationState}
              >
                <Input value={name.trim()} onChange={nameChange} />
              </Field>
              <Field
                className="col3"
                label={label}
                validationMessage={validValueMessage}
                validationState={valueValidationState}
              >
                {valOrVar}
              </Field>
            </div>
          </DialogContent>
          <DialogActions>
            <DialogTrigger disableButtonEnhancement>
              <Button
                disabled={!saveEnabled}
                appearance="primary"
                onClick={saveValue}
              >
                Save
              </Button>
            </DialogTrigger>
            <DialogTrigger disableButtonEnhancement>
              <Button appearance="secondary">Cancel</Button>
            </DialogTrigger>
          </DialogActions>
        </DialogBody>
      </DialogSurface>
    </Dialog>
  );
}
