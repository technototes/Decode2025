import { ReactElement } from 'react';

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
} from '@fluentui/react-components';
import { useAtomValue, useSetAtom } from 'jotai';
import { useState } from 'react';
import { NamedValue } from '../server/types';
import { AllNamesAtom, NamedValuesAtom } from './state/Atoms';

const validName: RegExp = /^[A-Za-z_][a-zA-Z0-9_]*$/;

export function NewNamedValue(): ReactElement {
  const allNames = useAtomValue(AllNamesAtom);

  const setNamedValue = useSetAtom(NamedValuesAtom);
  const [name, setName] = useState('newValName');
  const [valStr, setValStr] = useState('0.000');
  const [valType, setValType] = useState<'int' | 'double' | 'degrees'>(
    'double',
  );

  const checkName = (nm: string): [string, 'error' | 'none'] => {
    if (allNames.has(nm)) {
      return ['Please use a unique name.', 'error'];
    } else if (!validName.test(nm)) {
      return ['Please enter a valid Java variable name.', 'error'];
    }
    return ['', 'none'];
  };
  const checkValue = (vl: string): [string, 'error' | 'none'] => {
    if (valType !== 'int' && isNaN(Number.parseFloat(vl))) {
      return ['Please enter a valid floating point number', 'error'];
    } else if (valType === 'int' && isNaN(Number.parseInt(vl))) {
      return ['Please enter a valid integer', 'error'];
    }
    return ['', 'none'];
  };

  const [validNameMessage, nameValidationState] = checkName(name.trim());
  const [validValueMessage, valueValidationState] = checkValue(valStr);

  const saveEnabled =
    nameValidationState === 'none' && valueValidationState === 'none';
  const typeChange: RadioGroupProps['onChange'] = (_, data) => {
    const numericVal = Number.parseFloat(valStr);
    switch (data.value) {
      case 'int':
        setValType('int');
        setValStr(isNaN(numericVal) ? '0' : numericVal.toFixed(0));
        break;
      case 'double':
        setValType('double');
        setValStr(isNaN(numericVal) ? '0.0' : numericVal.toFixed(3));
        break;
      case 'degrees':
        setValType('degrees');
        setValStr(isNaN(numericVal) ? '0.0' : numericVal.toFixed(1));
        break;
    }
  };
  const valueChange: InputProps['onChange'] = (_, data) => {
    setValStr(data.value);
  };
  const nameChange: InputProps['onChange'] = (_, data) => {
    setName(data.value);
  };

  const formatNum = (val: number): string => {
    switch (valType) {
      case 'int':
        return val.toFixed(0);
      case 'double':
        return val.toFixed(3);
      case 'degrees':
        return val.toFixed(1);
    }
  };

  const saveValue = () => {
    const nv: NamedValue = {
      name,
      value: {
        type: valType === 'degrees' ? 'radians' : valType,
        value: Number.parseFloat(valStr),
      },
    };
    setNamedValue(nv);
  };

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
                label="Value"
                validationMessage={validValueMessage}
                validationState={valueValidationState}
              >
                <Input value={valStr} onChange={valueChange} />
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
