import { useState } from "react";
import { PlaidFrontend } from "../Header/PlaidFrontend";
import {Plus} from "lucide-react";
import "../../css/BankTiles.css"
export function AddPlaidButton(){
    const[open,setOpen] = useState(false);
    const toggleView = () => setOpen((prev) => !prev);
    const closeMenu = () => setOpen(false);
    return<>
        <div
              onClick={toggleView}
              className="add-plaid-button"
            >
              <Plus/> Add Account Your First Account
        </div>
        
        {open?<PlaidFrontend onClose={closeMenu}/>:<></>}



    </>;
}