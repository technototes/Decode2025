import {
  Button,
  Combobox,
  ComboboxProps,
  Dialog,
  DialogBody,
  DialogSurface,
  DialogTrigger,
  Field,
  Option,
  Text,
} from '@fluentui/react-components';
import { EditRegular } from '@fluentui/react-icons';
import { useSetAtom } from 'jotai';
import { CSSProperties, ReactElement, useState } from 'react';
import { isIntValue, isValueName, ValueRef } from '../../server/types';
import { BlurAtom } from '../state/Atoms';
import { GetValueAsString, HasKeys } from '../types';
import {
  CheckValidValueOrName,
  IsValidJavaIdentifier,
  IsValidNumber,
} from './Validation';

export function NumberOrNamedValue<T extends string, U extends HasKeys<T>>({
  style,
  names,
  placeholder,
  value,
  setValue,
}: {
  style?: CSSProperties;
  names: U;
  placeholder?: string;
  value: ValueRef;
  setValue: (val: string) => void;
}): ReactElement {
  const options = [...names.keys()] as string[];
  const setBlur = useSetAtom(BlurAtom);
  const optsLC = options.map((opt) => opt.toLowerCase());
  const [matchingOptions, setMatchingOptions] = useState(options);
  const [customSearch, setCustomSearch] = useState<string | undefined>();
  const [curValue, setCurValue] = useState<string>(GetValueAsString(value));
  let { message, state } = CheckValidValueOrName(names, curValue || '', true);
  const onChange: ComboboxProps['onChange'] = (event) => {
    const value = event.target.value.trim();
    // Only filter values that might be potential names. Otherwise, treat it like a possible value
    if (IsValidJavaIdentifier(value)) {
      const valLC = value.toLowerCase();
      const matches = options.filter((_, index) =>
        optsLC[index].startsWith(valLC),
      );
      setMatchingOptions(matches);
      if (value.length && matches.length < 1) {
        setCustomSearch(value);
      } else {
        setCustomSearch(undefined);
      }
    } else if (matchingOptions.length !== options.length) {
      // We have a value: Set the match options to the original set
      setMatchingOptions(options);
    } else if (IsValidNumber(value)) {
      setValue(value);
    }
    if (state !== 'error') {
      setValue(value);
    } else {
      setCurValue(value);
      setBlur(value);
    }
  };

  const onOptionSelect: ComboboxProps['onOptionSelect'] = (_, data) => {
    const matchingOption = data.optionText && options.includes(data.optionText);
    if (matchingOption) {
      setCustomSearch(undefined);
      // setValue(data.optionText);
    } else {
      setCustomSearch(data.optionText);
    }
    ({ message, state } = CheckValidValueOrName(
      names,
      data.optionText || '',
      true,
    ));
    if (state !== 'error') {
      setValue(data.optionText);
    } else {
      setCurValue(data.optionText);
      setBlur(data.optionText);
    }
  };

  return (
    <span>
      <Text>{curValue}&nbsp;</Text>
      <Dialog>
        <DialogTrigger>
          <Button size="small" icon={<EditRegular />} />
        </DialogTrigger>
        <DialogSurface>
          <DialogBody>
            <Field
              style={style}
              validationMessage={message}
              validationState={state}
            >
              <Combobox
                freeform
                placeholder={placeholder || 'Select a variable'}
                onChange={onChange}
                onOptionSelect={onOptionSelect}
                defaultValue={
                  isValueName(value)
                    ? value
                    : isIntValue(value)
                      ? value.int.toFixed(0)
                      : value.double.toFixed(2)
                }
              >
                {customSearch ? (
                  <Option key="freeform" text={customSearch}>
                    Search for "{customSearch}"
                  </Option>
                ) : null}
                {matchingOptions.map((option) => (
                  <Option key={option}>{option}</Option>
                ))}
              </Combobox>
            </Field>
          </DialogBody>
        </DialogSurface>
      </Dialog>
    </span>
  );
}
