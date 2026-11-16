// Minimal, dependency-free demo shop used as the application under test.
// Synthetic data only. Not production code.
const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');

const PORT = Number(process.env.APP_PORT || 3000);
const PUBLIC = path.join(__dirname, 'public');

const USERS = {
  standard_user: { password: 'demo_pass', locked: false },
  locked_user: { password: 'demo_pass', locked: true },
};
const PRODUCTS = [
  { id: 1, name: 'Wireless Mouse', price: 24.99, stock: 50 },
  { id: 2, name: 'Mechanical Keyboard', price: 89.5, stock: 20 },
  { id: 3, name: 'USB-C Hub', price: 39.0, stock: 0 },
  { id: 4, name: 'Laptop Stand', price: 45.25, stock: 12 },
];
const sessions = new Map(); // token -> username
let orders = [];
let nextOrderId = 1000;

function send(res, status, body, headers = {}) {
  const isJson = typeof body !== 'string';
  res.writeHead(status, { 'Content-Type': isJson ? 'application/json' : 'text/html; charset=utf-8', ...headers });
  res.end(isJson ? JSON.stringify(body) : body);
}
function readBody(req) {
  return new Promise((resolve) => {
    let data = '';
    req.on('data', (c) => (data += c));
    req.on('end', () => {
      try { resolve(data ? JSON.parse(data) : {}); } catch { resolve(null); }
    });
  });
}
function auth(req) {
  const h = req.headers.authorization || '';
  const token = h.startsWith('Bearer ') ? h.slice(7) : null;
  return token && sessions.has(token) ? sessions.get(token) : null;
}

const server = http.createServer(async (req, res) => {
  const url = new URL(req.url, `http://localhost:${PORT}`);
  const p = url.pathname;

  // ---- API ----
  if (p === '/api/health') return send(res, 200, { status: 'ok' });
  if (p === '/api/test/reset' && req.method === 'POST') {
    orders = []; nextOrderId = 1000; sessions.clear();
    return send(res, 204, '');
  }
  if (p === '/api/login' && req.method === 'POST') {
    const body = await readBody(req);
    if (!body || !body.username || !body.password) return send(res, 400, { error: 'username and password are required' });
    const u = USERS[body.username];
    if (!u || u.password !== body.password) return send(res, 401, { error: 'Invalid username or password' });
    if (u.locked) return send(res, 403, { error: 'User is locked out' });
    const token = crypto.randomBytes(16).toString('hex');
    sessions.set(token, body.username);
    return send(res, 200, { token, username: body.username });
  }
  if (p === '/api/products' && req.method === 'GET') return send(res, 200, PRODUCTS);
  const pm = p.match(/^\/api\/products\/(\d+)$/);
  if (pm && req.method === 'GET') {
    const prod = PRODUCTS.find((x) => x.id === Number(pm[1]));
    return prod ? send(res, 200, prod) : send(res, 404, { error: 'Product not found' });
  }
  if (p === '/api/orders') {
    const user = auth(req);
    if (!user) return send(res, 401, { error: 'Missing or invalid token' });
    if (req.method === 'GET') return send(res, 200, orders.filter((o) => o.username === user));
    if (req.method === 'POST') {
      const body = await readBody(req);
      if (!body || !Array.isArray(body.items) || body.items.length === 0) return send(res, 400, { error: 'items must be a non-empty array' });
      let total = 0;
      for (const it of body.items) {
        const prod = PRODUCTS.find((x) => x.id === it.productId);
        if (!prod) return send(res, 422, { error: `Unknown product ${it.productId}` });
        if (!Number.isInteger(it.quantity) || it.quantity < 1) return send(res, 422, { error: 'quantity must be a positive integer' });
        if (prod.stock < it.quantity) return send(res, 409, { error: `Insufficient stock for ${prod.name}` });
        total += prod.price * it.quantity;
      }
      const order = { id: nextOrderId++, username: user, items: body.items, total: Math.round(total * 100) / 100, status: 'CREATED', createdAt: new Date().toISOString() };
      orders.push(order);
      return send(res, 201, order);
    }
  }
  if (p.startsWith('/api/')) return send(res, 404, { error: 'Not found' });

  // ---- static UI ----
  const file = p === '/' ? 'login.html' : p.replace(/^\//, '');
  const full = path.join(PUBLIC, path.normalize(file));
  if (!full.startsWith(PUBLIC) || !fs.existsSync(full)) return send(res, 404, '<h1>Not found</h1>');
  const type = full.endsWith('.js') ? 'application/javascript' : 'text/html; charset=utf-8';
  res.writeHead(200, { 'Content-Type': type });
  fs.createReadStream(full).pipe(res);
});

server.listen(PORT, () => console.log(`Demo shop running on http://localhost:${PORT}`));
