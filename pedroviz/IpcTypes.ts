import { MultiMap } from '@freik/containers';
import { ErrorOr } from '@freik/typechk';

import { ParsedClass } from './CodeTypes';
import { Nominal } from './TypeHelpers';

export type Team = Nominal<string, 'Team'>;
export type Path = Nominal<string, 'Path'>;
// A path key is a Path + * + seqnum
export type PathKey = Nominal<string, 'PathKey'>;
// A class key is a Class + * + seqnum
export type ClassKey = Nominal<string, 'ClassKey'>;
export type MaybePathFile = ErrorOr<ParsedClass>;
export type PathDatabase = {
  TeamPaths: MultiMap<Team, PathKey>;
  PathClasses: MultiMap<PathKey, ClassKey>;
  ParsedClasses: Map<ClassKey, ParsedClass>;
};
