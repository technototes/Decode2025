import { ReactElement } from 'react';
import { useAtomValue } from 'jotai';

import {
  createTableColumn,
  DataGrid,
  DataGridBody,
  DataGridCell,
  DataGridHeader,
  DataGridHeaderCell,
  DataGridRow,
  TableColumnDefinition,
  Text,
} from '@fluentui/react-components';
import { isDefined } from '@freik/typechk';

import {
  isDoubleValue,
  isPoseName,
  isRadiansRef,
  isRef,
  isValueRef,
} from '../../CodeTypeCheck';
import {
  AnonymousValue,
  HeadingRef,
  NamedValue,
  PoseName,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../CodeTypes';
import { GetValueAsString } from '../ExpressionEval';
import { NamedValuesAtom } from '../state/Atoms';
import { ItemWithStyle } from '../ui-tools/types';

export function HeadingRefForSorting(item: HeadingRef | undefined): string {
  if (isRadiansRef(item)) {
    return GetValueAsString(item.radians) + ' degrees';
  }
  if (isPoseName(item)) {
    return item + '.getHeading()';
  }
  return isDefined(item) ? GetValueAsString(item) : '';
}

export function HeadingRefDisplay({
  item,
  ...props
}: ItemWithStyle<HeadingRef>): ReactElement {
  if (isDefined(item)) {
    if (isRadiansRef(item)) {
      return <RadiansRefDisplay item={item} {...props} />;
    } else if (isRef(item)) {
      return <GeneralRefDisplay item={item} {...props} />;
    } else {
      return <AnonymousValueDisplay item={item} {...props} />;
    }
  }
  return <> </>;
}

export function AnonymousValueDisplay({
  item,
  ...props
}: ItemWithStyle<AnonymousValue>): ReactElement {
  return <code {...props}>{GetValueAsString(item)}</code>;
}

export function UnnamedValueDisplay({
  item,
  ...props
}: ItemWithStyle<ValueRef | RadiansRef>): ReactElement {
  return isRadiansRef(item) ? (
    <RadiansRefDisplay item={item} {...props} />
  ) : (
    <ValueRefDisplay item={item} {...props} />
  );
}

/*

export function EditableValueRef({
  initial,
  setRef,
  style,
}: {
  initial: ValueName;
  setRef: (val: ValueName) => void;
  style?: CSSProperties;
}): ReactElement {
  const validRefs = useAtomValue(ValuesLookupAtom);
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
      validationState={nameValidationState}>
      <Input
        type="text"
        value={curVal}
        onChange={onChange}
        input={{ style: { textAlign: 'right' } }}
      />
    </Field>
  );
}

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
*/

function getNumber(val: AnonymousValue): number {
  return isDoubleValue(val) ? val.double : val.int;
}
/*
export function NamedValueElem({ name }: { name: ValueName }): ReactElement {
  const [item, setItem] = useAtom(ValueAtomFamily(name));
  const names = useAtomValue(ValuesLooukpAtom);
  const type = isRadiansRef(item)
    ? 'degrees'
    : isIntValue(item)
      ? 'int'
      : 'double';
  let editable: ReactElement;
  if (isRadiansRef(item)) {
    editable = (
      <NumberOrNamedValue
        names={names as unknown as HasKeys<string>}
        value={item.radians}
        placeholder="RADIANS!"
        setValue={(val) => setItem({ radians: ValRefFromString(val) })}
      />
      /*<EditableValueRef
          initial={item.radians}
          setRef={(nm) => setItem({ radians: nm })}
        />* //
    
    /*    if (isRef(item.radians)) {
    } else {
      editable = (
        <EditableValueExpr
          initial={getNumber(item.radians)}
          setVal={(av) => setItem({ radians: av })}
          precision={1}
        />
      );
    }* //
  } else if (isDefined(item)) {
    //    editable = <EditableOnlyValueRef ref={item} setRef={setItem} />;
    editable = (
      <NumberOrNamedValue
        names={names as unknown as HasKeys<string>}
        value={item}
        placeholder="NotRadians"
        setValue={(val) => setItem(ValRefFromString(val))}
      />
    );
  } else {
    return <></>;
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
*/

export function GeneralRefDisplay({
  item,
  ...props
}: ItemWithStyle<ValueName | PoseName>) {
  return <code {...props}>{item}</code>;
}

export function ValueRefDisplay({
  item,
  ...props
}: ItemWithStyle<ValueRef>): ReactElement {
  return isRef(item) ? (
    <GeneralRefDisplay item={item} {...props} />
  ) : (
    <AnonymousValueDisplay item={item} {...props} />
  );
}

function MathToRadianDisplay({
  item,
  ...props
}: ItemWithStyle<ValueRef>): ReactElement {
  return (
    <>
      <ValueRefDisplay item={item} {...props} />
      <Text {...props}>&nbsp;degrees</Text>
    </>
  );
}

export function RadiansRefDisplay({
  item,
  ...props
}: ItemWithStyle<RadiansRef>): ReactElement {
  return <MathToRadianDisplay item={item.radians} {...props} />;
}

export function GetVal(ref: NamedValue) {
  return GetValueAsString(
    isValueRef(ref.value) ? ref.value : ref.value.radians,
  );
}

const columns: TableColumnDefinition<NamedValue>[] = [
  createTableColumn<NamedValue>({
    columnId: 'name',
    compare: (a, b) => a.name.localeCompare(b.name),
    renderHeaderCell: () => <Text weight="bold">Name</Text>,
    renderCell: (nv) => <code>{nv.name}</code>,
  }),
  createTableColumn<NamedValue>({
    columnId: 'value',
    compare: (a, b) => {
      const av = GetVal(a);
      const bv = GetVal(b);
      return av.localeCompare(bv);
    },
    renderHeaderCell: () => <Text weight="bold">Value</Text>,
    renderCell: (nv) =>
      isValueRef(nv.value) ? (
        <code>{GetVal(nv)}</code>
      ) : (
        <span>
          <code>{GetVal(nv)}</code>&nbsp;degrees
        </span>
      ),
  }),
];

export function NamedValueList(): ReactElement {
  const items = useAtomValue(NamedValuesAtom);
  return (
    <DataGrid
      items={items}
      columns={columns}
      sortable
      getRowId={(itm: NamedValue) => itm.name}>
      <DataGridHeader>
        <DataGridRow>
          {({ renderHeaderCell }) => (
            <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
          )}
        </DataGridRow>
      </DataGridHeader>
      <DataGridBody<NamedValue>>
        {({ item, rowId }) => (
          <DataGridRow<NamedValue> key={rowId}>
            {({ renderCell, columnId }) => (
              <DataGridCell>{renderCell(item)}</DataGridCell>
            )}
          </DataGridRow>
        )}
      </DataGridBody>
    </DataGrid>
  );
}
