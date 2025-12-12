import { describe, expect, test } from 'bun:test';
import { AnonymousBezier, PathChainFile, TeamPaths } from '../../server/types';
import {
  EmptyPathChainFile,
  getBezierPoints,
  GetPaths,
  getValue,
  getValueFromHeaderRef,
  LoadFile,
  numFromVal,
  pointFromPose,
  pointFromPoseRef,
} from '../state/API';
import { IndexedPCF } from '../state/types';

// Mocks & phony data for my tests:
const teamPaths: TeamPaths = {
  team1: ['path1.java', 'path2.java'],
  team2: ['path3.java', 'path4.java'],
};

const badTeamPaths: unknown = {
  team1: ['path1.java', 'path2.java'],
  team2: { path3: 'path3.java' },
};

const testPathChainFile: IndexedPCF = {
  ...EmptyPathChainFile,
};

const simpleBez: AnonymousBezier = {
  type: 'curve',
  points: [{ x: 'val1', y: 'val1' }, 'pose1', 'pose2'],
};

const fullPathChainFile: PathChainFile = {
  name: 'path3.java',
  values: [
    { name: 'val1', value: { type: 'int', value: 1 } },
    { name: 'val2', value: { type: 'double', value: 2.5 } },
    { name: 'val3', value: { type: 'radians', value: 90 } },
  ],
  poses: [
    { name: 'pose1', pose: { x: { type: 'double', value: 2.5 }, y: 'val1' } },
    {
      name: 'pose2',
      pose: { x: 'val2', y: 'val1', heading: { type: 'radians', value: 60 } },
    },
    {
      name: 'pose3',
      pose: { x: 'val1', y: 'val2', heading: 'val3' },
    },
  ],
  beziers: [
    { name: 'bez1', points: { type: 'line', points: ['pose1', 'pose2'] } },
    {
      name: 'bez2',
      points: simpleBez,
    },
  ],
  pathChains: [
    {
      name: 'pc1',
      paths: ['bez1', 'bez2'],
      heading: { type: 'tangent' },
    },
    {
      name: 'pc2',
      paths: ['bez2', { type: 'line', points: ['pose1', 'pose3'] }],
      heading: { type: 'constant', heading: 'pose3' },
    },
    {
      name: 'pc3',
      paths: ['bez1', { type: 'curve', points: ['pose1', 'pose3', 'pose2'] }],
      heading: {
        type: 'interpolated',
        headings: ['pose2', { radians: { type: 'int', value: 135 } }],
      },
    },
  ],
};

const fullIndexedPCF: IndexedPCF = {
  ...fullPathChainFile,
  namedValues: new Map([
    ['val1', 0],
    ['val2', 1],
    ['val3', 2],
  ]),
  namedPoses: new Map([
    ['pose1', 0],
    ['pose2', 1],
    ['pose3', 2],
  ]),
  namedBeziers: new Map([
    ['bez1', 0],
    ['bez2', 1],
  ]),
  namedPathChains: new Map([
    ['pc1', 0],
    ['pc2', 1],
    ['pc3', 2],
  ]),
};

const danglingPCF: PathChainFile = {
  name: 'dangling.java',
  values: [...fullPathChainFile.values],
  poses: [
    ...fullPathChainFile.poses,
    {
      name: 'danglingHeader',
      pose: { x: 'nope', y: 'val1' },
    },
  ],
  beziers: [
    ...fullPathChainFile.beziers,
    {
      name: 'danglingPoseRef',
      points: {
        type: 'line',
        points: [
          'noPose',
          { x: 'val1', y: 'not_here', heading: { radians: 'nuthing' } },
        ],
      },
    },
    {
      name: 'danglingPoseRef2',
      points: {
        type: 'curve',
        points: [{ x: 'val1', y: 'val2', heading: 'zip' }],
      },
    },
    {
      name: 'danglingPoseRef3',
      points: {
        type: 'line',
        points: [{ x: 'val1', y: 'val2', heading: 'zip' }],
      },
    },
  ],
  pathChains: [
    ...fullPathChainFile.pathChains,
    {
      name: 'danglingBezRef',
      paths: ['noBez'],
      heading: { type: 'constant', heading: 'noHeading' },
    },
    {
      name: 'danglingBezRef2',
      paths: ['bez1', 'bez2'],
      heading: { type: 'constant', heading: { radians: 'nospot' } },
    },
  ],
};

