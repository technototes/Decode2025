import {
  chkAnyOf,
  chkObjectOfExactType,
  chkTupleOf,
  isNumber,
  typecheck,
} from '@freik/typechk';
import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierRef,
  HeadingRef,
  HeadingType,
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  PathChainFile,
  PoseRef,
  ValueRef,
} from '../../server/types';

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: HeadingType;
};

export type ConcreteTangentHeading = { htype: 'T' };
export const chkConreteTangentHeading =
  chkObjectOfExactType<ConcreteTangentHeading>({
    htype: (t: unknown): t is 'T' => t === 'T',
  });
export type ConcreteConstantHeading = {
  htype: 'C';
  heading: number;
};
export const chkConcreteConstantHeading =
  chkObjectOfExactType<ConcreteConstantHeading>({
    htype: (t: unknown): t is 'C' => t === 'C',
    heading: isNumber,
  });
export type ConcreteInterpolatedHeading = {
  htype: 'I';
  headings: [number, number];
};
export const chkConcreteInterpolatedHeading =
  chkObjectOfExactType<ConcreteInterpolatedHeading>({
    htype: (t: unknown): t is 'I' => t === 'I',
    headings: chkTupleOf(isNumber, isNumber),
  });
export type ConcreteHeadingType =
  | ConcreteTangentHeading
  | ConcreteConstantHeading
  | ConcreteInterpolatedHeading;
export const chkConcreteHeadingType: typecheck<ConcreteHeadingType> = chkAnyOf(
  chkConreteTangentHeading,
  chkConcreteConstantHeading,
  chkConcreteInterpolatedHeading,
);
export type IndexedPCF = PathChainFile & {
  namedValues: Map<string, number>;
  namedPoses: Map<string, number>;
  namedBeziers: Map<string, number>;
  namedPathChains: Map<string, number>;
};

export type IndexedPCFile = {
  values: Map<string, AnonymousValue>;
  poses: Map<string, AnonymousPose>;
  beziers: Map<string, AnonymousBezier>;
  pathChains: Map<string, AnonymousPathChain>;
};

export type Point = { x: number; y: number };

export type IndexedFile = {
  getValues(): NamedValue[];
  getPoses(): NamedPose[];
  getBeziers(): NamedBezier[];
  getPathChains(): NamedPathChain[];

  getValueNames(): string[];
  getPoseNames(): string[];
  getBezierNames(): string[];
  getPathChainNames(): string[];

  getValue(name: string): AnonymousValue | undefined;
  getPose(name: string): AnonymousPose | undefined;
  getBezier(name: string): AnonymousBezier | undefined;
  getPathChain(name: string): AnonymousPathChain | undefined;

  setValue(name: string, value: AnonymousValue): void;
  setPose(name: string, pose: AnonymousPose): void;
  setBezier(name: string, bezier: AnonymousBezier): void;
  setPathChain(name: string, pathChain: AnonymousPathChain): void;

  getValueRefValue(vr: ValueRef): number;
  getPoseRefPoint(pr: PoseRef): Point;
  getBezierRefPoints(br: BezierRef): Point[];
  getHeadingRefValue(hr: HeadingRef): number;

  dump(): string;
};
