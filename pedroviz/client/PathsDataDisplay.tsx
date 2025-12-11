import { Button, Input, InputProps, Text } from '@fluentui/react-components';
import { isDefined, isString } from '@freik/typechk';
import { useAtom, useAtomValue } from 'jotai';
import { CSSProperties, Fragment, ReactElement } from 'react';
import {
  AnonymousPose,
  AnonymousValue,
  chkRadiansRef,
  HeadingRef,
  HeadingType,
  isRef,
  NamedPathChain,
  PoseRef,
  RadiansRef,
  ValueRef,
} from '../server/types';
import { NewNamedValue } from './NewNamedValue';
import { getBezier, getColorFor, getPose } from './state/API';
import {
  ColorsAtom,
  NamedBeziersAtom,
  NamedPathChainsAtom,
  NamedPosesAtom,
  SelectedFileAtom,
  ValueAtomFor,
  ValueNamesAtom,
} from './state/Atoms';
import { Expando } from './ui-tools/Expando';

type ItemWithStyle<Type> = { item: Type; style?: CSSProperties };

function MathToRadianDisplay({
  item,
  ...props
}: ItemWithStyle<ValueRef>): ReactElement {
  return (
    <span>
      <ValueRefDisplay item={item} {...props} />
      <Text {...props}> degrees</Text>
    </span>
  );
}

function AnonymousValueDisplay({
  item,
  ...props
}: ItemWithStyle<AnonymousValue>): ReactElement {
  switch (item.type) {
    case 'radians':
      return (
        <MathToRadianDisplay
          item={{ type: 'double', value: item.value }}
          {...props}
        />
      );
    case 'double':
      return <Text {...props}>{item.value.toFixed(3)}</Text>;
    case 'int':
      return <Text {...props}>{item.value.toFixed(0)}</Text>;
  }
}

export function ValueRefDisplay({
  item,
  ...props
}: ItemWithStyle<ValueRef>): ReactElement {
  return isString(item) ? (
    <Text {...props}>{item}</Text>
  ) : (
    <AnonymousValueDisplay item={item} {...props} />
  );
}

function RadiansRefDisplay({
  item,
  ...props
}: ItemWithStyle<RadiansRef>): ReactElement {
  return <MathToRadianDisplay item={item.radians} {...props} />;
}

function HeadingRefDisplay({
  item,
  ...props
}: ItemWithStyle<HeadingRef>): ReactElement {
  if (isDefined(item)) {
    return chkRadiansRef(item) ? (
      <RadiansRefDisplay item={item} {...props} />
    ) : (
      <ValueRefDisplay item={item} {...props} />
    );
  }
  return <>&nbsp;</>;
}

export function NamedValueElem({ name }: { name: string }): ReactElement {
  const [val, setVal] = useAtom(ValueAtomFor(name));
  const onChange: InputProps['onChange'] = (_, data) => {
    const newVal = Number.parseFloat(data.value);
    if (!isNaN(newVal)) {
      const nv: AnonymousValue = { type: val.value.type, value: newVal };
      setVal(nv);
    }
  };
  return (
    <>
      <Text>{name}</Text>
      <Input
        type="number"
        value={val.value.value.toString()}
        onChange={onChange}
        input={{ style: { textAlign: 'right' } }}
      />
      <Text>
        {` ${val.value.type === 'radians' ? 'degrees' : val.value.type}`}
      </Text>
    </>
  );
}

export function NamedValueList(): ReactElement {
  const names = useAtomValue(ValueNamesAtom);
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto auto',
    justifyItems: 'end',
    justifySelf: 'start',
    alignItems: 'center',
  };

  return (
    <>
      <div style={gridStyle}>
        <Text size={400}>Name</Text>
        <Text size={400}>Value</Text>
        <Text size={400}>Units</Text>
        {names.map((val) => (
          <NamedValueElem key={val} name={val} />
        ))}
      </div>
      <NewNamedValue />
    </>
  );
}

