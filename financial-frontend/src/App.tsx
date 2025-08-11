import { Routes, Route } from "react-router-dom";
import {Home} from "./pages/Home";
import { darkMode } from "./utils/DarkMode";
import { useAuthBootstrap } from "./hooks/UseAuthBootstrap";
export default function App() {
    if (darkMode.value) {
      document.body.classList.add('dark');
    }
    useAuthBootstrap();
    return (
      <Routes>
        <Route path="/" element = {<Home />}/>
      </Routes>
    )

}

