
import { createRoot } from 'react-dom/client'
import 'bootstrap/dist/css/bootstrap.css'
import { BrowserRouter } from "react-router-dom";
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  
  <BrowserRouter>
    <App />
  </BrowserRouter>
  
)