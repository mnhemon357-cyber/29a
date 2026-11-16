const CACHE_NAME = '29a-soc-webapk-v2';
const ASSETS = [
  '/29a/',
  '/29a/index.html',
  '/29a/manifest.json',
  '/29a/assets/developer_photo.jpg',
  '/29a/assets/cyber_guardian_android_ca.crt',
  '/29a/assets/cyber_guardian_android_ca.pem'
];
self.addEventListener('install', e => {
  e.waitUntil(caches.open(CACHE_NAME).then(c => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});
self.addEventListener('activate', e => {
  e.waitUntil(self.clients.claim());
});
self.addEventListener('fetch', e => {
  e.respondWith(caches.match(e.request).then(r => r || fetch(e.request).catch(() => caches.match('/29a/index.html'))));
});
