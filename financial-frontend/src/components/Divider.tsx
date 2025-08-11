import {useDarkMode} from "../hooks/UseDarkMode"
import "../css/Divider.css"

export function Divider(){
    const [isDark] = useDarkMode();
    return isDark?<div className="divider-dark"></div>:<div className="divider-light"></div>
}