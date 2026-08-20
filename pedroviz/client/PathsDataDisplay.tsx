import { ReactElement, Suspense } from 'react';
import { useAtomValue, useSetAtom } from 'jotai';

import { Text } from '@fluentui/react-components';
import { Expandable } from '@freik/fluent9-tools';

import { NamedBezierList } from './Displays/CurveDisplay';
import { PathChainList } from './Displays/PathChainDisplay';
import { NamedPoseList } from './Displays/PoseDisplay';
import { NamedValueList } from './Displays/ValueDisplay';
import {
  FocusedCurveAtom,
  FocusedPathAtom,
  FocusedPoseAtom,
  SelectedClassAtom,
  SelectedPathAtom,
} from './state/UserCode';

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
  const setFocusedPose = useSetAtom(FocusedPoseAtom);
  const setFocusedCurve = useSetAtom(FocusedCurveAtom);
  const setFocusedPath = useSetAtom(FocusedPathAtom);
  if (selFile.length === 0 || selClass.length === 0) {
    return <Text size={600}>Please select a file & class to view.</Text>;
  }
  return (
    <div>
      {/* <FileInfo /> */}
      <Expandable label={<Text weight="bold">Values</Text>} indent={20}>
        <Suspense>
          <NamedValueList />
        </Suspense>
        {/* <NewValue /> */}
      </Expandable>
      <Expandable
        label={<Text weight="bold">Poses</Text>}
        indent={20}
        onChanged={(exp: boolean) => exp || setFocusedPose(undefined)}>
        <Suspense>
          <NamedPoseList />
        </Suspense>
        {/* <NewPose /> */}
      </Expandable>
      <Expandable
        label={<Text weight="bold">Curves & Lines</Text>}
        indent={20}
        onChanged={(exp: boolean) => exp || setFocusedCurve(undefined)}>
        <Suspense>
          <NamedBezierList />
        </Suspense>
        {/* <Button style={{ margin: 10 }} disabled>
          New Curve
        </Button> */}
      </Expandable>
      <Expandable
        label={<Text weight="bold">Full Paths</Text>}
        indent={20}
        onChanged={(exp: boolean) => exp || setFocusedPath(undefined)}>
        <Suspense>
          <PathChainList />
        </Suspense>
        {/* <Button style={{ margin: 10 }} disabled>
          New Path
        </Button> */}
      </Expandable>
    </div>
  );
}
