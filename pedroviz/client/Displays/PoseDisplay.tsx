import { CSSProperties, ReactElement } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import { Text } from '@fluentui/react-components';
import { isDefined } from '@freik/typechk';

import { isPoseName, isRef } from '../../CodeTypeCheck';
import { AnonymousPose, PoseName } from '../../CodeTypes';
import { HeadingRefDisplay } from '../PathsDataDisplay';
import { getColorFor } from '../state/API';
import {
  ColorsAtom,
  MappedPosesAtom,
  MappedValuesAtom,
  PoseAtomFamily,
} from '../state/Atoms';
import { HasKeys } from '../types';
import { ItemWithStyle } from '../ui-tools/types';
import { NumberOrNamedValue } from './NumberOrNamedValueEditor';
import { ValRefFromString } from './Validation';

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
  const names = useAtomValue(MappedValuesAtom);

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
      {!noHeading && <Text size={400}>Heading</Text>}
    </>
  );
}

export function NamedPoseItem({
  item,
  style,
}: ItemWithStyle<PoseName>): ReactElement {
  const [pose, setPose] = useAtom(PoseAtomFamily(item));
  const names = useAtomValue(MappedValuesAtom);
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

export function NamedPoseList(): ReactElement {
  const items = useAtomValue(MappedPosesAtom);
  const colors = useAtomValue(ColorsAtom);
  const gridStyle: CSSProperties = {
    display: 'grid',
    columnGap: '10pt',
    gridTemplateColumns: '1fr auto auto auto',
    justifyItems: 'end',
    justifySelf: 'start',
  };
  return (
    <div style={gridStyle}>
      <Text size={400}>Name</Text>
      <AnonymousPoseHeader />
      {[
        ...items.entries().map(([name, pose]) => {
          if (!isRef(pose)) {
            const color = getColorFor(pose);
            const style = { color: colors[color % colors.length] };
            return <NamedPoseItem style={style} key={name} item={name} />;
          }
        }),
      ]}
    </div>
  );
}
