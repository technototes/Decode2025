import { serve } from 'bun';

import index from './index.html';
import { main } from './server/main';
import { SavePath } from './server/savepath';
import {
  LoadClassList,
  LoadDatabase,
  LoadPath,
  SaveDatabase,
} from './server/web-interface';

const server = serve({
  routes: {
    // Serve index.html for all unmatched routes.
    '/*': index,
    // We could just do "/foo.jpg": Bun.file("file.jpg") but this way keeps them in memory
    // which seems good for the canvas backgrounds...
    '/assets/field-light.jpg': new Response(
      await Bun.file('./field-light.jpg').bytes(),
    ),
    '/assets/field-dark.jpg': new Response(
      await Bun.file('./field-dark.jpg').bytes(),
    ),
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
    '/api/db': async (req) => LoadDatabase(),
    '/api/putdb': {
      PUT: async (req) => SaveDatabase(JSON.stringify(await req.json())),
    },
  },

  development: process.env.NODE_ENV !== 'production' && {
    // Enable browser hot reloading in development
    hmr: true,
    // Echo console logs from the browser to the server
    console: true,
  },
});

main(server.url).then(console.log).catch(console.error);
