import { useEffect, useState, useRef } from "react";
import { User, Sun, Moon, LogOut, Settings } from "lucide-react";
import "../../css/ProfileMenu.css";
import { useDarkMode } from "../../hooks/UseDarkMode";
import { BankData } from "../../types/BankData";
import { authState } from "../../utils/authState";
import { GetCsrf, WipeCSRF } from "../../utils/GetCSRF";
import SettingsScreen from "./SettingsScreen";

export function ProfileMenu() {
  const [open, setOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);
  const menuRef = useRef<HTMLDivElement | null>(null);
  const [isDark, toggleDarkMode] = useDarkMode();
  const toggleMenu = () => setOpen((prev) => !prev);
  const closeMenu = () => setOpen(false);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const toggleSettings = () => setSettingsOpen((prev) => !prev);
  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth < 768);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        closeMenu();
      }
    };
    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [open]);

  const handleLogout = async () => {
    const csrf = await GetCsrf();
    BankData.clear();
    await fetch("/api/logout", {
      method: "POST",
      credentials: "include",
      headers: { ...csrf },
    });
    WipeCSRF();
    authState.set(false);
  };

  return (
    <div className="profile-container" ref={menuRef}>
      <div className="profile-icon-wrapper" onClick={toggleMenu}>
        <User className="profile-icon" />
      </div>
      
      {!isMobile && open && (
        <div className="dropdown-menu">
          <button onClick={toggleDarkMode} className="menu-item">
            {isDark ? <Sun size={18} /> : <Moon size={18} />}
            <span>{isDark ? "Light Mode" : "Dark Mode"}</span>
          </button>
          <button onClick={()=>{toggleSettings()}} className="menu-item">
            <Settings size={18} />
            <span>Settings</span>
          </button>
          <button onClick={handleLogout} className="menu-item">
            <LogOut size={18} />
            <span>Log Out</span>
          </button>
        </div>
      )}

      {isMobile && open && (
        <div className="drawer-overlay" onClick={closeMenu}>
          <div className="drawer-menu" onClick={(e) => e.stopPropagation()}>
            <div className="drawer-header" onClick={closeMenu}>
              <User className="profile-icon" />
              <span className="drawer-title">Account</span>
            </div>

            <div className="drawer-section">
              <button onClick={toggleDarkMode} className="menu-item">
                {isDark ? <Sun size={18} /> : <Moon size={18} />}
                <span>{isDark ? "Light Mode" : "Dark Mode"}</span>
              </button>
              <button onClick={()=>{toggleSettings()}} className="menu-item">
                <Settings size={18} />
                <span>Settings</span>
              </button>
              <button onClick={handleLogout} className="menu-item">
                <LogOut size={18} />
                <span>Log Out</span>
              </button>
            </div>

              
            
          </div>
        </div>
      )}
      {settingsOpen? <SettingsScreen isOpen={settingsOpen} onClose={() => setSettingsOpen(false)}/>:<></>}
    </div>
  );
}
