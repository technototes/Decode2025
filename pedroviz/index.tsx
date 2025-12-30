import { serve } from 'bun';
import { GetPathFileNames } from './server/getpaths';
import { LoadPath } from './server/loadpath';
import { SavePath } from './server/savepath';

import index from './index.html';

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
    '/api/savepath/:team/:path/:data': async (req) =>
      SavePath(
        decodeURIComponent(req.params.team),
        decodeURIComponent(req.params.path),
        decodeURIComponent(req.params.data),
      ),
  },

  development: process.env.NODE_ENV !== 'production' && {
    // Enable browser hot reloading in development
    hmr: true,
    // Echo console logs from the browser to the server
    console: true,
  },
});

console.log(`🚀 Server running at ${server.url}`);
