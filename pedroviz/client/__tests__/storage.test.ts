import { describe, expect, test } from 'bun:test';

import { chkFieldOf, isDefined, isNumber, isString } from '@freik/typechk';

import { fetchApi, getStore, putApi } from '../state/Storage';

const put_value: Record<string, unknown> = {
  body: '{"a":"b"}',
  headers: {
    'Content-Type': 'application/json',
  },
  method: 'PUT',
};

async function MyFetchFunc(
  key: string | URL | Request,
  init?: RequestInit,
): Promise<Response> {
  switch (key) {
    case '/api/test':
      const body = JSON.stringify({ a: 'b' });
      return new Response(body, {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      });
    case '/api/test2':
      const body2 = 'a,b,[123]//asdf';
      try {
        return new Response(body2, {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      } catch {}
      break;
    case '/api/error':
      const body3 = JSON.stringify({ error: 'This is an error' });
      return new Response(body3, {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      });
    case '/api/put':
      expect(init).toEqual(put_value);
      return new Response('OK', { status: 200 });
  }
  return new Response('ERROR', { status: 404 });
}
MyFetchFunc.preconnect = () => {};

// I never really followed through on math enough to do more than cover
// the bare essentials of this thing, but here's a test:
describe('Storage validation', () => {
  test('Store creation', () => {
    const store = getStore();
    expect(store).toBeDefined();
  });
  test('Simple fetch of mocked value', async () => {
    globalThis.fetch = MyFetchFunc;
    const res = await fetchApi('test', chkFieldOf('a', isString), { a: 'foo' });
    expect(res).toEqual({ a: 'b' });
    const res2 = await fetchApi('test', chkFieldOf('a', isNumber), {
      a: 'foo',
    });
    expect(res2).toEqual({ a: 'foo' });
    const res3 = await fetchApi('test2', isDefined, { a: 'foo' });
    expect(res3).toEqual({ a: 'foo' });
    const res4 = await fetchApi('error', isString, 'stuff');
    expect(res4).toEqual('stuff');
    const val = { a: 'b' };
    put_value['body'] = JSON.stringify(val);
    const res5 = await putApi('put', val);
    expect(res5).toBeUndefined();
  });
});
