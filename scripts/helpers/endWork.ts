import simpleGit, { PushResult } from 'simple-git';
import { DEFAULT_BRANCH_NAME, ReadBranchName } from './branch.js';
import { hasGithubAccess, onlyRobotConnection } from './connectivity.js';
import { GetGitHubUrlFromRepo } from './github.js';
import { invoke } from './invoke.js';
import { Error, Menu, MenuItem } from './menu.js';

const git = simpleGit();

export const FinishWork: MenuItem = [
  'Finish work: ready for merging',
  finishWork,
];
export const StopWork: MenuItem = [
  'Stop for the day (not yet ready for merging)',
  stopWork,
];

async function addFiles(files: string[], message: string): Promise<boolean> {
  console.log(await git.add(files).commit(message));
  return true;
}

// Helper for stop & finish:
async function completeWork(): Promise<false | PushResult> {
  // Repo dirty check:
  const status = await git.status();
  if (!status.isClean()) {
    return Error("You appear to have some work that isn't yet committed!");
  }

  // Un-added file check
  if (status.not_added.length !== 0) {
    let abort = false;
    console.log('These files:');
    console.log(status.not_added);
    await Menu('What should be done with those untracked files?', [
      [
        "Ignore them: It's fine. Trust me. I know what I'm doing.",
        () => Promise.resolve(true),
      ],
      [
        'Please add them to the repository.',
        () => addFiles(status.not_added, 'Oops, missed some files!'),
      ],
      [
        'Abort! Abort! Abort!',
        () => {
          abort = true;
          return Promise.resolve(true);
        },
      ],
    ]);
    if (abort) {
      return false;
    }
  }

  // Check for network access
  if (!(await hasGithubAccess())) {
    if (onlyRobotConnection()) {
      return Error(
        "It looks like you're connected to the robot. Please fix that before continuing.",
      );
    } else {
      return Error(
        'Unable to communicate with GitHub: Check your internet connection',
      );
    }
  }

  // Run the code formatting
  await invoke('bun format');
  const fmtStat = await git.status();
  if (!fmtStat.isClean()) {
    console.log('Some files were re-formatted. Commiting the change.');
    await addFiles(fmtStat.modified, 'Auto-formatted files');
  }

  const curBranchNameOrErr = await ReadBranchName();
  if (curBranchNameOrErr === false) {
    console.log("Couldn't get the curent branch name.");
    return false;
  }
  // Push the code (Wire it  properly)
  return await git.push(['-u', 'origin', curBranchNameOrErr]);
}

async function stopWork(): Promise<boolean> {
  // This should:
  // - Commit any outstanding work
  // - Check for any unadded files
  // - Make sure we've got network access
  // - Format code and add the commit if there were changes
  // - Push the code to github
  // - Check out main
  const failureOrPullRes = await completeWork();

  if (failureOrPullRes === false) {
    return false;
  }

  // Check out main
  console.log(await git.checkout(DEFAULT_BRANCH_NAME));
  const cur = await git.branch();
  if (cur.current !== DEFAULT_BRANCH_NAME) {
    return Error(`Unable to check out the ${DEFAULT_BRANCH_NAME} branch.`);
  }
  return false;
}

async function finishWork(): Promise<boolean> {
  // This should:
  // - Commit any outstanding work
  // - Check for any unadded files
  // - Make sure we've got network access
  // - Format code and add the commit if there were changes
  // - Push the code to github
  // - Try to build?
  // - If the build succeeded, offer to open a PR

  const failureOrPullRes = await completeWork();

  if (failureOrPullRes === false) {
    return false;
  }
  const pullRes: PushResult = failureOrPullRes;
  const branch = await ReadBranchName();
  if (branch === false || typeof pullRes.repo !== 'string') {
    console.log('Some weird error: Ask for help.');
    return true;
  }
  const url = `${GetGitHubUrlFromRepo(
    pullRes.repo,
  )}/compare/${DEFAULT_BRANCH_NAME}...${branch}`;
  switch (process.platform) {
    case 'win32':
      await invoke(`start "" "${url}"`);
      break;
    case 'darwin':
      await invoke('open ' + url);
      break;
    case 'linux':
      await invoke('xdg-open ' + url);
      break;
  }
  return false;
}
