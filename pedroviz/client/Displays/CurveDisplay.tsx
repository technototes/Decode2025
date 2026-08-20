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
import { isString } from '@freik/typechk';

import { isRef } from '../../CodeTypeCheck';
import { BezierName, BezierRef, NamedBezier } from '../../CodeTypes';
import { FocusedCurveAtom, NamedBeziersAtom } from '../state/UserCode';
import { InlinePoseRefDisplay } from './PoseDisplay';

export function InlineBezierRefDisplay({
  bezier,
}: {
  bezier: BezierRef;
}): ReactElement {
  if (isRef(bezier)) {
    return <code>{bezier}</code>;
  }
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap' }}>
      {bezier.points.map((pr, index, poseRefs) => {
        const comma = index !== poseRefs.length - 1;
        return (
          <span key={index}>
            <InlinePoseRefDisplay pose={pr} key={index} />
            {comma && ','}&nbsp;
          </span>
        );
      })}
    </div>
  );
}

const columns: TableColumnDefinition<NamedBezier>[] = [
  createTableColumn<NamedBezier>({
    columnId: 'name',
    compare: (a, b) => a.name.localeCompare(b.name),
    renderHeaderCell: () => <Text weight="bold">Name</Text>,
    renderCell: (nb) => <code>{nb.name}</code>,
  }),
  createTableColumn<NamedBezier>({
    columnId: 'points',
    // Let's not bother trying to sort points-lists.
    renderHeaderCell: () => <Text weight="bold">Points</Text>,
    renderCell: (nb) => <InlineBezierRefDisplay bezier={nb.points} />,
  }),
];

export function NamedBezierList(): ReactElement {
  const curves = useAtomValue(NamedBeziersAtom);
  const [focusedCurve, setFocusedCurve] = useAtom(FocusedCurveAtom);
  const selectedRows = new Set<TableRowId>(
    focusedCurve && [focusedCurve.name as TableRowId],
  );
  const onSelectionChange: DataGridProps['onSelectionChange'] = useCallback(
    (e, data) => {
      if (data.selectedItems.size === 0) {
        setFocusedCurve(undefined);
      } else {
        const item = [...data.selectedItems].pop();
        const ref = curves.find(
          (val) => isString(item) && val.name === (item as BezierName),
        );
        setFocusedCurve(ref);
      }
    },
    [curves],
  );
  // This enables deselection
  const maybeClearSelection = (id: BezierName) =>
    id === focusedCurve?.name && setFocusedCurve(undefined);
  return (
    <DataGrid
      items={curves}
      columns={columns}
      sortable
      resizableColumns
      getRowId={(item: NamedBezier) => item.name}
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
      <DataGridBody<NamedBezier>>
        {({ item, rowId }) => (
          <DataGridRow<NamedBezier>
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
