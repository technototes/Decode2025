import {
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierRef,
  HeadingType,
  PathChainFile,
} from '../../server/types';

export type AnonymousPathChain = {
  paths: BezierRef[];
  heading: HeadingType;
};

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
