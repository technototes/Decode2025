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

import { isRef } from '../../CodeTypeCheck';
import { BezierRef, NamedBezier } from '../../CodeTypes';
import { NamedBeziersAtom } from '../state/Atoms';
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
  return (
    <DataGrid
      items={curves}
      columns={columns}
      sortable
      resizableColumns
      getRowId={(item: NamedBezier) => item.name}>
      <DataGridHeader>
        <DataGridRow>
          {({ renderHeaderCell }) => (
            <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
          )}
        </DataGridRow>
      </DataGridHeader>
      <DataGridBody<NamedBezier>>
        {({ item, rowId }) => (
          <DataGridRow<NamedBezier> key={rowId}>
            {({ renderCell }) => (
              <DataGridCell>{renderCell(item)}</DataGridCell>
            )}
          </DataGridRow>
        )}
      </DataGridBody>
    </DataGrid>
  );
}
