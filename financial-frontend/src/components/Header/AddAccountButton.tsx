import { useState } from "react";
import { PlaidFrontend } from "./PlaidFrontend";
import {Plus} from "lucide-react";
import "../../css/Header.css"
export function AddPlaidButton(){
    const[open,setOpen] = useState(false);
    const toggleView = () => setOpen((prev) => !prev);
    const closeMenu = () => setOpen(false);
    return<>
        <div className="Account-btn-div" onClick={toggleView}>
            <button className="account-btn"><Plus className="icon"/> Add Account </button>
        </div>
        
        {open?<PlaidFrontend onClose={closeMenu}/>:<></>}



    </>;
}