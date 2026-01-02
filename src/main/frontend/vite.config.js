import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import path from 'node:path'
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
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8000',
        }
    },
    build: {
        outDir: path.resolve(__dirname, '../resources/static'),
        emptyOutDir: true,
    }
})
