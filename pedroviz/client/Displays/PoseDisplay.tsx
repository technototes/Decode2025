import { ReactElement, useCallback } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import {
  createTableColumn,
  DataGrid,
  DataGridBody,
  DataGridCell,
  DataGridHeader,
  DataGridHeaderCell,
  DataGridProps,
  DataGridRow,
  TableColumnDefinition,
  TableRowId,
  Text,
} from '@fluentui/react-components';
import { isDefined, isString } from '@freik/typechk';

import { isPoseName, isRef } from '../../CodeTypeCheck';
import { AnonymousPose, NamedPose, PoseName, PoseRef } from '../../CodeTypes';
import { GetValueAsString } from '../ExpressionEval';
import {
  ColorsAtom,
  FocusedPoseAtom,
  NamedPosesAtom,
  PoseAtomFamily,
  ValuesLookupAtom,
} from '../state/UserCode';
import { HasKeys } from '../types';
import { ItemWithStyle } from '../ui-tools/types';
import { NumberOrNamedValue } from './NumberOrNamedValueEditor';
import { ValRefFromString } from './Validation';
import {
  HeadingRefDisplay,
  HeadingRefForSorting,
  ValueRefDisplay,
} from './ValueDisplay';

export function InlinePoseRefDisplay({
  pose,
}: {
  pose: PoseRef;
}): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  /*const ap = isRef(pose) ? getPose(pose) : pose;
  const color = getColorFor(ap);*/
  // const style = { color: colors[color % colors.length] };
  return isRef(pose) ? (
    <Text style={{/*style*/}}>{pose}</Text>
  ) : (
    <Text style={{/*style*/}}>
      (<ValueRefDisplay item={pose.x} />, <ValueRefDisplay item={pose.y} />)
    </Text>
  );
}

export type AnonymousPoseDisplayProps = {
  pose: AnonymousPose;
  noHeading?: boolean;
  setPose: (p: AnonymousPose) => void;
};
export function AnonymousPoseDisplay({
  pose,
  noHeading,
  setPose,
}: AnonymousPoseDisplayProps): ReactElement {
  // const colors = usAtomValue(ColorsAtom);
  const names = useAtomValue(ValuesLookupAtom);

  const style = {/* color: colors[getColorFor(pose)]*/};
  /*

      <EditableOnlyValueRef
        ref={pose.x}
        setRef={(px: ValueRef) => setPose({ ...pose, x: px })}
      />
      <EditableOnlyValueRef
        ref={pose.y}
        setRef={(py: ValueRef) => setPose({ ...pose, y: py })}
      />
*/
  return (
    <>
      <NumberOrNamedValue
        names={names as unknown as HasKeys<string>}
        placeholder="Enter a value or select a variable"
        value={pose.x}
        setValue={(str: string) =>
          setPose({ ...pose, x: ValRefFromString(str) })
        }
      />
      <NumberOrNamedValue
        names={names as unknown as HasKeys<string>}
        placeholder="Enter a value or select a variable"
        value={pose.y}
        setValue={(str: string) =>
          setPose({ ...pose, y: ValRefFromString(str) })
        }
      />
      {!noHeading && <HeadingRefDisplay style={style} item={pose.heading!} />}
    </>
  );
}

export function AnonymousPoseHeader({
  noHeading,
}: {
  noHeading?: boolean;
}): ReactElement {
  return (
    <>
      <Text size={400}>X</Text>
      <Text size={400}>Y</Text>
      {!noHeading && (
        <>
          <Text size={400}>Heading</Text>
          <Text size={400}>Units</Text>
        </>
      )}
    </>
  );
}

export function NamedPoseItem({
  item,
  style,
}: ItemWithStyle<PoseName>): ReactElement {
  const [pose, setPose] = useAtom(PoseAtomFamily(item));
  const names = useAtomValue(ValuesLookupAtom);
  if (isPoseName(pose)) {
    return <Text>{pose}</Text>;
  } else if (isDefined(pose)) {
    return (
      <>
        <Text style={style}>{item}</Text>
        <AnonymousPoseDisplay pose={pose} setPose={setPose} />
      </>
    );
  }
  return <></>;
}

