/**import { useEffect, useState } from "react";
import { useBankData } from "../hooks/UseBankData";
import { GetCsrf } from "../utils/GetCSRF";
export function PlaidUpdate(){
    const [csrf,setCsrf] = useState<Record<string, string> | null>(null);
    const [accounts]  = useBankData();
    const [update,setUpdate] = useState(false);
    const [tokens] = useState(new Array<string>);
    useEffect(() => {
        GetCsrf().then(setCsrf);
    }, []);
    useEffect(()=>{
        if(!csrf)return;
        accounts.forEach(element => {
            if(element.status){
                setUpdate(true);
                const fun = async ()=>{
                    fetch("/api/plaid/create-link-token",{
                        method: "POST",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded",
                            ...csrf,
                        },
                        body: new URLSearchParams({
                            accountId: element.accountId,
                        }).toString(),
                        
                    })
                }
                fun();

            }
        });
    },[accounts])

    return <>Cum fart</>;
}**/