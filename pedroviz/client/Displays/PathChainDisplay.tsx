import { CSSProperties, ReactElement } from 'react';
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
  tokens,
} from '@fluentui/react-components';
import { isUndefined } from '@freik/typechk';

import { AnonymousFacing, FacingType, NamedPathChain } from '../../CodeTypes';
import { NamedPathChainsAtom } from '../state/Atoms';
import { InlineBezierRefDisplay } from './CurveDisplay';
import { InlinePoseRefDisplay } from './PoseDisplay';
import { HeadingRefDisplay } from './ValueDisplay';

function HeadingTypeDisplay({
  heading,
  ...props
}: {
  heading: AnonymousFacing;
  style?: CSSProperties;
}): ReactElement {
  if (isUndefined(heading)) {
    return <></>;
  }
  switch (heading.type) {
    case FacingType.Constant:
      return (
        <div>
          <Text {...props}>Constant:</Text>
          <br />
          <HeadingRefDisplay item={heading.heading} {...props} />
        </div>
      );
    case FacingType.Tangent:
      return <Text {...props}>Tangent</Text>;
    case FacingType.Linear:
      return (
        <div>
          <Text {...props}>Linear:</Text>
          <br />
          <span {...props}>
            <HeadingRefDisplay item={heading.start} />
            <Text> to </Text>
            <HeadingRefDisplay item={heading.end} />
          </span>
        </div>
      );
    case FacingType.Point:
      return (
        <div>
          <Text {...props}>Point:</Text>
          <br />
          <span {...props}>
            <InlinePoseRefDisplay pose={heading.point} />
          </span>
        </div>
      );
    case FacingType.Reversed:
      return <Text {...props}>Reversed...[TODO!]</Text>;
    case FacingType.Piecewise:
      return <Text {...props}>Piecewise heading...[TODO!]</Text>;
  }
}

const columns: TableColumnDefinition<NamedPathChain>[] = [
  createTableColumn<NamedPathChain>({
    columnId: 'name',
    compare: (a, b) => a.name.localeCompare(b.name),
    renderHeaderCell: () => <Text weight="semibold">Name</Text>,
    renderCell: (nv) => <code>{nv.name}</code>,
  }),
  createTableColumn<NamedPathChain>({
    columnId: 'paths',
    renderHeaderCell: () => <Text weight="semibold">Paths</Text>,
    renderCell: (nv) => (
      <div style={{ display: 'flex', flexDirection: 'column' }}>
        {nv.paths.map((br, index) => (
          <div
            key={index}
            style={{
              outlineColor: tokens.colorNeutralForegroundDisabled,
              outlineWidth: 2,
              outlineStyle: 'outset',
              padding: 3,
              margin: 3,
            }}>
            <InlineBezierRefDisplay bezier={br} />
          </div>
        ))}
      </div>
    ),
  }),
  createTableColumn<NamedPathChain>({
    columnId: 'heading',
    renderHeaderCell: () => <Text weight="semibold">Heading</Text>,
    renderCell: (nv) => <HeadingTypeDisplay heading={nv.heading} />,
  }),
];

export function PathChainList(): ReactElement {
  const npcs = useAtomValue(NamedPathChainsAtom);
  return (
    <DataGrid
      items={npcs}
      columns={columns}
      sortable
      resizableColumns
      getRowId={(npc: NamedPathChain) => npc.name}>
      <DataGridHeader>
        <DataGridRow>
          {({ renderHeaderCell }) => (
            <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
          )}
        </DataGridRow>
      </DataGridHeader>
      <DataGridBody<NamedPathChain>>
        {({ item, rowId }) => (
          <DataGridRow<NamedPathChain> key={rowId}>
            {({ renderCell }) => (
              <DataGridCell>{renderCell(item)}</DataGridCell>
            )}
          </DataGridRow>
        )}
      </DataGridBody>
    </DataGrid>
  );
}
