import { isNumber, isString } from '@freik/typechk';
import { describe, expect, test } from 'bun:test';
import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierName,
  BezierRef,
  EmptyPathChainFile,
  HeadingRef,
  isError,
  NamedBezier,
  NamedPose,
  NamedValue,
  Path,
  PathChainFile,
  PathChainName,
  PoseName,
  PoseRef,
  Team,
  TeamPaths,
  ValueName,
  ValueRef,
} from '../../server/types';
import { GetPaths, LoadAndIndexFile, SavePath } from '../state/API';
import {
  calcBezierRef,
  calcHeadingRef,
  calcPoseRef,
  calcPoseRefHeading,
  calcValue,
  calcValueRef,
} from '../state/IndexedFile';

function mkValNm(name: string): ValueName {
  return name as ValueName;
}
function mkVal(type: 'int' | 'double', value: number): AnonymousValue;
function mkVal(type: 'radians', value: number | string): HeadingRef;
function mkVal(
  type: 'radians' | 'int' | 'double',
  value: number | string,
): AnonymousValue | HeadingRef {
  if (type === 'radians') {
    if (isNumber(value)) {
      return {
        radians: Number.isInteger(value) ? { int: value } : { double: value },
      };
    } else {
      return { radians: value as ValueName };
    }
  } else if (isNumber(value)) {
    return Number.isInteger(value) ? { int: value } : { double: value };
  }
}
function mkNmVal(
  name: string,
  value: AnonymousValue | string | HeadingRef,
): NamedValue {
  return {
    name: mkValNm(name),
    value: isString(value) ? (value as ValueRef) : value,
  };
}
function mkPoseNm(name: string): PoseName {
  return name as PoseName;
}
function mkPose(
  x: AnonymousValue | string,
  y: AnonymousValue | string,
  heading?: HeadingRef,
): AnonymousPose {
  return { x: x as ValueRef, y: y as ValueRef, heading: heading as HeadingRef };
}
function mkNmPose(name: string, pose: AnonymousPose | string): NamedPose {
  return { name: name as PoseName, pose: pose as PoseRef };
}
function mkBezNm(name: string): BezierName {
  return name as BezierName;
}
function mkBez(type: 'line' | 'curve', ...points: PoseRef[]): AnonymousBezier {
  return { type, points };
}
function mkNmBez(name: string, bez: AnonymousBezier | string): NamedBezier {
  return { name: mkBezNm(name), points: bez as BezierRef };
}
function mkPCNm(name: string): PathChainName {
  return name as PathChainName;
}

// Mocks & phony data for my tests:
const teamPaths: TeamPaths = {
  ['team1' as Team]: ['path1.java' as Path, 'path2.java' as Path],
  ['team2' as Team]: ['path3.java' as Path, 'path4.java' as Path],
};

const badTeamPaths: unknown = {
  team1: ['path1.java', 'path2.java'],
  team2: { path3: 'path3.java' },
};

const testPathChainFile: PathChainFile = {
  ...EmptyPathChainFile,
};
testPathChainFile.values.push({
  name: 'item1' as ValueName,
  value: { int: 1 },
});
testPathChainFile.poses.push({
  name: 'item1' as PoseName,
  pose: { x: { int: 1 }, y: { int: 1 } },
});

const simpleBez: AnonymousBezier = {
  type: 'curve',
  points: [
    { x: 'val1' as ValueName, y: 'val1' as ValueName },
    'pose1' as PoseName,
    'pose2' as PoseName,
  ],
};

const fullPathChainFile: PathChainFile = {
  name: 'path3.java',
  values: [
    mkNmVal('val1', mkVal('int', 1)),
    mkNmVal('val2', mkVal('double', 2.5)),
    mkNmVal('val3', mkVal('radians', 90)),
    mkNmVal('valCirc', mkVal('radians', 'valCirc2')),
    mkNmVal('valCirc2', mkVal('radians', 'valCirc')),
    mkNmVal('refVal', mkValNm('val1'))
  ],
  poses: [
    mkNmPose('pose1', mkPose(mkVal('double', 2.5), mkValNm('val1'))),
    mkNmPose(
      'pose2',
      mkPose(mkValNm('val2'), mkValNm('val1'), mkVal('radians', 60)),
    ),
    mkNmPose(
      'pose3',
      mkPose(mkValNm('val1'), mkValNm('val2'), mkValNm('val3')),
    ),
  ],
  beziers: [
    mkNmBez('bez1', mkBez('line', mkPoseNm('pose1'), mkPoseNm('pose2'))),
    mkNmBez('bez2', simpleBez),
  ],
  pathChains: [
    {
      name: 'pc1' as PathChainName,
      paths: ['bez1' as BezierName, 'bez2' as BezierName],
      heading: { type: 'tangent' },
    },
    {
      name: 'pc2' as PathChainName,
      paths: [
        'bez2' as BezierName,
        { type: 'line', points: ['pose1' as PoseName, 'pose3' as PoseName] },
      ],
      heading: { type: 'constant', heading: 'pose3' as PoseName },
    },
    {
      name: 'pc3' as PathChainName,
      paths: [
        'bez1' as BezierName,
        {
          type: 'curve',
          points: [
            'pose1' as PoseName,
            'pose3' as PoseName,
            'pose2' as PoseName,
          ],
        },
      ],
      heading: {
        type: 'interpolated',
        headings: ['pose2' as PoseName, { radians: { int: 135 } }],
      },
    },
  ],
};

