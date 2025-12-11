/*
 * This is the 'student workflow' script.
 * See the readme.md file for what it's supposed to do.
 */
import { $ } from 'bun';
import { ResumeWork, StartWork } from './helpers/beginWork.js';
import { anyRobotConnection } from './helpers/connectivity.js';
import { FinishWork, StopWork } from './helpers/endWork.js';
import { invoke } from './helpers/invoke.js';
import { Error, Menu } from './helpers/menu.js';

async function workflow() {
  console.clear();
  await Menu('What do you want to do?', [
    StartWork,
    ResumeWork,
    FinishWork,
    StopWork,
    ['Connect to the control hub', connect],
    ['Disconnect from control hub', disconnect],
    ['Launch the dashboard', launchDash],
    [
      "Setup .gitconfig file so Kevin isn't constantly frustrated",
      configureStuff,
    ],
    ['Quit', () => Promise.resolve(true)],
  ]);
}

async function addAlias(alias: string, command: string): Promise<void> {
  await $`git config --global alias.${alias} ${command}`;
}

// Nothing in here yet...
async function configureStuff(): Promise<boolean> {
  // This should:
  // Add my silly things to .gitconfig
  console.log('Configuring stuff');
  await addAlias('st', 'status');
  await addAlias(
    'sl',
    "log --graph --oneline --decorate --pretty=format:'%Cred%h%Creset%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --date=relative --all --since='365 days ago'",
  );
  await addAlias(
    'gr',
    "log --graph --oneline --decorate --pretty=format:'%Cred%h%Creset%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --date=relative --all --since='365 days ago' -n 5000",
  );
  await addAlias('co', 'checkout');
  await addAlias('unstage', 'reset HEAD --');
  await addAlias('amend', 'commit --amend');
  await addAlias('branches', 'branch --list --all');
  await addAlias('tags', 'tag --list');
  return false;
}

async function connect(): Promise<boolean> {
  if (!anyRobotConnection()) {
    return Error("You don't appear to be connected to a robot.");
  }
  const res = await invoke('bun connect');
  console.log(res.stdout);
  console.log(res.stderr);
  return false;
}

async function disconnect(): Promise<boolean> {
  const res = await invoke('bun discall');
  console.log(res.stdout);
  console.log(res.stderr);
  return false;
}

async function launchDash(): Promise<boolean> {
  const res = await invoke('bun dash');
  console.log(res.stdout);
  console.log(res.stderr);
  return false;
}

workflow()
  .catch((err) => console.error(err))
  .finally(() => {
    // console.log('All Done');
    process.exit(0);
  });