let bad = false;

const status = {
  status: 200,
  headers: { 'Content-Type': 'application/json' },
};
async function MyFetchFunc(
  key: string | URL | Request,
  init?: RequestInit,
): Promise<Response> {
  switch (key) {
    case '/api/getpaths': {
      const body = JSON.stringify(bad ? badTeamPaths : teamPaths);
      return new Response(body, status);
    }
    case '/api/loadpath/team1/path1.java': {
      const body = JSON.stringify({ a: 'b' });
      return new Response(body, status);
    }
    case '/api/loadpath/team1/path2.java': {
      const body = JSON.stringify(testPathChainFile);
      return new Response(body, status);
    }
    case '/api/loadpath/team2/path3.java': {
      const body = JSON.stringify(fullPathChainFile);
      return new Response(body, status);
    }
    case '/api/loadpath/team2/path4.java': {
      const body = JSON.stringify(danglingPCF);
      return new Response(body, status);
    }
  }
  return new Response('ERROR', { status: 404 });
}
MyFetchFunc.preconnect = () => {};

describe('API validation', () => {
  test('GetPaths', async () => {
    globalThis.fetch = MyFetchFunc;
    bad = true;
    const res2 = await GetPaths();
    expect(res2).toEqual({});
    bad = false;
    const res = await GetPaths();
    expect(res).toEqual(teamPaths);
  });
  test('LoadPaths', async () => {
    globalThis.fetch = MyFetchFunc;
    const res2 = await LoadFile('team1', 'path1.java');
    expect(res2).toEqual(EmptyPathChainFile);
    const res = await LoadFile('team1', 'path2.java');
    expect(res).toEqual(testPathChainFile);
  });
  test('Undefined references in PathChainFile validation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadFile('team2', 'path4.java');
    expect(res).toEqual(EmptyPathChainFile);
  });
  test('Full PathChainFile validation, color hashing, and evaluation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadFile('team2', 'path3.java');
    expect(res).toEqual(fullIndexedPCF);
    expect(numFromVal({ type: 'int', value: 1 })).toEqual(1);
    expect(numFromVal({ type: 'double', value: 2.5 })).toEqual(2.5);
    expect(numFromVal({ type: 'radians', value: 180 })).toEqual(Math.PI);
    expect(getValue('val2')).toEqual(2.5);
    expect(pointFromPose({ x: 'val1', y: 'val2' })).toEqual({ x: 1, y: 2.5 });
    const pose3 = pointFromPoseRef('pose3');
    expect(pose3.length).toEqual(2);
    expect(pose3[1]).toEqual({ x: 1, y: 2.5 });
    expect(() => pointFromPoseRef('noPose')).toThrow();
    expect(getBezierPoints('bez2')[1].map(([n, p]) => p)).toEqual([
      { x: 1, y: 1 },
      { x: 2.5, y: 1 },
      { x: 2.5, y: 1 },
    ]);
    expect(getValueFromHeaderRef('val1')).toEqual(1);
    expect(getValueFromHeaderRef({ radians: 'val2' })).toEqual(
      (2.5 * Math.PI) / 180,
    );
    expect(getValueFromHeaderRef({ type: 'int', value: 15 })).toEqual(15);
  });
  test.todo("Need to implement a 'save' feature", () => {
    // Probably add a test for this, yeah?
    expect(true).toBeTrue();
  });
});
