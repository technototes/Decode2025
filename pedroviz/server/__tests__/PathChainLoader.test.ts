import path from 'node:path';
import { expect, test } from 'bun:test';

import { isError } from '@freik/typechk';

import {
  BezierName,
  BezierType,
  FacingType,
  PathChainName,
  PoseName,
  ValueName,
} from '../../CodeTypes';
import { MakeParsedClass } from '../PathChainLoader';
import { firstFtcSrc, getProjectFilePath } from '../utility';
import { LoadPath } from '../web-interface';

function getTestRepoPath(): string {
  return path.resolve(__dirname, 'test-repo-root');
}

test('raw endpoint invocation', async () => {
  const res: Response = await LoadPath('TeamCode', 'TeamTestPaths.java');
  expect(res).toHaveProperty('ok', true);
  expect(res).toHaveProperty('status', 200);
  expect(res).toHaveProperty('headers');
});

test('loadPathChainsFromFile loads paths correctly', async () => {
  const testPath = getProjectFilePath('TeamA', 'TeamTestPaths.java');
  expect(testPath).toBe(
    path.join('..', 'TeamA', firstFtcSrc, 'teama', 'TeamTestPaths.java'),
  );
  // Need to hack aroun the actual location
  const repoPathToFile = path.join(
    getTestRepoPath(),
    'FtcRobotController',
    testPath,
  );
  const paths = await MakeParsedClass(repoPathToFile);
  expect(paths).toBeDefined();
  if (isError(paths)) {
    console.error(paths.errors());
  }
  expect(isError(paths)).toBeFalse();
  if (isError(paths)) {
    return;
  }
  // This currently failing, as I haven't implemented the parsing yet.
  expect(paths.values.length).toBe(7);
  expect(paths.values[0]).toEqual({
    name: 'org' as ValueName,
    value: { double: 72.0 },
  });
  expect(paths.values[1]).toEqual({
    name: 'step' as ValueName,
    value: { int: 80 },
  });
  expect(paths.values[2]).toEqual({
    name: 'ninety' as ValueName,
    value: { int: 90 },
  });
  expect(paths.values[3]).toEqual({
    name: 'one80' as ValueName,
    value: { radians: { int: 180 } },
  });
  expect(paths.values[4]).toEqual({
    name: 'step_mid' as ValueName,
    value: { double: 74.0 },
  });
  expect(paths.values[4]).toEqual({
    name: 'step_mid' as ValueName,
    value: { double: 74.0 },
  });
  expect(paths.values[5]).toEqual({
    name: 'radRef' as ValueName,
    value: { radians: 'ninety' as ValueName },
  });
  expect(paths.values[6]).toEqual({
    name: 'valRef' as ValueName,
    value: 'org' as ValueName,
  });

  expect(paths.poses.length).toBe(6);
  expect(paths.poses[0]).toEqual({
    name: 'start' as PoseName,
    pose: { x: 'org' as ValueName, y: 'org' as ValueName, heading: { int: 0 } },
  });
  expect(paths.poses[1]).toEqual({
    name: 'step1' as PoseName,
    pose: {
      x: 'step' as ValueName,
      y: 'org' as ValueName,
      heading: { radians: { int: 90 } },
    },
  });
  expect(paths.poses[2]).toEqual({
    name: 'step2' as PoseName,
    pose: {
      x: 'step' as ValueName,
      y: 'step' as ValueName,
      heading: 'one80' as ValueName,
    },
  });
  expect(paths.poses[3]).toEqual({
    name: 'step23_mid' as PoseName,
    pose: { x: 'step_mid' as ValueName, y: 'step_mid' as ValueName },
  });
  expect(paths.poses[4]).toEqual({
    name: 'step3' as PoseName,
    pose: {
      x: 'org' as ValueName,
      y: 'step' as ValueName,
      heading: { double: -0.7854 },
    },
  });
  expect(paths.poses[5]).toEqual({
    name: 'step4' as PoseName,
    pose: {
      x: { double: 72.0 },
      y: { int: 72 },
      heading: { radians: { int: -30 } },
    },
  });

  expect(paths.beziers.length).toBe(4);
  expect(paths.beziers[0]).toEqual({
    name: 'start_to_step1' as BezierName,
    points: {
      points: ['start' as PoseName, 'step1' as PoseName],
      type: BezierType.Line,
    },
  });
  expect(paths.beziers[1]).toEqual({
    name: 'step2_to_step3' as BezierName,
    points: {
      points: [
        'step2' as PoseName,
        'step23_mid' as PoseName,
        'step3' as PoseName,
      ],
      type: BezierType.Curve,
    },
  });
  expect(paths.beziers[2]).toEqual({
    name: 'step4_to_start' as BezierName,
    points: {
      points: [
        'step4' as PoseName,
        { x: 'org' as ValueName, y: { int: 15 } },
        'start' as PoseName,
      ],
      type: BezierType.Curve,
    },
  });
  expect(paths.beziers[3]).toEqual({
    name: 'another_line' as BezierName,
    points: {
      points: [
        {
          x: { double: 1.2 },
          y: 'step_mid' as ValueName,
          heading: { double: 0.0 },
        },
        {
          x: { int: 1 },
          y: { double: 3.4 },
          heading: { radians: { int: 60 } },
        },
      ],
      type: BezierType.Line,
    },
  });

  expect(paths.pathChains.length).toBe(6);
  expect(paths.pathChains[0]).toEqual({
    name: 'Path1' as PathChainName,
    paths: ['start_to_step1' as BezierName],
    heading: {
      type: FacingType.Linear,
      start: 'start' as PoseName,
      end: 'step1' as PoseName,
    },
  });
  expect(paths.pathChains[1]).toEqual({
    name: 'Path2' as PathChainName,
    paths: [
      {
        type: BezierType.Curve,
        points: ['step1' as PoseName, 'step2' as PoseName],
      },
    ],
    heading: {
      type: FacingType.Linear,
      start: {
        radians: { int: 90 },
      },
      end: { radians: 'step_mid' as ValueName },
    },
  });
  expect(paths.pathChains[2]).toEqual({
    name: 'Path3' as PathChainName,
    paths: ['step2_to_step3' as BezierName],
    heading: {
      type: FacingType.Linear,
      start: 'step_mid' as PoseName,
      end: 'step3' as PoseName,
    },
  });
  expect(paths.pathChains[3]).toEqual({
    name: 'Path4' as PathChainName,
    paths: [
      {
        type: BezierType.Line,
        points: ['step3' as PoseName, 'step4' as PoseName],
      },
    ],
    heading: { type: FacingType.Constant, heading: 'one80' as ValueName },
  });
  expect(paths.pathChains[4]).toEqual({
    name: 'AnotherPath' as PathChainName,
    paths: [
      {
        type: BezierType.Line,
        points: [
          { x: { int: 0 }, y: { int: 0 } },
          { x: { int: 20 }, y: { int: 20 } },
        ],
      },
      {
        type: BezierType.Curve,
        points: [
          'step1' as PoseName,
          'step2' as PoseName,
          'step3' as PoseName,
          'step4' as PoseName,
        ],
      },
      'step4_to_start' as BezierName,
    ],
    heading: {
      type: FacingType.Linear,
      start: { radians: 'step' as ValueName },
      end: 'radRef' as PoseName,
    },
  });
});
