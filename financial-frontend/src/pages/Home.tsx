import { Header } from "../components/Header/Header";
import {Graph} from "../components/Graph"
import { Divider } from "../components/Divider";
import { BankTiles } from "../components/BankTile/BankTiles";
import { PlaidUpdate } from "../components/BankTile/PlaidUpdate";
//import {PlaidUpdate} from "../components/PlaidUpdate"
export function Home(){




    return <>
        <PlaidUpdate/>
        <Header/>
        <Divider/>
        <Graph/>
        <BankTiles/>
        
    </>;
}