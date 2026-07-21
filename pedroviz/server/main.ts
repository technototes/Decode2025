import { sleep } from 'bun';

import { PopulateDatabase } from './full-database';
import { OpenBrowser } from './open-browser';

// Scan the files
export async function main(url: URL) {
  console.log('Parsing code: Please wait...');
  await PopulateDatabase();
  console.log(`🚀 Server running at ${url}`);
  // Delay to let some stuff get moving. This is annoying, but
  // necessary, AFAICT.
  await sleep(1500);
  OpenBrowser(url.toString());
}
