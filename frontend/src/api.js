const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function parseResponse(response) {
  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = data?.message || data?.error || 'Request failed';
    const error = new Error(message);
    error.status = response.status;
    error.fieldErrors = data?.fieldErrors || {};
    throw error;
  }

  return data;
}

async function request(path, options = {}) {
  const token = localStorage.getItem('inventory_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers
  });
  return parseResponse(response);
}

export function login(payload) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function register(payload) {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getInventoryItems() {
  return request('/api/inventory-items');
}

export function createInventoryItem(payload) {
  return request('/api/inventory-items', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateInventoryItem(id, payload) {
  return request(`/api/inventory-items/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteInventoryItem(id) {
  return request(`/api/inventory-items/${id}`, {
    method: 'DELETE'
  });
}
