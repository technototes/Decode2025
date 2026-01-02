import { Field, Input, InputProps, Text } from '@fluentui/react-components';
import { useAtom, useAtomValue } from 'jotai';
import { CSSProperties, ReactElement, useState } from 'react';
import {
  AnonymousValue,
  isDoubleValue,
  isIntValue,
  isRadiansRef,
  isValueName,
  ValueName,
  ValueRef,
} from '../../server/types';
import { MappedValuesAtom, ValueAtomFamily } from '../state/Atoms';
import { ItemWithStyle } from '../ui-tools/types';
import { NumberOrNamedValue } from './NumberOrNamedValueEditor';
import { CheckValidName, ValRefFromString } from './Validation';

export function AnonymousValueDisplay({
  item,
  ...props
}: ItemWithStyle<AnonymousValue>): ReactElement {
  if (isDoubleValue(item)) {
    return <Text {...props}>{item.double.toFixed(2)}</Text>;
  } else {
    return <Text {...props}>{item.int.toFixed(0)}</Text>;
  }
}

export function EditableValueRef({
  initial,
  setRef,
  style,
}: {
  initial: ValueName;
  setRef: (val: ValueName) => void;
  style?: CSSProperties;
}): ReactElement {
  const validRefs = useAtomValue(MappedValuesAtom);
  const [curVal, setCurVal] = useState(initial);
  let { message: validNameMessage, state: nameValidationState } =
    CheckValidName(validRefs, curVal, true);
  const onChange: InputProps['onChange'] = (_, data) => {
    ({ message: validNameMessage, state: nameValidationState } = CheckValidName(
      validRefs,
      data.value.trim() as ValueName,
      true,
    ));
    if (nameValidationState == 'none') {
      setRef(data.value.trim() as ValueName);
    }
    setCurVal(data.value as ValueName);
  };
  return (
    <Field
      style={style}
      validationMessage={validNameMessage}
      validationState={nameValidationState}
    >
      <Input
        type="text"
        value={curVal}
        onChange={onChange}
        input={{ style: { textAlign: 'right' } }}
      />
    </Field>
  );
}

/*
import {
  Combobox,
  makeStyles,
  Option,
  useId,
} from "@fluentui/react-components";
import type { ComboboxProps } from "@fluentui/react-components";

const useStyles = makeStyles({
  root: {
    // Stack the label above the field with a gap
    display: "grid",
    gridTemplateRows: "repeat(1fr)",
    justifyItems: "start",
    gap: "2px",
    maxWidth: "400px",
  },
});
*/

export function EditableValueExpr({
  initial,
  setVal,
  precision,
}: {
  initial: number;
  setVal: (v: AnonymousValue) => void;
  precision: number;
}): ReactElement {
  const onChangeVal: InputProps['onChange'] = (_, data) => {
    const newVal = Number.parseFloat(data.value);
    if (!isNaN(newVal)) {
      if (precision === 0) {
        setVal({ int: Math.round(newVal) });
      } else {
        setVal({ double: newVal });
      }
    }
  };
  return (
    <Input
      type="number"
      value={initial.toFixed(precision)}
      onChange={onChangeVal}
      input={{ style: { textAlign: 'right' } }}
    />
  );
}

function getNumber(val: AnonymousValue): number {
  return isDoubleValue(val) ? val.double : val.int;
}

export function NamedValueElem({ name }: { name: ValueName }): ReactElement {
  const [item, setItem] = useAtom(ValueAtomFamily(name));
  const names = useAtomValue(MappedValuesAtom);
  const type = isRadiansRef(item)
    ? 'degrees'
    : isIntValue(item)
      ? 'int'
      : 'double';
  let editable: ReactElement;
  if (isRadiansRef(item)) {
    editable = (
      <NumberOrNamedValue
        names={names}
        value={item.radians}
        placeholder="RADIANS!"
        setValue={(val) => setItem({ radians: ValRefFromString(val) })}
      />
      /*<EditableValueRef
          initial={item.radians}
          setRef={(nm) => setItem({ radians: nm })}
        />*/
    );
    /*    if (isRef(item.radians)) {
    } else {
      editable = (
        <EditableValueExpr
          initial={getNumber(item.radians)}
          setVal={(av) => setItem({ radians: av })}
          precision={1}
        />
      );
    }*/
  } else {
    //    editable = <EditableOnlyValueRef ref={item} setRef={setItem} />;
    editable = (
      <NumberOrNamedValue
        names={names}
        value={item}
        placeholder="NotRadians"
        setValue={(val) => setItem(ValRefFromString(val))}
      />
    );
  }
  return (
    <>
      <Text>{name}</Text>
      {editable}
      <Text>{type}</Text>
    </>
  );
}

export function EditableOnlyValueRef({
  ref,
  setRef,
}: {
  ref: ValueRef;
  setRef: (v: ValueRef) => void;
}): ReactElement {
  let editable: ReactElement;
  if (isValueName(ref)) {
    editable = <EditableValueRef initial={ref} setRef={setRef} />;
  } else {
    editable = (
      <EditableValueExpr
        initial={getNumber(ref)}
        setVal={setRef}
        precision={isIntValue(ref) ? 0 : 2}
      />
    );
  }
  return editable;
}

export function NamedValueList(): ReactElement {
  const items = useAtomValue(MappedValuesAtom);
  const names = [...items.keys()];
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto auto',
    justifyItems: 'end',
    justifySelf: 'start',
    alignItems: 'center',
  };

  return (
    <div style={gridStyle}>
      <Text size={400}>Name {names.length}</Text>
      <Text size={400}>Value</Text>
      <Text size={400}>Units</Text>
      {names.map((val) => (
        <NamedValueElem key={val} name={val} />
      ))}
    </div>
  );
}
