// [FILEPATH] vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'url'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {                      
    proxy: {                     
      '/api': 'http://localhost:8080'
    }
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
      "@components": fileURLToPath(
        new URL("./src/components", import.meta.url)
      ),
      // 아래 라인을 추가하세요.
      "@common": fileURLToPath(new URL("./src/common", import.meta.url))
    },
  },
})