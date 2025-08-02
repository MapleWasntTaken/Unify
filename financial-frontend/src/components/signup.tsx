import { useEffect, useState } from "react";
import { GetCsrf } from "../utils/GetCSRF";

export function SignUp() {
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);
  
    useEffect(() => {
      GetCsrf().then(setCsrf);
    }, []);
  
    while (!csrf) return <p>Loading CSRF...</p>;



  if (!csrf) return <p>Loading...</p>;

  return (
    <form
      className="formclass"
      onSubmit={async (e) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);
        const payload = {
          email: formData.get("username"),
          password: formData.get("password"),
        };
        if (formData.get("password") === formData.get("confpassword")) {
        const x = await fetch("/api/signup", {
            method: "POST",
            body: JSON.stringify(payload),
            headers: {
            "Content-Type": "application/json",
            ...csrf,
            },
            credentials: "include",
        });

        if (x.status === 409) {
            console.log("Email already exists.");
        }
        } else {
        console.log("Passwords do not match.");
        }  



        
      }}
    >
      <label htmlFor="username">Email:</label>
      <input type="text" id="username" name="username" required />
      <br />
      <br />

      <label htmlFor="password">Password:</label>
      <input type="password" id="password" name="password" required />
      <br/>
      <br/>
      <label htmlFor="confpassword">Confirm Password:</label>
      <input type="password" id="confpassword" name="confpassword" required />
      <br />
      <br />

      <button type="submit">Sign Up</button><p>Have an account?<button onClick={()=>{
        location.href = "/signin";
      }}><em>Sign in</em></button></p>
      
    </form>
  );
}
