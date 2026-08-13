import { ReactElement, useEffect } from 'react';
import { useAtom, useAtomValue } from 'jotai';

import { Label, Text } from '@fluentui/react-components';

import { Path } from '../IpcTypes';
import { Strings } from './constants';
import {
  BlurAtom,
  ClassesForSelectedPathAtom,
  PathsForSelectedTeamAtom,
  SelectedClassAtom,
  SelectedPathAtom,
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
  return (
    <span>
      <Label className="pathLabel">Robot:</Label>
      <AutoSelector
        prompt={Strings.select_a_bot}
        items={teams}
        selected={team}
        setSelected={setTeam}
      />
    </span>
  );
}

export function FileSelector(): ReactElement {
  let files = useAtomValue(PathsForSelectedTeamAtom);
  const [file, setFile] = useAtom(SelectedPathAtom);
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
  return (
    <span>
      <Label className="pathLabel" htmlFor="select_a_file">
        File:
      </Label>
      <AutoSelector
        id="select_a_file"
        prompt={Strings.select_a_file}
        items={files}
        selected={file.substring(prefix.length)}
        setSelected={(item) => setFile((prefix + item) as Path)}
      />
    </span>
  );
}

export function ClassSelector(): ReactElement {
  const classes = useAtomValue(ClassesForSelectedPathAtom);
  const [classSel, setClass] = useAtom(SelectedClassAtom);
  useEffect(() => {
    if (classes.length === 1) {
      setClass(classes[0]!);
    }
  }, [classes, setClass]);
  return (
    <span>
      <Label className="pathLabel">Class:</Label>
      <AutoSelector
        prompt={Strings.select_a_class}
        items={classes}
        selected={classSel}
        setSelected={setClass}
      />
    </span>
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