const columns: TableColumnDefinition<NamedPose>[] = [
  createTableColumn<NamedPose>({
    columnId: 'name',
    compare: (a, b) => a.name.localeCompare(b.name),
    renderHeaderCell: () => <Text weight="semibold">Name</Text>,
    renderCell: (nv) => <code>{nv.name}</code>,
  }),
  createTableColumn<NamedPose>({
    columnId: 'X',
    compare: (a, b) => {
      const av: string = isPoseName(a.pose)
        ? a.pose
        : GetValueAsString(a.pose.x);
      const bv: string = isPoseName(b.pose)
        ? b.pose
        : GetValueAsString(b.pose.x);
      return av.localeCompare(bv);
    },
    renderHeaderCell: () => <Text weight="semibold">X</Text>,
    renderCell: (np) => (
      <code>{isPoseName(np.pose) ? np.pose : GetValueAsString(np.pose.x)}</code>
    ),
  }),
  createTableColumn<NamedPose>({
    columnId: 'Y',
    compare: (a, b) => {
      const av: string = isPoseName(a.pose) ? '' : GetValueAsString(a.pose.y);
      const bv: string = isPoseName(b.pose) ? '' : GetValueAsString(b.pose.y);
      return av.localeCompare(bv);
    },
    renderHeaderCell: () => <Text weight="semibold">Y</Text>,
    renderCell: (np) => (
      <code>{isPoseName(np.pose) ? '' : GetValueAsString(np.pose.y)}</code>
    ),
  }),
  createTableColumn<NamedPose>({
    columnId: 'Heading',
    compare: (a, b) => {
      const av = isPoseName(a.pose) ? '' : HeadingRefForSorting(a.pose.heading);
      const bv = isPoseName(b.pose) ? '' : HeadingRefForSorting(b.pose.heading);
      return av.localeCompare(bv);
    },
    renderHeaderCell: () => <Text weight="semibold">Heading</Text>,
    renderCell: (np) => {
      if (!isPoseName(np.pose) && isDefined(np.pose.heading)) {
        return <HeadingRefDisplay item={np.pose.heading} />;
      }
    },
  }),
];

export function NamedPoseList(): ReactElement {
  const poses = useAtomValue(NamedPosesAtom);
  const [focusedPose, setFocusedPose] = useAtom(FocusedPoseAtom);
  const selectedRows = new Set<TableRowId>(
    focusedPose && [focusedPose.name as TableRowId],
  );
  const onSelectionChange: DataGridProps['onSelectionChange'] = useCallback(
    (e, data) => {
      if (data.selectedItems.size === 0) {
        setFocusedPose(undefined);
      } else {
        const item = [...data.selectedItems].pop();
        const ref = poses.find(
          (val) => isString(item) && val.name === (item as PoseName),
        );
        setFocusedPose(ref);
      }
    },
    [poses],
  );
  // This enables deselection
  const maybeClearSelection = (id: PoseName) =>
    id === focusedPose?.name && setFocusedPose(undefined);
  return (
    <DataGrid
      items={poses}
      columns={columns}
      sortable
      resizableColumns
      getRowId={(itm: NamedPose) => itm.name}
      selectionMode="single"
      selectedItems={selectedRows}
      onSelectionChange={onSelectionChange}>
      <DataGridHeader>
        <DataGridRow>
          {({ renderHeaderCell }) => (
            <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
          )}
        </DataGridRow>
      </DataGridHeader>
      <DataGridBody<NamedPose>>
        {({ item, rowId }) => (
          <DataGridRow<NamedPose>
            key={rowId}
            selectionCell={{ radioIndicator: { 'aria-label': 'Select row' } }}
            onClick={() => maybeClearSelection(item.name)}>
            {({ renderCell }) => (
              <DataGridCell>{renderCell(item)}</DataGridCell>
            )}
          </DataGridRow>
        )}
      </DataGridBody>
    </DataGrid>
  );
}
