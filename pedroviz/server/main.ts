import { PopulateDatabase } from './full-database';
import { OpenBrowser } from './open-browser';

// Scan the files
export async function main(url: URL) {
  console.log('Parsing code: Please wait...');
  await PopulateDatabase();
  console.log(`🚀 Server running at ${url}`);
  OpenBrowser(url.toString());
}