export type AnonymousPoseDisplayProps = {
  pose: AnonymousPose;
  noHeading?: boolean;
};
export function AnonymousPoseDisplay({
  pose,
  noHeading,
}: AnonymousPoseDisplayProps): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  const style = { color: colors[getColorFor(pose)] };
  return noHeading ? (
    <>
      <ValueRefDisplay style={style} item={pose.x} />
      <ValueRefDisplay style={style} item={pose.y} />
    </>
  ) : (
    <>
      <ValueRefDisplay style={style} item={pose.x} />
      <ValueRefDisplay style={style} item={pose.y} />
      <HeadingRefDisplay style={style} item={pose.heading} />
    </>
  );
}

export function AnonymousPoseHeader({
  noHeading,
}: {
  noHeading?: boolean;
}): ReactElement {
  return noHeading ? (
    <>
      <Text size={400}>X</Text>
      <Text size={400}>Y</Text>
    </>
  ) : (
    <>
      <Text size={400}>X</Text>
      <Text size={400}>Y</Text>
      <Text size={400}>Heading</Text>
    </>
  );
}

export function NamedPoseList(): ReactElement {
  const poses = [...useAtomValue(NamedPosesAtom).values()];
  const colors = useAtomValue(ColorsAtom);
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto auto auto',
    justifyItems: 'end',
    justifySelf: 'start',
  };
  return (
    <>
      <div style={gridStyle}>
        <Text size={400}>Name</Text>
        <AnonymousPoseHeader />
        {poses.map((pose) => {
          const color = getColorFor(pose.pose);
          const style = { color: colors[color % colors.length] };
          return (
            <Fragment key={`pr-${pose.name}-1`}>
              <Text style={style}>{pose.name}</Text>
              <AnonymousPoseDisplay pose={pose.pose} />
            </Fragment>
          );
        })}
      </div>
      <Button style={{ margin: 10 }}>New Pose</Button>
    </>
  );
}

function InlinePoseRefDisplay({ pose }: { pose: PoseRef }): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  const ap = isRef(pose) ? getPose(pose) : pose;
  const color = getColorFor(ap);
  const style = { color: colors[color % colors.length] };
  return isRef(pose) ? (
    <Text style={style}>{pose}</Text>
  ) : (
    <Text style={style}>
      (<ValueRefDisplay item={pose.x} />, <ValueRefDisplay item={pose.y} />)
    </Text>
  );
}

type RowData = { offset: number; size: number };

// Generate the grid row start/end for a span starting at a *zero* based row index
// "start" and a row count height of "count".
function rowSpan(offset: number, rd: RowData): CSSProperties {
  return {
    gridRowStart: rd.offset + offset,
    gridRowEnd: rd.offset + rd.size + offset,
    alignSelf: 'center',
  };
}

export function NamedBezierList(): ReactElement {
  const beziers = [...useAtomValue(NamedBeziersAtom).values()];
  const colors = useAtomValue(ColorsAtom);
  const rowData: RowData[] = [];
  let count = 1;
  for (const b of beziers) {
    rowData.push({ offset: count, size: b.points.points.length });
    count += b.points.points.length;
  }
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto',
    justifyItems: 'end',
    justifySelf: 'start',
  };
  return (
    <>
      <div style={gridStyle}>
        <Text size={400}>Name</Text>
        <Text size={400}>Poses</Text>
        {beziers.map((nb, index) => {
          const color = getColorFor(nb.points);
          const style = {
            color: colors[color % colors.length],
            ...rowSpan(1, rowData[index]),
          };
          return (
            <Fragment key={`br-${nb.name}`}>
              <Text style={style}>{nb.name}</Text>
              {nb.points.points.map((pr, index) => (
                <InlinePoseRefDisplay
                  key={`br-${nb.name}-${index}-2`}
                  pose={pr}
                />
              ))}
            </Fragment>
          );
        })}
      </div>
      <Button style={{ margin: 10 }}>New Bezier</Button>
    </>
  );
}

