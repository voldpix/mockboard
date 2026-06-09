import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';
import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

const __dirname = dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [svelte()],
  resolve: {
    alias: {
      $lib: resolve(__dirname, './src/lib')
    }
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8000',
      '/m': 'http://localhost:8000'
    }
  },
  build: {
    outDir: resolve(__dirname, '../main/resources/static'),
    emptyOutDir: true
  }
});
