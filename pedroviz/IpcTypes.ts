import { MultiMap } from '@freik/containers';
import { ErrorOr } from '@freik/typechk';

import { ParsedClass } from './CodeTypes';
import { Nominal } from './TypeHelpers';

export type Team = Nominal<string, 'Team'>;
export type Path = Nominal<string, 'Path'>;
export type ClassName = Nominal<string, 'ClassName'>;

// A path key is a Path + * + seqnum
export type PathKey = Nominal<string, 'PathKey'>;
export type TeamPaths = MultiMap<Team, PathKey>;

// A class key is a Class + * + seqnum
export type ClassKey = Nominal<string, 'ClassKey'>;
export type PathClasses = MultiMap<PathKey, ClassKey>;

export type MaybePathFile = ErrorOr<ParsedClass>;
export type PathDatabase = {
  TeamPaths: TeamPaths;
  PathClasses: PathClasses;
  ParsedClasses: Map<ClassKey, ParsedClass>;
};
