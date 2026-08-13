import { createStore } from 'jotai';

import {
  hasStrField,
  isDefined,
  Pickle,
  SafelyUnpickle,
  typecheck,
} from '@freik/typechk';

const theStore = createStore();

export type MyStore = typeof theStore;
// export type MaybeStore = MyStore | undefined;

export function getStore(curStore?: MyStore): MyStore {
  return curStore || theStore;
}

// This is the simple "sync with main" storage provider for Jotai

export async function fetchApi<T>(
  key: string,
  chk: typecheck<T>,
  def: T,
): Promise<T> {
  const fetched = await fetch('/api/' + key);
  let res = '';
  if (fetched.ok) {
    try {
      res = await fetched.text();
      const try2 = SafelyUnpickle(res, chk);
      if (isDefined(try2)) {
        return try2;
      }
    } catch {
      console.error('Received malformed message from server:', res);
      return def;
    }
    try {
      const val = JSON.parse(res);
      if (hasStrField(val, 'error')) {
        console.error('Received error from server', val.error);
      }
    } catch {
      console.error('Received malformed message from server:', res);
    }
  }
  return def;
}

export async function putApi(key: string, value: unknown): Promise<void> {
  const put = await fetch('/api/' + key, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: Pickle(value),
  });
  if (put.ok) {
    console.log(await put.text());
  } else {
    console.log(put);
  }
}
