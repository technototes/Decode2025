import { isNumber, isString } from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousFacing,
  AnonymousPose,
  BezierName,
  BezierRef,
  BezierType,
  FacingConstant,
  FacingLinear,
  FacingPiece,
  FacingPieceWise,
  FacingPoint,
  FacingReversed,
  FacingReversible,
  FacingSimple,
  FacingTangent,
  FacingType,
  HeadingRef,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../CodeTypes';

export function mkValueRef(val: number | string): ValueRef {
  if (isString(val)) {
    return val as ValueName;
  } else if (Number.isInteger(val)) {
    return { int: val };
  } else {
    return { double: val };
  }
}

export function mkNamedValue(name: string, val: number | string): NamedValue {
  return { name: name as ValueName, value: mkValueRef(val) };
}

export function mkRadiansRef(val: number | string): RadiansRef {
  return { radians: mkValueRef(val as ValueName) };
}

export function mkNamedRadians(name: string, val: number | string): NamedValue {
  return { name: name as ValueName, value: mkRadiansRef(val) };
}

export function mkPoseRef(
  x: number | string,
  y: number | string,
  heading?: number | string,
): PoseRef {
  const pose: AnonymousPose = {
    x: mkValueRef(x),
    y: mkValueRef(y),
  };
  if (heading !== undefined) {
    pose.heading = mkValueRef(heading);
  }
  return pose;
}

export function mkNamedPose(
  name: string,
  x: number | string,
  y: number | string,
  heading?: number | string,
): NamedPose {
  const pose: AnonymousPose = {
    x: mkValueRef(x),
    y: mkValueRef(y),
  };
  if (heading !== undefined) {
    pose.heading = mkValueRef(heading);
  }
  return { name: name as PoseName, pose };
}

export function mkNamedPoseRad(
  name: string,
  x: number | string,
  y: number | string,
  heading: number | string,
): NamedPose {
  const pose: AnonymousPose = {
    x: mkValueRef(x),
    y: mkValueRef(y),
    heading: mkRadiansRef(heading),
  };
  return { name: name as PoseName, pose };
}

export function mkNamedLine(
  name: string,
  start: string | AnonymousPose,
  end: string | AnonymousPose,
): NamedBezier {
  return {
    name: name as BezierName,
    points: {
      type: BezierType.Line,
      points: [start as PoseRef, end as PoseRef],
    },
  };
}

export function mkAnonymousBezier(
  ...points: (string | AnonymousPose)[]
): AnonymousBezier {
  return {
    type: points.length === 2 ? BezierType.Line : BezierType.Curve,
    points: points as PoseRef[],
  };
}

export function mkNamedCurve(
  name: string,
  points: (string | AnonymousPose)[],
): NamedBezier {
  return {
    name: name as BezierName,
    points: mkAnonymousBezier(...points),
  };
}

export function mkNamedPathChain(
  name: string,
  paths: (string | AnonymousBezier)[],
  heading: AnonymousFacing,
): NamedPathChain {
  return {
    name: name as PathChainName,
    paths: paths as BezierRef[],
    heading,
  };
}

export function mkFacingTangent(): FacingTangent {
  return { type: FacingType.Tangent };
}
export function mkFacingConstant(heading: string | HeadingRef): FacingConstant {
  return { type: FacingType.Constant, heading: heading as HeadingRef };
}
export function mkFacingLinear(
  start: HeadingRef | string,
  end: HeadingRef | string,
): FacingLinear {
  return {
    type: FacingType.Linear,
    start: start as HeadingRef,
    end: end as HeadingRef,
  };
}
export function mkFacingPoint(point: PoseRef | string): FacingPoint {
  return { type: FacingType.Point, point: point as PoseRef };
}
export function mkFacingReversed(facing: FacingReversible): FacingReversed {
  return { type: FacingType.Reversed, facing };
}
export function mkFacingPiece(
  heading: FacingSimple,
  start: number | ValueRef,
  end: number | ValueRef,
): FacingPiece {
  return {
    heading,
    timing: {
      start: isNumber(start) ? { double: start } : start,
      end: isNumber(end) ? { double: end } : end,
    },
  };
}
export function mkFacingPiecewise(...pieces: FacingPiece[]): FacingPieceWise {
  return { type: FacingType.Piecewise, pieces };
}

