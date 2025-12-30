import { isString } from '@freik/typechk';
import { expect, test } from 'bun:test';
import path from 'path';
import { LoadPath, loadPathChainsFromFile } from '../loadpath';
import { firstFtcSrc, getProjectFilePath } from '../utility';

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
  const paths = await loadPathChainsFromFile(repoPathToFile);
  expect(paths).toBeDefined();
  if (isString(paths)) {
    console.error(paths);
  }
  expect(isString(paths)).toBeFalse();
  if (isString(paths)) {
    return;
  }
  // This currently failing, as I haven't implemented the parsing yet.
  expect(paths.values.length).toBe(4);
  expect(paths.values[0]).toEqual({
    name: 'org',
    value: { type: 'double', value: 72.0 },
  });
  expect(paths.values[1]).toEqual({
    name: 'step',
    value: { type: 'int', value: 80 },
  });
  expect(paths.values[2]).toEqual({
    name: 'one80',
    value: { type: 'radians', value: 180 },
  });
  expect(paths.values[3]).toEqual({
    name: 'step_mid',
    value: { type: 'double', value: 74.0 },
  });

  expect(paths.poses.length).toBe(6);
  expect(paths.poses[0]).toEqual({
    name: 'start',
    pose: { x: 'org', y: 'org', heading: { type: 'int', value: 0 } },
  });
  expect(paths.poses[1]).toEqual({
    name: 'step1',
    pose: { x: 'step', y: 'org', heading: { type: 'radians', value: 90 } },
  });
  expect(paths.poses[2]).toEqual({
    name: 'step2',
    pose: { x: 'step', y: 'step', heading: 'one80' },
  });
  expect(paths.poses[3]).toEqual({
    name: 'step23_mid',
    pose: { x: 'step_mid', y: 'step_mid' },
  });
  expect(paths.poses[4]).toEqual({
    name: 'step3',
    pose: { x: 'org', y: 'step', heading: { type: 'double', value: -0.7854 } },
  });
  expect(paths.poses[5]).toEqual({
    name: 'step4',
    pose: {
      x: { type: 'double', value: 72.0 },
      y: { type: 'int', value: 72 },
      heading: { type: 'radians', value: -30 },
    },
  });

  expect(paths.beziers.length).toBe(4);
  expect(paths.beziers[0]).toEqual({
    name: 'start_to_step1',
    points: {
      points: ['start', 'step1'],
      type: 'line',
    },
  });
  expect(paths.beziers[1]).toEqual({
    name: 'step2_to_step3',
    points: {
      points: ['step2', 'step23_mid', 'step3'],
      type: 'curve',
    },
  });
  expect(paths.beziers[2]).toEqual({
    name: 'step4_to_start',
    points: {
      points: ['step4', { x: 'org', y: { type: 'int', value: 15 } }, 'start'],
      type: 'curve',
    },
  });
  expect(paths.beziers[3]).toEqual({
    name: 'another_line',
    points: {
      points: [
        {
          x: { type: 'double', value: 1.2 },
          y: 'step_mid',
          heading: { type: 'double', value: 0.0 },
        },
        {
          x: { type: 'int', value: 1 },
          y: { type: 'double', value: 3.4 },
          heading: { type: 'radians', value: 60 },
        },
      ],
      type: 'line',
    },
  });

  expect(paths.pathChains.length).toBe(5);
  expect(paths.pathChains[0]).toEqual({
    name: 'Path1',
    paths: ['start_to_step1'],
    heading: { type: 'interpolated', headings: ['start', 'step1'] },
  });
  expect(paths.pathChains[1]).toEqual({
    name: 'Path2',
    paths: [{ type: 'curve', points: ['step1', 'step2'] }],
    heading: {
      type: 'interpolated',
      headings: [
        {
          type: 'radians',
          value: 90,
        },
        { radians: 'step_mid' },
      ],
    },
  });
  expect(paths.pathChains[2]).toEqual({
    name: 'Path3',
    paths: ['step2_to_step3'],
    heading: { type: 'interpolated', headings: ['step_mid', 'step3'] },
  });
  expect(paths.pathChains[3]).toEqual({
    name: 'Path4',
    paths: [{ type: 'line', points: ['step3', 'step4'] }],
    heading: { type: 'constant', heading: 'one80' },
  });
  expect(paths.pathChains[4]).toEqual({
    name: 'AnotherPath',
    paths: [
      {
        type: 'line',
        points: [
          { x: { type: 'int', value: 0 }, y: { type: 'int', value: 0 } },
          { x: { type: 'int', value: 20 }, y: { type: 'int', value: 20 } },
        ],
      },
      { type: 'curve', points: ['step1', 'step2', 'step3', 'step4'] },
      'step4_to_start',
    ],
    heading: {
      type: 'interpolated',
      headings: [{ radians: 'step' }, 'step4'],
    },
  });
});