const danglingPCF: PathChainFile = {
  name: 'dangling.java',
  values: [...fullPathChainFile.values],
  poses: [
    ...fullPathChainFile.poses,
    mkNmPose('danglingHeader', mkPose('nope', 'val1')),
  ],
  beziers: [
    ...fullPathChainFile.beziers,
    mkNmBez(
      'danglingPoseRef',
      mkBez(
        'line',
        mkPoseNm('noPose'),
        mkPose('val1', 'not_here', mkVal('radians', 'nuthing')),
      ),
    ),
    mkNmBez(
      'danglingPoseRef2',
      mkBez('curve', mkPose('val1', 'val2', mkValNm('zip'))),
    ),
    mkNmBez(
      'danglingPoseRef3',
      mkBez('line', mkPose('val1', 'val2', mkValNm('zip'))),
    ),
  ],
  pathChains: [
    ...fullPathChainFile.pathChains,
    {
      name: 'danglingBezRef' as PathChainName,
      paths: ['noBez' as BezierName],
      heading: { type: 'constant', heading: 'noHeading' as ValueName },
    },
    {
      name: 'danglingBezRef2' as PathChainName,
      paths: ['bez1' as BezierName, 'bez2' as BezierName],
      heading: {
        type: 'constant',
        heading: { radians: 'nospot' as ValueName },
      },
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
    const res2 = await LoadAndIndexFile('team1', 'path1.java');
    expect(isError(res2)).toBeTrue();
    if (isError(res2)) {
      expect(res2.errors()).toEqual([
        'Invalid PathChainFile loaded from server',
      ]);
    }
    const res = await LoadAndIndexFile('team1', 'path2.java');
    expect(isError(res)).toBeTrue();
    if (isError(res)) {
      expect(res.errors()).toEqual([
        'Duplicate names found between values, points, beziers, and path chains.',
        'Loaded file team1/path2.java has dangling references.',
      ]);
    }
  });
  test('Full PathChainFile validation, color hashing, and evaluation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadAndIndexFile('team2', 'path3.java');
    if (isError(res)) {
      console.log('Errors:', res.errors());
      expect(isError(res)).toBeFalse();
      return;
    }
    expect(res.namedValues.size).toEqual(6);
    expect(res.namedPoses.size).toEqual(3);
    expect(res.namedBeziers.size).toEqual(2);
    expect(res.namedPathChains.size).toEqual(3);

    expect(calcValue(res, { int: 1 })).toEqual(1);
    expect(() => calcValueRef(res, 'valCirc' as ValueName)).toThrowError(
      'Circular reference for valCirc (valCirc, valCirc2 cause the cycle)',
    );
    expect(calcValueRef(res, { double: 2.5 })).toEqual(2.5);
    expect(calcValueRef(res, { radians: { int: 180 } })).toEqual(Math.PI);
    expect(calcValueRef(res, 'val2' as ValueName)).toEqual(2.5);
    expect(
      calcPoseRef(res, { x: 'val1' as ValueName, y: 'val2' as ValueName }),
    ).toEqual({ x: 1, y: 2.5 });
    const pose3 = calcPoseRef(res, 'pose3' as PoseName);
    expect(pose3).toEqual({ x: 1, y: 2.5 });
    expect(() => calcPoseRef(res, 'noPose' as PoseName)).toThrow();
    expect(calcBezierRef(res, 'bez2' as BezierName)).toEqual([
      { x: 1, y: 1 },
      { x: 2.5, y: 1 },
      { x: 2.5, y: 1 },
    ]);
    expect(calcValueRef(res, 'val1' as ValueName)).toEqual(1);
    expect(calcHeadingRef(res, { radians: 'val2' as ValueName })).toEqual(
      (2.5 * Math.PI) / 180,
    );
    expect(calcValueRef(res, { int: 15 })).toEqual(15);
    expect(calcValueRef(res, mkValNm('refVal'))).toEqual(1);
    expect(calcPoseRefHeading(res, mkPoseNm('pose3'))).toEqual(Math.PI / 2);
    const res2 = await LoadAndIndexFile('team2', 'path3.java');
    expect(!isError(res2)).toBeTrue();
  });
  test('Undefined references in PathChainFile validation', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await LoadAndIndexFile('team2', 'path4.java');
    expect(isError(res)).toBeTrue();
    if (isError(res)) {
      const errs = res.errors();
      expect(errs.length).toEqual(12);
      const errTxt = String(res);
      expect(errTxt).toEndWith(
        'Loaded file team2/path4.java has dangling references.',
      );
    }
  });
  test('Need to implement a real "save" feature', async () => {
    // Probably add a test for this, yeah?
    const res = await SavePath('teamX', 'pathY.java', fullPathChainFile);
    expect(res).toEqual('NYI');
  });
});
