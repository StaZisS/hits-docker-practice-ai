import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: Number(__ENV.VUS || 20),
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

function postJson(path, body) {
  return http.post(`${BASE_URL}${path}`, JSON.stringify(body), {
    headers: { 'Content-Type': 'application/json' },
    timeout: '10s',
  });
}

export function setup() {
  const laptopIds = [];
  const monitorIds = [];
  const hddIds = [];
  const pcIds = [];

  for (let i = 0; i < 20; i++) {
    const r = postJson('/api/laptops/add', {
      seriesNumber: `L-${i}`,
      producer: 'k6',
      price: 1000.0,
      numberOfProductsInStock: 10,
      size: '13 inches',
    });
    check(r, { 'seed laptop: 200': (res) => res.status === 200 });
    if (r.status === 200) laptopIds.push(r.json('id'));
  }

  for (let i = 0; i < 20; i++) {
    const r = postJson('/api/monitors/add', {
      seriesNumber: `M-${i}`,
      producer: 'k6',
      price: 250.0,
      numberOfProductsInStock: 10,
      diagonal: 24.0,
    });
    check(r, { 'seed monitor: 200': (res) => res.status === 200 });
    if (r.status === 200) monitorIds.push(r.json('id'));
  }

  for (let i = 0; i < 20; i++) {
    const r = postJson('/api/hdds/add', {
      seriesNumber: `H-${i}`,
      producer: 'k6',
      price: 80.0,
      numberOfProductsInStock: 10,
      capacity: 512.0,
    });
    check(r, { 'seed hdd: 200': (res) => res.status === 200 });
    if (r.status === 200) hddIds.push(r.json('id'));
  }

  for (let i = 0; i < 20; i++) {
    const r = postJson('/api/pcs/add', {
      seriesNumber: `PC-${i}`,
      producer: 'k6',
      price: 700.0,
      numberOfProductsInStock: 10,
      formFactor: 'Desktop',
    });
    check(r, { 'seed pc: 200': (res) => res.status === 200 });
    if (r.status === 200) pcIds.push(r.json('id'));
  }

  return { laptopIds, monitorIds, hddIds, pcIds };
}

export default function (data) {
  const dice = Math.random();

  if (dice < 0.55) {
    const r = http.get(`${BASE_URL}/api/laptops`);
    check(r, { 'list laptops: 200': (res) => res.status === 200 });
  } else if (dice < 0.7) {
    const r = http.get(`${BASE_URL}/api/monitors`);
    check(r, { 'list monitors: 200': (res) => res.status === 200 });
  } else if (dice < 0.82) {
    const r = http.get(`${BASE_URL}/api/hdds`);
    check(r, { 'list hdds: 200': (res) => res.status === 200 });
  } else if (dice < 0.92) {
    const r = http.get(`${BASE_URL}/api/pcs`);
    check(r, { 'list pcs: 200': (res) => res.status === 200 });
  } else {
    const ids = data.laptopIds;
    const id = ids[Math.floor(Math.random() * ids.length)];
    const r = http.get(`${BASE_URL}/api/laptops/${id}`);
    check(r, { 'get laptop: 200': (res) => res.status === 200 });
  }

  sleep(0.2);
}
