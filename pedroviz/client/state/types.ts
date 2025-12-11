import { PathChainFile } from '../../server/types';

export type IndexedPCF = PathChainFile & {
  namedValues: Map<string, number>;
  namedPoses: Map<string, number>;
  namedBeziers: Map<string, number>;
  namedPathChains: Map<string, number>;
};

export type Point = { x: number; y: number };
