import { chkFieldOf, isNumber, isString } from '@freik/typechk';
import { describe, expect, test } from 'bun:test';
import { fetchApi, getStore } from '../state/Storage';

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
    const res = await fetchApi('test', chkFieldOf('a', isString));
    expect(res).toEqual({ a: 'b' });
    const res2 = await fetchApi('test', chkFieldOf('a', isNumber));
    expect(res2).toBeUndefined();
  });
});
