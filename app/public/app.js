// Shared browser helpers for the demo shop.
const api = async (path, opts = {}) => {
  const token = sessionStorage.getItem('token');
  const res = await fetch(path, {
    ...opts,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
  });
  const text = await res.text();
  return { status: res.status, body: text ? JSON.parse(text) : null };
};
const cart = {
  get: () => JSON.parse(sessionStorage.getItem('cart') || '[]'),
  set: (c) => sessionStorage.setItem('cart', JSON.stringify(c)),
};
