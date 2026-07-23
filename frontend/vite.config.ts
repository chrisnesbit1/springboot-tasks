import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // Proxies API calls to the Spring Boot backend so the browser sees
    // same-origin requests in dev, matching the same-origin setup used
    // in production. This avoids needing any CORS configuration.
    proxy: {
      '/tasks': 'http://localhost:8080',
    },
  },
})
