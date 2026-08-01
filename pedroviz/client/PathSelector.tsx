import { ReactElement, useEffect } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import { Label, Text } from '@fluentui/react-components';
import { Path } from 'IpcTypes';

import { Strings } from './constants';
import {
  BlurAtom,
  ClassesForSelectedFileAtom,
  FilesForSelectedTeamAtom,
  SelectedClassAtom,
  SelectedFileAtom,
  SelectedTeamAtom,
  TeamsAtom,
} from './state/Atoms';
import { AutoSelector } from './ui-tools/AutoSelector';

export function TeamSelector(): ReactElement {
  const teams = useAtomValue(TeamsAtom); //['TeamCode', 'LearnBot'];
  const [team, setTeam] = useAtom(SelectedTeamAtom);
  useEffect(() => {
    if (teams.length === 1) {
      setTeam(teams[0]!);
    }
  }, [teams, setTeam]);
  if (teams.length === 1) {
    return <Label className="pathLabel">Robot: {teams[0]!}</Label>;
  }
  return (
    <>
      <Label className="pathLabel">Robot:</Label>
      <AutoSelector
        prompt={Strings.select_a_bot}
        items={teams}
        selected={team}
        setSelected={setTeam}
      />
    </>
  );
}

export function FileSelector(): ReactElement {
  let files = useAtomValue(FilesForSelectedTeamAtom);
  const [file, setFile] = useAtom(SelectedFileAtom);
  // if all the files have a common folder prefix, filter the prefix out
  let prefix = '';
  if (files.length > 0) {
    let tryIt = files[0]!.indexOf('/');
    while (tryIt >= 0) {
      const tryPrefix = files[0]!.substring(0, tryIt + 1);
      if (files.every((p) => p.startsWith(tryPrefix))) {
        prefix += tryPrefix;
        files = files.map((p) => p.substring(tryIt + 1) as Path);
        tryIt = files[0]!.indexOf('/');
      } else {
        break;
      }
    }
  }
  useEffect(() => {
    if (files.length === 1) {
      setFile(files[0]!);
    }
  }, [files, setFile]);
  if (files.length === 1) {
    return <Label className="pathLabel">File: {files[0]}</Label>;
  }
  return (
    <>
      <Label className="pathLabel">File:</Label>
      <AutoSelector
        prompt={Strings.select_a_file}
        items={files}
        selected={file.substring(prefix.length)}
        setSelected={(item) => setFile(prefix + item)}
      />
    </>
  );
}

export function ClassSelector(): ReactElement {
  const classes = useAtomValue(ClassesForSelectedFileAtom);
  const [classSel, setClass] = useAtom(SelectedClassAtom);
  useEffect(() => {
    if (classes.length === 1) {
      setClass(classes[0]!);
    }
  }, [classes, setClass]);
  if (classes.length === 1) {
    return <Label className="pathLabel">Class: {classes[0]}</Label>;
  }
  return (
    <>
      <Label className="pathLabel">Class:</Label>
      <AutoSelector
        prompt={Strings.select_a_class}
        items={classes}
        selected={classSel}
        setSelected={setClass}
      />
    </>
  );
}

export function PathSelector(): ReactElement {
  const blur = useAtomValue(BlurAtom);
  return (
    <>
      <TeamSelector />
      <FileSelector />
      <ClassSelector />
      &nbsp;
      <Text>{blur}</Text>
    </>
  );
}
