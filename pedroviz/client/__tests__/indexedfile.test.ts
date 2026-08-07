import { describe, expect, test } from 'bun:test';

import '@testing-library/jest-dom';

import { MakeMultiMap } from '@freik/containers';

import { EmptyParsedClass } from '../../CodeTypeCheck';
import {
  AnonymousBezier,
  BezierName,
  BezierType,
  FacingType,
  ParsedClass,
  PathChainName,
  PoseName,
  ValueName,
} from '../../CodeTypes';
import { ClassKey, PathDatabase, PathKey, Team } from '../../IpcTypes';
import {
  GetNameLookup,
  MakeFileIndex,
  ValidateIndex,
} from '../state/IndexedFile';
import { TestPathsParsed } from './testpaths.input';

// Mocks & phony data for my tests:
const teams: Team[] = ['team1' as Team, 'team2' as Team];
//   ['team1' as Team]: ['path1.java' as Path, 'path2.java' as Path],
//   ['team2' as Team]: ['path3.java' as Path, 'path4.java' as Path],
// };

const testParsedClass: ParsedClass = {
  values: [],
  poses: [],
  beziers: [],
  pathChainHelpers: [],
  pathChains: [],
  container: { fileName: '' },
  children: {},
  name: 'path1.java',
  fullName: 'test.path1',
  imports: [],
};

const simpleBez: AnonymousBezier = {
  type: BezierType.Curve,
  points: [
    { x: 'val1' as ValueName, y: 'val1' as ValueName },
    'pose1' as PoseName,
    'pose2' as PoseName,
  ],
};

const noParsedClass: ParsedClass = {
  name: 'z',
  fullName: 'test.z',
  imports: [],
  container: { fileName: 'path2.java' },
  children: {},
  values: [],
  poses: [],
  beziers: [],
  pathChains: [],
  pathChainHelpers: [],
};

const fullParsedClass: ParsedClass = {
  name: 'c',
  fullName: 'test.c',
  imports: [],
  values: [
    { name: 'val1' as ValueName, value: { int: 1 } },
    { name: 'val2' as ValueName, value: { double: 2.5 } },
    { name: 'val3' as ValueName, value: { radians: { int: 90 } } },
  ],
  poses: [
    {
      name: 'pose1' as PoseName,
      pose: { x: { double: 2.5 }, y: 'val1' as ValueName },
    },
    {
      name: 'pose2' as PoseName,
      pose: {
        x: 'val2' as ValueName,
        y: 'val1' as ValueName,
        heading: { radians: { int: 60 } },
      },
    },
    {
      name: 'pose3' as PoseName,
      pose: {
        x: 'val1' as ValueName,
        y: 'val2' as ValueName,
        heading: 'val3' as ValueName,
      },
    },
  ],
  beziers: [
    {
      name: 'bez1' as BezierName,
      points: {
        type: BezierType.Line,
        points: ['pose1' as PoseName, 'pose2' as PoseName],
      },
    },
    {
      name: 'bez2' as BezierName,
      points: simpleBez,
    },
  ],
  pathChains: [
    {
      name: 'pc1' as PathChainName,
      paths: ['bez1' as BezierName, 'bez2' as BezierName],
      heading: { type: FacingType.Tangent },
    },
    {
      name: 'pc2' as PathChainName,
      paths: [
        'bez2' as BezierName,
        {
          type: BezierType.Line,
          points: ['pose1' as PoseName, 'pose3' as PoseName],
        },
      ],
      heading: { type: FacingType.Constant, heading: 'pose3' as PoseName },
    },
    {
      name: 'pc3' as PathChainName,
      paths: [
        'bez1' as BezierName,
        {
          type: BezierType.Curve,
          points: [
            'pose1' as PoseName,
            'pose3' as PoseName,
            'pose2' as PoseName,
          ],
        },
      ],
      heading: {
        type: FacingType.Linear,
        start: 'pose2' as PoseName,
        end: { radians: { int: 135 } },
      },
    },
  ],
  // TODO
  container: { fileName: '' },
  children: {},
  pathChainHelpers: [],
};

const database: PathDatabase = {
  TeamPaths: MakeMultiMap<Team, PathKey>([
    [
      'team1' as Team,
      ['team1*path1.java' as PathKey, 'team1*path2.java' as PathKey],
    ],
    [
      'team2' as Team,
      ['team2*path3.java' as PathKey, 'team2*path4.java' as PathKey],
    ],
    ['LearnBot' as Team, ['LearnBot*TestPaths.java' as PathKey]],
  ]),
  PathClasses: MakeMultiMap<PathKey, ClassKey>([
    ['team1*path1.java' as PathKey, ['team1*path1.java;a' as ClassKey]],
    ['team1*path2.java' as PathKey, ['team1*path2.java;b' as ClassKey]],
    ['team2*path3.java' as PathKey, ['team2*path3.java;c' as ClassKey]],
    ['team2*path4.java' as PathKey, ['team2*path4.java;d' as ClassKey]],
    [
      'LearnBot*TestPaths.java' as PathKey,
      ['LearnBot*TestPaths.java;TestPaths' as ClassKey],
    ],
  ]),
  ParsedClasses: new Map<ClassKey, ParsedClass>([
    ['team1*path1.java;a' as ClassKey, EmptyParsedClass],
    ['team1*path2.java;b' as ClassKey, EmptyParsedClass],
    ['team2*path3.java;c' as ClassKey, fullParsedClass],
    ['team2*path4.java;d' as ClassKey, EmptyParsedClass],
    ['LearnBot*TestPaths.java;TestPaths' as ClassKey, TestPathsParsed],
  ]),
};

describe('IndexedFile', () => {
  test('MakeFileIndex', async () => {
    const index = MakeFileIndex(fullParsedClass);
    expect(index).toBeDefined();
    expect(index.namedValues.size).toBe(3);
    expect(index.namedPoses.size).toBe(3);
    expect(index.namedBeziers.size).toBe(2);
    expect(index.namedPathChains.size).toBe(3);
  });

  test('GetNameLookup', async () => {
    const lkup = GetNameLookup();
    expect(lkup).toBeDefined();
    expect(
      lkup.findBezier('nope' as BezierName, noParsedClass),
    ).toBeUndefined();
    // TODO: Add more tests for GetNameLookup, including scoped names to other
    // classes, and names that are not found in the current class but are found
    // in the parent class, etc...
  });

  test('ValidateIndex', async () => {
    const index = MakeFileIndex(TestPathsParsed);
    const lkup = GetNameLookup();
    lkup.setDb(database);
    const result = ValidateIndex(index, lkup);
    // if (result !== true) {
    //   console.error(result.errors());
    // }
    expect(result).toBe(true);
  });
});
