import "../../css/Header.css";
import { ProfileMenu } from "./ProfileMenu";
import { useDarkMode } from "../../hooks/UseDarkMode";
import { useAuthState } from "../../hooks/UseAuthState";
import { LoginButton } from "./signup-signin/LoginButton";
import { AddPlaidButton } from "./AddAccountButton";
import  WhiteLogoUrl from "../../assets/Logo-white.svg"
import BlackLogoUrl from "../../assets/Logo-black.svg"
export function Header() {
  const [isDark] = useDarkMode();
  const [isLoggedIn] = useAuthState();

  return (
    <div className={isDark ? "header-dark" : "header-light"}>
      {isLoggedIn?
      <>
      <div className="header-txt">
        {isDark? <><img src={WhiteLogoUrl} className="logo-img"/><h1 className="header-txt-top">Unify</h1></>:<><img src={BlackLogoUrl} className="logo-img"/><h1 className="header-txt-top">Unify</h1></>}
      </div>
      <div className="buttons">
        <AddPlaidButton/>
        <ProfileMenu/>
      </div>
      </>:
      <>
      <div className="header-txt">
        {isDark? <><img src={WhiteLogoUrl} className="logo-img"/><h1 className="header-txt-top">Unify</h1></>:<><img src={BlackLogoUrl} className="logo-img"/><h1 className="header-txt-top">Unify</h1></>}
      </div><LoginButton/></>}
      
      

      
    </div>
  );
}
