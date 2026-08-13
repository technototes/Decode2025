import { describe, expect, test } from 'bun:test';

import { MakeMultiMap } from '@freik/containers';
import { Pickle, Unpickle } from '@freik/typechk';

import { chkPathDatabase } from '../../IpcTypeCheck';
import { Team } from '../../IpcTypes';
import { RescanSourceCode, ResetDatabase } from '../full-database';
import { LoadDatabase, LoadPath, SaveDatabase } from '../web-interface';

describe('The basic web interfaces', () => {
  test('LoadDatabase', async () => {
    ResetDatabase();
    const dbRes = await LoadDatabase();
    expect(dbRes.ok).toBeTrue();
    expect(dbRes.status).toEqual(200);
    const json = await dbRes.json();
    const data = Unpickle(JSON.stringify(json));
    expect(data).toBeDefined();
    expect(chkPathDatabase(data)).toBeTrue();
    if (!chkPathDatabase(data)) {
      return false;
    }
    expect(data.TeamPaths.size()).toEqual(0);
    await RescanSourceCode();
    const dbRes2 = await LoadDatabase();
    expect(dbRes2.ok).toBeTrue();
    expect(dbRes2.status).toEqual(200);
    const json2 = await dbRes2.json();
    const data2 = Unpickle(JSON.stringify(json2));
    expect(data2).toBeDefined();
    expect(chkPathDatabase(data2)).toBeTrue();
    if (!chkPathDatabase(data2)) {
      return false;
    }
    expect(data2.TeamPaths.has('LearnBot' as Team)).toBeTrue();
  });
  test('LoadPath', async () => {
    ResetDatabase();
    await RescanSourceCode();
    const res = await LoadPath('LearnBot', 'TestPaths.java');
    expect(res.ok).toBeTrue();
    expect(res.status).toEqual(200);
    const json = await res.json();
    const data = Unpickle(JSON.stringify(json));
    expect(data).toBeDefined();
    // console.log(data);
  });
  test('SaveDatabase', async () => {
    ResetDatabase();
    await RescanSourceCode();
    const dbRes2 = await LoadDatabase();
    expect(dbRes2.ok).toBeTrue();
    expect(dbRes2.status).toEqual(200);
    const json2 = await dbRes2.json();
    const data2 = Unpickle(JSON.stringify(json2));
    expect(data2).toBeDefined();
    expect(chkPathDatabase(data2)).toBeTrue();
    if (!chkPathDatabase(data2)) {
      return false;
    }
    expect(data2.TeamPaths.has('LearnBot' as Team)).toBeTrue();
    const newDB = {
      TeamPaths: MakeMultiMap(),
      PathClasses: MakeMultiMap(),
      ParsedClasses: new Map(),
    };
    await SaveDatabase(Pickle(newDB));
    const dbRes = await LoadDatabase();
    expect(dbRes.ok).toBeTrue();
    expect(dbRes.status).toEqual(200);
    const json = await dbRes.json();
    const data = Unpickle(JSON.stringify(json));
    expect(data).toBeDefined();
    expect(chkPathDatabase(data)).toBeTrue();
    if (!chkPathDatabase(data)) {
      return false;
    }
    expect(data.TeamPaths.size()).toEqual(0);
  });
});
