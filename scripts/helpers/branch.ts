import { Ask, Menu, MenuItem } from './menu.js';
import { promises as fsp } from 'node:fs';
import { simpleGit, type SimpleGit } from 'simple-git';

export const DEFAULT_BRANCH_NAME = 'main';

const git = simpleGit();

export function Clean(file: string): string {
  let res = file;
  // First, yoink diacriticals:
  res = res.normalize('NFD').replace(/\p{Diacritic}/gu, '');
  // Next, remove all non alpha-numeric stuff:
  res = res.replace(/[^0-9a-zA-Z]/g, '_');
  // Collapse multiple __'s into a single _
  res = res.replace(/__+/g, '_');
  // And trim initial/trailing underscores:
  return res.replace(/^_*/, '').replace(/_*$/, '');
}

function zpad(num: number): string {
  return num < 10 ? '0' + num.toString() : num.toString();
}

const NAME_PROMPT = "What's your user or name? ";
const TASK_PROMPT = 'What are you working on today (1-3 words)? ';

export async function GetBranchName(): Promise<string | undefined> {
  let task = await Ask(TASK_PROMPT);
  let user = await Ask(NAME_PROMPT);
  const today = new Date();
  const yr = today.getFullYear();
  const mo = today.getMonth() + 1;
  const dy = today.getDate();
  let branch = '';
  let done = false;
  let abandon = true;
  async function nameChange() {
    user = await Ask(NAME_PROMPT);
    return true;
  }
  async function taskChange() {
    task = await Ask(TASK_PROMPT);
    return true;
  }
  async function allDone() {
    done = true;
    abandon = false;
    return Promise.resolve(true);
  }
  async function abort() {
    done = true;
    return Promise.resolve(true);
  }
  do {
    branch = `${Clean(user)}-${yr}.${zpad(mo)}.${zpad(dy)}-${Clean(task)}`;
    await Menu(`Does "${branch}" look like a good branch name?`, [
      ['Yup: Start coding!', allDone],
      ['No: Change my name.', nameChange],
      ['No: Change the work description', taskChange],
      ['No: Nevermind, just stop', abort],
    ]);
  } while (!done);
  return abandon ? undefined : branch;
}

export async function ReadBranchName(): Promise<string | false> {
  return await git.revparse({ '--abbrev-ref': null, HEAD: null });
}

function selectBranchGen(name: string): () => Promise<boolean> {
  return async (): Promise<boolean> => {
    try {
      await git.checkout(name);
      await git.pull();
      return true;
    } catch (e) {
      return false;
    }
  };
}

export async function PickBranchToContinue(): Promise<void> {
  // git branch --list --all gets even the remote branches
  const branches = await git.branch({ '--list': null, '--all': null });

  // Read in any branch names to ignore. This is mostly used for mentors
  // to not show students branches that aren't relevant.
  let branchFilter = (name: string) => true;
  try {
    const ignore = await fsp.readFile('scripts/branches-to-ignore.txt', {
      encoding: 'utf8',
    });
    const namesToIgnore = ignore
      .split('\n')
      .map((v) => v.trim())
      .filter((v) => v.length > 0);
    if (namesToIgnore.length > 0) {
      const names = new Set<string>(namesToIgnore);
      branchFilter = (name: string) => !names.has(name);
    }
  } catch (e) {}

  // Remove any remote branches that aren't from origin.
  // This allows things like 'upstream' or personal remotes for folks
  // who know what they're doing.
  // Also remove the default branch name, and show only unique names
  const localOrOriginBranches = [
    ...new Set<string>(
      branches.all
        .filter(
          (val: string) =>
            !val.startsWith('remotes/') || val.startsWith('remotes/origin/'),
        )
        .map((name: string) =>
          name.startsWith('remotes/origin/') ? name.substring(15) : name,
        )
        .filter(branchFilter)
        .filter((val) => val !== DEFAULT_BRANCH_NAME)
        .sort((a, b) => a.localeCompare(b)),
    ),
  ];
  const menuOptions: MenuItem[] = localOrOriginBranches.map((name) => [
    name,
    selectBranchGen(name),
  ]);
  menuOptions.push(['Nevermind: go back', () => Promise.resolve(true)]);
  await Menu('Which branch would you like to continue using?', menuOptions);
}
