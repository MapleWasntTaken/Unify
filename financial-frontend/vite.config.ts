import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: ['unifyfinance.ca', 'www.unifyfinance.ca'],
    https:true,
    host:true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        secure: false,
        xfwd:true,
        cookieDomainRewrite: '',
        cookiePathRewrite: '/',
      }
    },
  },
});