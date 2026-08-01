import { ErrorOr } from '@freik/typechk';

import { ParsedClass } from './CodeTypes';
import { Nominal } from './TypeHelpers';

export type Team = Nominal<string, 'Team'>;
export type Path = Nominal<string, 'Path'>;
export type TeamPaths = Record<Team, Path[]>;
export type MaybePathFile = ErrorOr<ParsedClass>;
export type PathDBKey = Nominal<string, 'DBKey'>;
export type PathDBValue = [string[], ParsedClass];
export type PathDatabase = Map<PathDBKey, PathDBValue>;
