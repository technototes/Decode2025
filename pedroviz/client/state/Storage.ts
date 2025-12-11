import { typecheck } from '@freik/typechk';
import { createStore } from 'jotai';

// import { atomWithStorage } from 'jotai/utils';
// import { AsyncStorage } from 'jotai/vanilla/utils/atomWithStorage';
// import { WritableAtomType } from './Hooks';

const theStore = createStore();

export type MyStore = typeof theStore;
export type MaybeStore = MyStore | undefined;

export function getStore(curStore?: MyStore): MyStore {
  return curStore || theStore;
}

// This is the simple "sync with main" storage provider for Jotai

export async function fetchApi<T>(
  key: string,
  chk: typecheck<T>,
  def?: T,
): Promise<T> {
  const fetched = await fetch('/api/' + key);
  if (fetched.ok) {
    const res = await fetched.json();
    if (chk(res)) {
      return res;
    }
    // else {
    //   console.log('Invalid result for', key);
    //   console.log(res);
    // }
  }
  return def;
}

/*
function makeGetItem<T>(
  chk: typecheck<T>,
): (key: string, initialValue: T) => PromiseLike<T> {
  return async (key: string, initialValue: T): Promise<T> => {
    const fetched = await fetch("/api/" + key);
    let maybeValue: T | undefined;
    if (fetched.ok) {
      const res = await fetched.json();
      if (chk(res)) {
        maybeValue = res;
      }
    }
    return isDefined(maybeValue) ? maybeValue : initialValue;
  };
}

export function getMainReadOnlyStorage<T>(chk: typecheck<T>): AsyncStorage<T> {
  return {
    getItem: makeGetItem(chk),
    setItem: Promise.resolve,
    removeItem: Promise.resolve,
  };
}

function makeGetTranslatedItem<T, U>(
  chk: typecheck<U>,
  xlate: (val: U) => T,
): (key: string, initialValue: T) => PromiseLike<T> {
  return async (key: string, initialValue: T): Promise<T> => {
    const value = await ReadFromStorage(key, chk);
    return isDefined(value) ? xlate(value) : initialValue;
  };
}

async function setItem<T>(key: string, data: T): Promise<void> {
  return WriteToStorage(key, data);
}

async function setTranslatedItem<T, U>(
  key: string,
  newValue: T,
  xlate: (val: T) => U,
): Promise<void> {
  return WriteToStorage(key, xlate(newValue));
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function noSetItem<T>(_key: string, _newValue: T): Promise<void> {
  return Promise.resolve();
}

async function removeItem(key: string): Promise<void> {
  await DeleteFromStorage(key);
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function noRemoveItem(_key: string): Promise<void> {
  return Promise.resolve();
}

type Unsub = () => void;
type Subscriber<T> = (
  key: string, // Jotai key, which I use as the stringified SocketMsg key
  callback: (value: T) => void,
  initVal: T,
) => Unsub;


export function getMainStorage<T>(chk: typecheck<T>): AsyncStorage<T> {
  return {
    getItem: makeGetItem(chk),
    setItem,
    removeItem,
  };
}

export function atomFromMain<T>(
  key: string,
  init: T,
  chk: typecheck<T>,
): WritableAtomType<T> {
  return atomWithStorage(key, init, getMainReadOnlyStorage(chk));
}

function getTranslatedMainStorage<T, U>(
  def: T,
  chk: typecheck<U>,
  fromMain: (val: U) => T,
  toMain: (val: T) => U,
): AsyncStorage<T> {
  return {
    getItem: makeGetTranslatedItem(chk, fromMain),
    setItem: async (k, v) => setTranslatedItem(k, toMain(v), fromMain),
    removeItem,
  };
}

function getTranslatedMainReadOnlyStorage<T, U>(
  def: T,
  chk: typecheck<U>,
  fromMain: (val: U) => T,
): AsyncStorage<T> {
  return {
    getItem: makeGetTranslatedItem(chk, fromMain),
    setItem: noSetItem,
    removeItem: noRemoveItem,
  };
}

export function atomWithTranslatedStorageInMain<T, U>(
  key: string,
  init: T,
  chk: typecheck<U>,
  toMain: (val: T) => U,
  fromMain: (val: U) => T,
): WritableAtomType<T> {
  return atomWithStorage(
    key,
    init,
    getTranslatedMainStorage<T, U>(init, chk, fromMain, toMain),
  );
}

export function atomFromTranslatedStorageFromMain<T, U>(
  key: string,
  init: T,
  chk: typecheck<U>,
  fromMain: (val: U) => T,
): WritableAtomType<T> {
  return atomWithStorage(
    key,
    init,
    getTranslatedMainReadOnlyStorage<T, U>(init, chk, fromMain),
  );
}
*/
