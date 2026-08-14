import { CSSProperties, Fragment, ReactElement } from 'react';
import { useAtomValue } from 'jotai';

import { Text } from '@fluentui/react-components';
import { isUndefined } from '@freik/typechk';

import { isRef } from '../../CodeTypeCheck';
import { AnonymousFacing, FacingType, NamedPathChain } from '../../CodeTypes';
import { rowSpan } from '../helpers';
import { ColorsAtom, SelectedParsedClassAtom } from '../state/Atoms';
import { RowData } from '../types';
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
