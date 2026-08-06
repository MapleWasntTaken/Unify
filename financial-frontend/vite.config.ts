import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: ['localhost:8080',"localhost:5173"],
    host:true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: true,
        xfwd:true,
        cookieDomainRewrite: '',
        cookiePathRewrite: '/',
      }
    },
  },
});