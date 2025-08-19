import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: ['unifyfinance.ca', 'www.unifyfinance.ca'],
    host:true,
    proxy: {
      '/api': {
        target: 'https://api.unifyfinance.ca',
        changeOrigin: true,
        secure: false,
        xfwd:true,
        cookieDomainRewrite: '',
        cookiePathRewrite: '/',
      }
    },
  },
});