import simpleGit from 'simple-git';
import {
  DEFAULT_BRANCH_NAME,
  GetBranchName,
  PickBranchToContinue,
} from './branch.js';
import { hasGithubAccess, onlyRobotConnection } from './connectivity.js';
import { Error, MenuItem, Sleep } from './menu.js';

const git = simpleGit();

export const StartWork: MenuItem = ['Begin new work for the day', startWork];
export const ResumeWork: MenuItem = [
  'Resume work from previous day',
  resumeWork,
];

// Helper for start & resumt
async function getStarted(): Promise<boolean> {
  // Repo dirty check:
  const status = await git.status();
  if (!status.isClean()) {
    return Error("You appear to have some work that isn't yet committed.");
  }

  // GitHub access check
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

  // Check out main
  console.log(await git.checkout(DEFAULT_BRANCH_NAME));
  const cur = await git.branch();
  if (cur.current !== DEFAULT_BRANCH_NAME) {
    return Error(`Unable to check out the ${DEFAULT_BRANCH_NAME} branch.`);
  }
  // Pull from github
  /* const pullRes = */ await git.pull();
  // console.log(pullRes);
  return true;
}

async function startWork(): Promise<boolean> {
  // This should:
  // --- Begin getStarted stuff
  // - Ensure the repo isn't dirty
  // - Make sure we've got network access
  // - Check out main
  // - Pull from main
  // --- End getStarted stuff
  // - Create a new branch for the work
  // - Open up Android Studio

  if ((await getStarted()) === false) {
    return false;
  }
  // Get the name for the new branch
  const branchName = await GetBranchName();
  if (typeof branchName !== 'string') {
    return false;
  }
  // Let's create the branch
  /* const coRes = */ await git.checkout(['-b', branchName]);
  // console.log(coRes);
  console.log(
    "You're ready to code! Come back to this window when you're done.",
  );
  await Sleep(3000);
  // Maybe open android studio automatically?
  return false;
}

async function resumeWork(): Promise<boolean> {
  // This should:

  // --- Begin getStarted stuff
  // - Ensure the repo isn't dirty
  // - Make sure we've got network access
  // - Check out main
  // - Pull from main
  // --- End getStarted stuff

  // - Pull all branch names
  // - Ask which branch to check out
  // - Check out the branch to continue
  // - Ensure it's up-to-date (pull)
  // - Open up Android Studio

  if ((await getStarted()) === false) {
    return false;
  }

  // Continue from "pull all branch names"
  await PickBranchToContinue();
  console.log("Come back to this window when you're done.");
  await Sleep(3000);
  // Maybe open android studio automatically?
  return false;
}
