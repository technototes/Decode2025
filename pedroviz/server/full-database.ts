import { isDefined, isUndefined } from '@freik/typechk';

import { GetTeamPaths } from './getpaths';
import { GetPathChainIndex } from './loadpath';
import {
  ErrorOr,
  isError,
  makeError,
  Path,
  PathChainClass,
  PathDatabase,
  PathDBKey,
  Team,
  TeamPaths,
} from './types';

const teampaths: Map<Team, Path[]> = new Map();
const database: PathDatabase = new Map();

function makeKey(team: Team, path: Path): PathDBKey {
  return `${team}*${path}` as PathDBKey;
}

function getTeamPath(dbKey: PathDBKey): [Team, Path] {
  const items = dbKey.split('*');
  if (items.length === 2) {
    return items as [Team, Path];
  }
  console.error('Invalid key provided: ', dbKey);
  return ['team', 'path'] as [Team, Path];
}

// Returns true if that file has *any* items we care about in it.
// This does wind up triggering for something that just has a static int/double,
// but that's okay (better than missing one...)
function anyItems(pcc: PathChainClass): boolean {
  const work = [pcc];
  while (work.length > 0) {
    const item = work.pop();
    if (isUndefined(item)) {
      break;
    }
    if (
      item.beziers.length ||
      item.poses.length ||
      item.pathChains.length ||
      item.values.length
    ) {
      return true;
    }
    work.push(...Object.values(item.children));
  }
  return false;
}

function RegisterPathChainIndex(
  team: Team,
  path: Path,
  classList: string[],
  pcc: PathChainClass,
): void {
  if (!anyItems(pcc)) {
    return;
  }
  console.log('Registering', team, path, classList);
  const paths = teampaths.get(team);
  if (isDefined(paths)) {
    paths.push(path);
  } else {
    teampaths.set(team, [path]);
  }
  database.set(makeKey(team, path), [classList, pcc]);
}

export async function PopulateDatabase() {
  const teamPaths: TeamPaths = await GetTeamPaths();
  for (const team of Object.keys(teamPaths) as Team[]) {
    for (const path of teamPaths[team]!) {
      const pci = await GetPathChainIndex(team, path);
      if (!isError(pci)) {
        RegisterPathChainIndex(team, path, pci[0], pci[1]);
      }
    }
  }
}

export function GetAllIndexContent(): [Team, Path, string[]][] {
  return [...database.entries()].map(([key, [classes, pcc]]) => {
    const [team, path] = getTeamPath(key);
    return [team, path, classes];
  });
}

export function GetFullPathChain(
  team: Team,
  path: Path,
): PathChainClass | undefined {
  const entry = database.get(makeKey(team, path));
  return entry && entry[1];
}

export function GetDatabase(): PathDatabase {
  return database;
}

// Interfaces to the web server to talk to the web client:

export function WebGetPathChainClassList(
  team: Team,
  path: Path,
): ErrorOr<string[]> {
  const res = database.get(makeKey(team, path));
  if (isUndefined(res)) {
    console.log(database);
    return makeError(`${team}:${path} no Pedro pathing classes found`);
  }
  return res[0];
}

export function WebGetPathChainClassRoot(
  team: Team,
  path: Path,
): ErrorOr<PathChainClass> {
  const res = database.get(makeKey(team, path));
  if (isUndefined(res)) {
    return makeError(`${team}:${path} no Pedro pathing classes found`);
  }
  return res[1];
}

export function WebGetTeamPaths(): TeamPaths {
  const teamPaths: TeamPaths = {};
  teampaths.forEach((paths, team) => {
    teamPaths[team] = paths;
  });
  return teamPaths;
}

/*
if (import.meta.main) {
  PopulateDatabase()
    .then(() => console.log('done'))
    .catch(console.error);
}
*/
