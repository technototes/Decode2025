import { Button, Text } from '@fluentui/react-components';
import { isDefined } from '@freik/typechk';
import { useAtomValue } from 'jotai';
import { CSSProperties, Fragment, ReactElement } from 'react';
import {
  HeadingRef,
  HeadingType,
  isRadiansRef,
  isRef,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../server/types';
// import { getBezier, getColorFor, getPose } from './state/API';
import { NewPose } from './Displays/NewPose';
import { NewValue } from './Displays/NewValue';
import { NamedPoseList } from './Displays/PoseDisplay';
import { AnonymousValueDisplay, NamedValueList } from './Displays/ValueDisplay';
import { getColorFor } from './state/API';
import {
  ColorsAtom,
  MappedBeziersAtom,
  MappedPathChainsAtom,
  SelectedFileAtom,
} from './state/Atoms';
import { AnonymousPathChain } from './types';
import { Expando } from './ui-tools/Expando';
import { ItemWithStyle } from './ui-tools/types';

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

export function GeneralRefDisplay({
  item,
  ...props
}: ItemWithStyle<ValueName | PoseName>) {
  return <Text {...props}>{item}</Text>;
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

function RadiansRefDisplay({
  item,
  ...props
}: ItemWithStyle<RadiansRef>): ReactElement {
  return <MathToRadianDisplay item={item.radians} {...props} />;
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
  return <>&nbsp;</>;
}

function InlinePoseRefDisplay({ pose }: { pose: PoseRef }): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  /*const ap = isRef(pose) ? getPose(pose) : pose;
  const color = getColorFor(ap);*/
  // const style = { color: colors[color % colors.length] };
  return isRef(pose) ? (
    <Text
      style={
        {
          /*style*/
        }
      }
    >
      {pose}
    </Text>
  ) : (
    <Text
      style={
        {
          /*style*/
        }
      }
    >
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
  const beziers = useAtomValue(MappedBeziersAtom);
  const colors = useAtomValue(ColorsAtom);
  const rowData: RowData[] = [];
  let count = 1;
  for (const [name, b] of beziers.entries()) {
    if (!isRef(b)) {
      rowData.push({ offset: count, size: b.points.length });
      count += b.points.length;
    }
  }
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto',
    justifyItems: 'end',
    justifySelf: 'start',
  };
  return (
    <div style={gridStyle}>
      <Text size={400}>Name {beziers.size}</Text>
      <Text size={400}>Poses</Text>
      {[
        ...beziers
          .entries()
          .filter(([, br]) => !isRef(br))
          .map(([name, br], index) => {
            if (!isRef(br)) {
              const color = getColorFor(br);
              const style = {
                color: colors[color % colors.length],
                ...rowSpan(1, rowData[index]),
              };
              return (
                <Fragment key={`br-${name}`}>
                  <Text style={style}>{name}</Text>
                  {br.points.map((pr, index) => (
                    <InlinePoseRefDisplay
                      key={`br-${name}-${index}-2`}
                      pose={pr}
                    />
                  ))}
                </Fragment>
              );
            }
          }),
      ]}
    </div>
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
  chain: [string, AnonymousPathChain];
  rowdata: NestedRowData;
}): ReactElement {
  const colors = useAtomValue(ColorsAtom);
  // This renders into a container grid that's 3 columns wide
  return (
    <>
      <Text style={rowSpan(1, rowdata)}>{chain[0]}</Text>
      {chain[1].paths.map((br, index) => {
        /*const anonBez = getBezier(br);
        const color = getColorFor(anonBez);*/
        if (isRef(br)) {
          // Span both columns for a named curve
          return (
            <Text
              key={`npc-${br}-${index}`}
              style={{
                gridColumnStart: 2,
                gridColumnEnd: 4,
                justifySelf: 'center',
                /*color: colors[color % colors.length],*/
              }}
            >
              {br}
            </Text>
          );
        } else {
          const style = {
            // color: colors[color % colors.length],
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
      <HeadingTypeDisplay heading={chain[1].heading} />
    </>
  );
}

export function PathChainList(): ReactElement {
  const items = useAtomValue(MappedPathChainsAtom);
  // I need to collect row spans for:
  // 1- The name, a running total of all prior path chains, plus a total count
  //    of this path's chains.
  // 2- The type/name of each bezier of the chain, which is a running count of
  //    the prior rows, plus the count of the current curve's control points
  let count = 1;
  let nestedRowData: NestedRowData[] = [];
  for (const [_, pc] of items) {
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
    <div style={gridStyle}>
      <Text size={400}>Name {items.size}</Text>
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
      {[
        ...items
          .entries()
          .map((pc, index) => (
            <NamedPathChainDisplay
              key={pc[0]}
              chain={pc}
              rowdata={nestedRowData[index]}
            />
          )),
      ]}
    </div>
  );
}

export function PathsDataDisplay({
  expand,
}: {
  expand?: boolean;
}): ReactElement {
  const selFile = useAtomValue(SelectedFileAtom);
  if (selFile.length === 0) {
    return <div>Please select a file to view.</div>;
  }
  return (
    <>
      <Expando label="Values" indent={20} size={500}>
        <NamedValueList />
        <NewValue />
      </Expando>
      <Expando label="Poses" indent={20} size={500}>
        <NamedPoseList />
        <NewPose />
      </Expando>
      <Expando label="Bezier Lines/Curves" indent={20} size={500}>
        <NamedBezierList />
        <Button style={{ margin: 10 }}>New Bezier</Button>
      </Expando>
      <Expando label="PathChains" indent={20} size={500}>
        <PathChainList />
        <Button style={{ margin: 10 }}>New PathChain</Button>
      </Expando>
    </>
  );
}
