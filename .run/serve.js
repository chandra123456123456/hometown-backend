const http = require('http');
const fs = require('fs');
const path = require('path');

const ROOT = 'E:/HomeTown/frontend/dist/frontend/browser';
const PORT = 4200;
const API = { host: 'localhost', port: 8080 };

const mime = {
  '.html': 'text/html', '.js': 'text/javascript', '.css': 'text/css',
  '.json': 'application/json', '.ico': 'image/x-icon', '.svg': 'image/svg+xml',
  '.woff2': 'font/woff2', '.woff': 'font/woff', '.ttf': 'font/ttf',
  '.png': 'image/png', '.jpg': 'image/jpeg', '.webp': 'image/webp',
};

const server = http.createServer((req, res) => {
  if (req.url.startsWith('/api')) {
    const opts = { host: API.host, port: API.port, path: req.url, method: req.method, headers: req.headers };
    const proxy = http.request(opts, pr => { res.writeHead(pr.statusCode, pr.headers); pr.pipe(res); });
    proxy.on('error', () => { res.writeHead(502); res.end('backend not reachable'); });
    req.pipe(proxy);
    return;
  }
  const urlPath = decodeURIComponent(req.url.split('?')[0]);
  let filePath = urlPath === '/' ? path.join(ROOT, 'index.html') : path.join(ROOT, urlPath);
  fs.readFile(filePath, (err, data) => {
    if (err) {
      fs.readFile(path.join(ROOT, 'index.html'), (e2, idx) => {
        if (e2) { res.writeHead(404); res.end('not found'); }
        else { res.writeHead(200, { 'Content-Type': 'text/html' }); res.end(idx); }
      });
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { 'Content-Type': mime[ext] || 'application/octet-stream' });
    res.end(data);
  });
});

server.listen(PORT, () => console.log('HomeTown frontend on http://localhost:' + PORT));
