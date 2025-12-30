import { describe, expect, test } from 'bun:test';
import { AnonymousBezier, isError, PathChainFile, TeamPaths } from '../../server/types';
import {
  EmptyPathChainFile,
  GetPaths,
  LoadFile,
  SavePath,
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
MyFetchFunc.preconnect = () => { };

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
    expect(isError(res2)).toBeTrue();
    if (isError(res2)) {
      expect(res2.errors()).toEqual(
        ['Invalid PathChainFile loaded from server'],
      );
    }
    const res = await LoadFile('team1', 'path2.java');
    expect(isError(res)).toBeTrue();
    if (isError(res)) {
      expect(res.errors()).toEqual(
        ['Invalid PathChainFile loaded from server'],
      );
    }
  });
  test('Undefined references in PathChainFile validation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadFile('team2', 'path4.java');
    expect(isError(res)).toBeTrue();
    if (isError(res)) {
      expect(res.errors()).toEqual(['Loaded file team2/path4.java has dangling references.']);
    }
  });
  test('Full PathChainFile validation, color hashing, and evaluation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadFile('team2', 'path3.java');
    if (isError(res)) {
      console.log('Errors:', res.errors());
      expect(isError(res)).toBeFalse();
      return;
    }
    expect(res.dump()).toEqual('3 values, 3 poses, 2 beziers, 3 pathChains.');
    expect(res.getValueRefValue({ type: 'int', value: 1 })).toEqual(1);
    expect(res.getValueRefValue({ type: 'double', value: 2.5 })).toEqual(2.5);
    expect(res.getValueRefValue({ type: 'radians', value: 180 })).toEqual(Math.PI);
    expect(res.getValueRefValue('val2')).toEqual(2.5);
    expect(res.getPoseRefPoint({ x: 'val1', y: 'val2' })).toEqual({ x: 1, y: 2.5 });
    const pose3 = res.getPoseRefPoint('pose3');
    expect(pose3).toEqual({ x: 1, y: 2.5 });
    expect(() => res.getPoseRefPoint('noPose')).toThrow();
    expect(res.getBezierRefPoints('bez2')).toEqual([
      { x: 1, y: 1 },
      { x: 2.5, y: 1 },
      { x: 2.5, y: 1 },
    ]);
    expect(res.getValueRefValue('val1')).toEqual(1);
    expect(res.getHeadingRefValue({ radians: 'val2' })).toEqual(
      (2.5 * Math.PI) / 180,
    );
    expect(res.getValueRefValue({ type: 'int', value: 15 })).toEqual(15);
    const res2 = await LoadFile('team2', 'path3.java');
    expect(!isError(res2)).toBeTrue();
  });
  test('Need to implement a real "save" feature', async () => {
    // Probably add a test for this, yeah?
    const res = await SavePath('teamX', 'pathY.java', fullPathChainFile);
    expect(res).toEqual('NYI');
  });
});
