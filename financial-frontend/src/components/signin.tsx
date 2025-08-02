import { useEffect, useState } from "react";
import { GetCsrf } from "../utils/GetCSRF";

export function SignIn() {
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);

  useEffect(() => {
    GetCsrf().then(setCsrf);
  }, []);

  while (!csrf) return <p>Loading CSRF...</p>;

return (
    <>
      <form
        onSubmit={async (e) => {
          e.preventDefault();
          const formdata = new FormData(e.currentTarget);
          const x = await fetch("/api/login", {
            credentials: "include",
            method: "POST",
            body: new URLSearchParams(
                [...formdata.entries()] as [string, string][]
            ),
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
              ...csrf,
            },
          });
          console.log(x);

            if(x.ok){
                location.href = "/home";
            }
            else{
                console.log("not ok");
            }
        }}
      >
        <label htmlFor="username">Email:</label>
        <input type="text" id="username" name="username" required />
        <br />
        <br />

        <label htmlFor="password">Password:</label>
        <input type="password" id="password" name="password" required />
        <br />
        <br />

        <input type="checkbox" name="remember-me" /> Stay Signed In?
        <button type="submit">Login</button>
      </form>
      <p>Don't have an account?</p><button onClick={()=>{
        location.href = "/SignUp";
      }}><em>Sign up</em></button>
    </>
  );
}
