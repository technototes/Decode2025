import { CSSProperties, Fragment, ReactElement } from 'react';
import { useAtomValue } from 'jotai';

import { Text } from '@fluentui/react-components';
import { Expandable } from '@freik/fluent9-tools';
import { isDefined, isUndefined } from '@freik/typechk';

import { isRadiansRef, isRef } from '../CodeTypeCheck';
import {
  AnonymousFacing,
  FacingType,
  HeadingRef,
  NamedPathChain,
  PoseRef,
} from '../CodeTypes';
import { NamedPoseList } from './Displays/PoseDisplay';
import {
  AnonymousValueDisplay,
  GeneralRefDisplay,
  NamedValueList,
  RadiansRefDisplay,
  ValueRefDisplay,
} from './Displays/ValueDisplay';
import { getColorFor } from './state/API';
import {
  ColorsAtom,
  MappedBeziersAtom,
  SelectedClassAtom,
  SelectedParsedClassAtom,
  SelectedPathAtom,
} from './state/Atoms';
import { ItemWithStyle } from './ui-tools/types';

export function HeadingRefDisplay({
  item,
  ...props
}: ItemWithStyle<HeadingRef>): ReactElement {
  if (isDefined(item)) {
    if (isRadiansRef(item)) {
      return <RadiansRefDisplay item={item} {...props} />;
    } else if (isRef(item)) {
      return (
        <>
          <GeneralRefDisplay item={item} {...props} />
          <Text>&nbsp;</Text>
        </>
      );
    } else {
      return (
        <>
          <AnonymousValueDisplay item={item} {...props} />
          <Text>&nbsp;</Text>
        </>
      );
    }
  }
  return (
    <>
      <Text>&nbsp;</Text>
      <Text>&nbsp;</Text>
    </>
  );
}

function InlinePoseRefDisplay({ pose }: { pose: PoseRef }): ReactElement {
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
      <Text size={400}>Name</Text>
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
                ...rowSpan(1, rowData[index]!),
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
  heading: AnonymousFacing;
  style?: CSSProperties;
}): ReactElement {
  if (isUndefined(heading)) {
    // TODO: I'm not sure why I'm landin here sometimes.
    return <></>;
  }
  switch (heading.type) {
    case FacingType.Constant:
      return (
        <>
          <Text {...props}>Constant heading</Text>
          <HeadingRefDisplay item={heading.heading} {...props} />
        </>
      );
    case FacingType.Tangent:
      return (
        <>
          <Text {...props}>Tangent heading</Text>
          <span>&nbsp;</span>
        </>
      );
    case FacingType.Linear:
      return (
        <>
          <Text {...props}>Linear heading</Text>
          <span {...props}>
            <HeadingRefDisplay item={heading.start} />
            <Text> to </Text>
            <HeadingRefDisplay item={heading.end} />
          </span>
        </>
      );
    case FacingType.Point:
      return (
        <>
          <Text {...props}>Point heading</Text>
          <span {...props}>
            <InlinePoseRefDisplay pose={heading.point} />
          </span>
        </>
      );
    case FacingType.Reversed:
      return <Text {...props}>Reversed...[TODO!]</Text>;
    case FacingType.Piecewise:
      return <Text {...props}>Piecewise heading...[TODO!]</Text>;
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
              }}>
              {br}
            </Text>
          );
        } else {
          const style = {
            // color: colors[color % colors.length],
            ...rowSpan(1, rowdata.children[index]!),
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
  const items = useAtomValue(SelectedParsedClassAtom);
  // I need to collect row spans for:
  // 1- The name, a running total of all prior path chains, plus a total count
  //    of this path's chains.
  // 2- The type/name of each bezier of the chain, which is a running count of
  //    the prior rows, plus the count of the current curve's control points
  let count = 1;
  let nestedRowData: NestedRowData[] = [];
  for (const pc of items.pathChains) {
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
      <Text size={400}>Name</Text>
      <Text
        size={400}
        style={{
          gridColumnStart: 2,
          gridColumnEnd: 4,
          justifySelf: 'center',
        }}>
        Paths
      </Text>
      {[
        items.pathChains.map((pc, index) => (
          <NamedPathChainDisplay
            key={pc.name}
            chain={pc}
            rowdata={nestedRowData[index]!}
          />
        )),
      ]}
    </div>
  );
}

// function FileInfo() {
//   const pc = useAtomValue(SelectedParsedClassAtom);
//   if (isUndefined(pc)) {
//     return <></>;
//   }
//   return <span>Class:&nbsp;{pc.fullName}</span>;
// }

export function PathsDataDisplay({
  expand,
}: {
  expand?: boolean;
}): ReactElement {
  const selFile = useAtomValue(SelectedPathAtom);
  const selClass = useAtomValue(SelectedClassAtom);
  if (selFile.length === 0 || selClass.length === 0) {
    return <Text size={600}>Please select a file & class to view.</Text>;
  }
  return (
    <div>
      {/* <FileInfo /> */}
      <Expandable label="Values" indent={20}>
        <NamedValueList />
        {/* <NewValue /> */}
      </Expandable>
      <Expandable label="Poses" indent={20}>
        <NamedPoseList />
        {/* <NewPose /> */}
      </Expandable>
      <Expandable label="Curves & Lines" indent={20}>
        <NamedBezierList />
        {/* <Button style={{ margin: 10 }} disabled>
          New Curve
        </Button> */}
      </Expandable>
      <Expandable label="Paths" indent={20}>
        <PathChainList />
        {/* <Button style={{ margin: 10 }} disabled>
          New Path
        </Button> */}
      </Expandable>
    </div>
  );
}
