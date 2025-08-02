import { SignIn } from "./components/signin"
import { Routes, Route } from "react-router-dom";
import { SignUp } from "./components/signup";
import { BankTiles } from "./components/BankTile/BankTiles";
export default function App() {

    return (
      <Routes>
        <Route path="/signin" element={<SignIn />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/home" element={<BankTiles />}/>
      </Routes>
    )

}

