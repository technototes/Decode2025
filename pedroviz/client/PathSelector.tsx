import { Text } from '@fluentui/react-components';
import { useAtom, useAtomValue } from 'jotai';
import { ReactElement } from 'react';
import { select_a_bot, select_a_file } from './constants';
import {
  BlurAtom,
  FilesForSelectedTeam,
  SelectedFileAtom,
  SelectedTeamAtom,
  TeamsAtom,
} from './state/Atoms';
import { AutoSelector } from './ui-tools/AutoSelector';

export function TeamSelector(): ReactElement {
  const teams = useAtomValue(TeamsAtom); //['TeamCode', 'LearnBot'];
  const [team, setTeam] = useAtom(SelectedTeamAtom);
  return (
    <AutoSelector
      prompt={select_a_bot}
      items={teams}
      selected={team}
      setSelected={setTeam}
      /* This is just while testing */
      // default="LearnBot"
    />
  );
}

export function FileSelector(): ReactElement {
  // TODO: get the atom from Jotai for the files
  const files = useAtomValue(FilesForSelectedTeam); // ['Path1.java', 'MyPaths.java'];
  const [file, setFile] = useAtom(SelectedFileAtom);
  return (
    <AutoSelector
      prompt={select_a_file}
      items={files}
      selected={file}
      setSelected={setFile}
    />
  );
}

export function PathSelector(): ReactElement {
  const blur = useAtomValue(BlurAtom);
  return (
    <>
      <TeamSelector />
      &nbsp;
      <FileSelector />
      &nbsp;
      <Text>{blur}</Text>
    </>
  );
}
