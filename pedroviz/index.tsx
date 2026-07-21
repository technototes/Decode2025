import { serve } from 'bun';

import index from './index.html';
import { main } from './server/main';
import { SavePath } from './server/savepath';
import {
  GetPathFileNames,
  LoadClassList,
  LoadDatabase,
  LoadPath,
} from './server/web-interface';

const server = serve({
  routes: {
    // Serve index.html for all unmatched routes.
    '/*': index,
    // Get the different robot path files
    '/api/getpaths': async (req) => GetPathFileNames(),
    '/api/loadpath/:team/:path': async (req) =>
      LoadPath(
        decodeURIComponent(req.params.team),
        decodeURIComponent(req.params.path),
      ),
    '/api/getclasslist/:team/:path': async (req) =>
      LoadClassList(
        decodeURIComponent(req.params.team),
        decodeURIComponent(req.params.path),
      ),
    '/api/savepath/:team/:path/:data': async (req) =>
      SavePath(
        decodeURIComponent(req.params.team),
        decodeURIComponent(req.params.path),
        decodeURIComponent(req.params.data),
      ),
    '/apii/fulldb': async (req) => LoadDatabase(),
  },

  development: process.env.NODE_ENV !== 'production' && {
    // Enable browser hot reloading in development
    hmr: true,
    // Echo console logs from the browser to the server
    console: true,
  },
});

main(server.url).then(console.log).catch(console.error);