function HeadingTypeDisplay({
  heading,
  ...props
}: {
  heading: HeadingType;
  style?: CSSProperties;
}): ReactElement {
  switch (heading.type) {
    case 'constant':
      return (
        <>
          <Text {...props}>Constant heading</Text>
          <HeadingRefDisplay item={heading.heading} {...props} />
        </>
      );
    case 'tangent':
      return (
        <>
          <Text {...props}>Tangent heading</Text>
          <span>&nbsp;</span>
        </>
      );
    case 'interpolated':
      return (
        <>
          <Text {...props}>Linear heading</Text>
          <span {...props}>
            <HeadingRefDisplay item={heading.headings[0]} />
            <Text> to </Text>
            <HeadingRefDisplay item={heading.headings[1]} />
          </span>
        </>
      );
  }
}

type NestedRowData = RowData & { children: RowData[] };

export function NamedPathChainDisplay({
  chain,
  rowdata,
}: {
  chain: NamedPathChain;
  rowdata: NestedRowData;
}): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  // This renders into a container grid that's 3 columns wide
  return (
    <>
      <Text style={rowSpan(1, rowdata)}>{chain.name}</Text>
      {chain.paths.map((br, index) => {
        const anonBez = getBezier(br);
        const color = getColorFor(anonBez);
        if (isRef(br)) {
          // Span both columns for a named curve
          return (
            <Text
              key={`npc-${br}-${index}`}
              style={{
                gridColumnStart: 2,
                gridColumnEnd: 4,
                justifySelf: 'center',
                color: colors[color % colors.length],
              }}
            >
              {br}
            </Text>
          );
        } else {
          const style = {
            color: colors[color % colors.length],
            ...rowSpan(1, rowdata.children[index]),
          };
          return (
            <Fragment key={`npc-${index}`}>
              <Text style={style}>{br.type}</Text>
              {br.points.map((pr, index) => (
                <InlinePoseRefDisplay key={index} pose={pr} />
              ))}
            </Fragment>
          );
        }
      })}
      <HeadingTypeDisplay heading={chain.heading} />
    </>
  );
}

export function PathChainList(): ReactElement {
  const pathChains = [...useAtomValue(NamedPathChainsAtom).values()];
  // I need to collect row spans for:
  // 1- The name, a running total of all prior path chains, plus a total count
  //    of this path's chains.
  // 2- The type/name of each bezier of the chain, which is a running count of
  //    the prior rows, plus the count of the current curve's control points
  let count = 1;
  let nestedRowData: NestedRowData[] = [];
  for (const pc of pathChains) {
    const children: RowData[] = [];
    const offset = count;
    for (const b of pc.paths) {
      const size = isRef(b) ? 1 : b.points.length;
      children.push({ offset: count, size });
      count += size;
    }
    count++; // Heading row
    nestedRowData.push({ offset, size: count - offset, children });
  }
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto auto',
    justifyItems: 'end',
    justifySelf: 'start',
  };
  return (
    <>
      <div style={gridStyle}>
        <Text size={400}>Name</Text>
        <Text
          size={400}
          style={{
            gridColumnStart: 2,
            gridColumnEnd: 4,
            justifySelf: 'center',
          }}
        >
          Paths
        </Text>
        {pathChains.map((npc, index) => (
          <NamedPathChainDisplay
            key={npc.name}
            chain={npc}
            rowdata={nestedRowData[index]}
          />
        ))}
      </div>
      <Button style={{ margin: 10 }}>New PathChain</Button>
    </>
  );
}

export function PathsDataDisplay() {
  const selFile = useAtomValue(SelectedFileAtom);
  if (selFile.length === 0) {
    return <div>Please select a file to view.</div>;
  }
  return (
    <>
      <Expando label="Values" indent={20} size={500} defaultShow={true}>
        <NamedValueList />
      </Expando>
      <Expando label="Poses" indent={20} size={500}>
        <NamedPoseList />
      </Expando>
      <Expando label="Bezier Lines/Curves" indent={20} size={500}>
        <NamedBezierList />
      </Expando>
      <Expando label="PathChains" indent={20} size={500}>
        <PathChainList />
      </Expando>
    </>
  );
}