export const TestPathsParsed: ParsedClass = {
  name: 'TestPaths',
  fullName: 'org.firstinspires.ftc.learnbot.TestPaths',
  imports: ['org.firstinspires.ftc.learnbot'],
  pathChainHelpers: [],
  container: {
    fileName:
      '../LearnBot/src/main/java/org/firstinspires/ftc/learnbot/TestPaths.java',
  },
  children: {},
  values: [
    mkNamedValue('org', 15),
    mkNamedValue('edge', 50),
    mkNamedValue('orgu', 130),
    mkNamedValue('edgeu', 90),
    mkNamedValue('extra', 25),
    mkNamedValue('extra2', 27),
    mkNamedRadians('one80', 180),
    mkNamedValue('refVal', 'edge'),
    mkNamedValue('sixty', 60),
    mkNamedValue('ninetyD', 90),
    mkNamedRadians('ninety', 'ninetyD'),
  ],
  poses: [
    mkNamedPoseRad('start', 'org', 'org', 0),
    mkNamedPose('step1', 'edge', 'org', 'ninety'),
    mkNamedPose('step2', 'edge', 'refVal', 35),
    mkNamedPoseRad('step3', 'extra', 'extra2', 'sixty'),
    mkNamedPose('step4', 'orgu', 'orgu', 'one80'),
    mkNamedPoseRad('startu', 'orgu', 'orgu', 0),
    mkNamedPose('step1u', 'edgeu', 'orgu', 'ninety'),
    mkNamedPose('step2u', 'edgeu', 'refVal', 35),
    mkNamedPoseRad('step3u', 'extra', 'extra2', 'sixty'),
    mkNamedPose('step4u', 'orgu', 'orgu', 'one80'),
    mkNamedPoseRad('stepb', 'extra', 'extra2', 'sixty'),
    mkNamedPose('stepc', 15, 20),
    mkNamedPoseRad('stepd', 18, 55, 135),
  ],
  beziers: [
    mkNamedLine('start_to_step1', 'start', 'step1'),
    mkNamedCurve('unused1', ['step1', 'step2', 'step4', 'step1']),
    mkNamedCurve('step1_to_step2', ['step1', 'stepb', 'step2']),
    mkNamedLine('u1_u2', 'step1', mkPoseRef('org', 'edge')),
    mkNamedLine('unused2', mkPoseRef('org', 'edge'), 'start'),
    mkNamedLine('u2_u3', 'start', mkPoseRef('edge', 5, 15)),
    mkNamedCurve('unused3', [
      mkPoseRef('edge', 5, 15),
      'start',
      mkPoseRef(5, 5),
    ]),
    mkNamedLine('u3_u4', mkPoseRef(5, 5), 'start'),
    mkNamedCurve('unused4', [
      'start',
      mkPoseRef(15, 25),
      mkPoseRef(55, 44),
      mkPoseRef(10, 'org'),
      mkPoseRef('edge', 10, 'sixty'),
      'step1',
    ]),
    mkNamedLine('u4_ol', 'step1', 'stepc'),
    mkNamedLine('otherLine', 'stepc', 'stepd'),
    mkNamedLine('start_to_step1_5', 'startu', 'step1u'),
    mkNamedCurve('unused1_5', ['step1u', 'step2u', 'step4u', 'step1u']),
    mkNamedLine('u1_u2_5', 'step1u', mkPoseRef('orgu', 'edgeu')),
    mkNamedLine('unused2_5', mkPoseRef('orgu', 'edgeu'), 'startu'),
    mkNamedLine('u2_u3_5', 'startu', mkPoseRef('edgeu', 95, 15)),
    mkNamedCurve('unused3_5', [
      mkPoseRef('edgeu', 95, 15),
      'startu',
      mkPoseRef(95, 95),
    ]),
    mkNamedLine('u3_u4_5', mkPoseRef(5, 5), 'startu'),
    mkNamedCurve('unused4_5', [
      'startu',
      mkPoseRef(95, 125),
      mkPoseRef(85, 133),
      mkPoseRef(130, 'orgu'),
      mkPoseRef('edgeu', 10, 'sixty'),
      'step1u',
    ]),
    mkNamedLine('u4_ol_5', 'step1u', 'stepc'),
    mkNamedLine('otherLine_5', 'stepc', 'stepd'),
  ],
  pathChains: [
    mkNamedPathChain(
      'Path1',
      [
        'start_to_step1',
        'unused1',
        mkAnonymousBezier(
          'step1',
          mkPoseRef(10, 'extra'),
          'step4',
          mkPoseRef('edge', 10),
          'step1',
        ),
      ],
      mkFacingLinear({ int: 0 }, 'ninety'),
    ),
    mkNamedPathChain(
      'Path2',
      [mkAnonymousBezier('step1', 'stepb', 'step2')],
      mkFacingConstant('step3'),
    ),
    mkNamedPathChain(
      'Path3',
      [mkAnonymousBezier('step2', 'step3')],
      mkFacingTangent(),
    ),
    mkNamedPathChain(
      'Path4',
      [mkAnonymousBezier('step3', 'step1u', 'step4')],
      mkFacingPiecewise(
        mkFacingPiece(mkFacingTangent(), 0, 0.2),
        mkFacingPiece(
          mkFacingPoint({ x: { int: 5 }, y: { int: 5 } }),
          0.2,
          0.4,
        ),
        mkFacingPiece(
          mkFacingConstant({
            radians: {
              int: 90,
            },
          }),
          0.4,
          0.6,
        ),
        mkFacingPiece(
          mkFacingLinear({ radians: { int: 90 } }, 'Math.PI'),
          0.6,
          0.8,
        ),
        mkFacingPiece(
          mkFacingReversed(mkFacingLinear('Math.PI', { radians: { int: 90 } })),
          0.8,
          1.0,
        ),
      ),
    ),
    mkNamedPathChain(
      'Path5',
      [
        'unused1_5',
        'u1_u2_5',
        'unused2_5',
        'u2_u3_5',
        'unused3_5',
        'u3_u4_5',
        'unused4_5',
        'u4_ol_5',
        'otherLine_5',
      ],
      mkFacingPoint({
        x: {
          int: 1,
        },
        y: {
          int: 1,
        },
      }),
    ),
  ],
};
