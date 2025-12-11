import {
  NamedBezier,
  NamedPathChain,
  NamedPose,
  NamedValue,
  PathChainFile,
} from '../../server/types';

export type IndexedPCF = PathChainFile & {
  namedValues: Map<string, NamedValue>;
  namedPoses: Map<string, NamedPose>;
  namedBeziers: Map<string, NamedBezier>;
  namedPathChains: Map<string, NamedPathChain>;
};

export type Point = { x: number; y: number };
