let csrfToken: string | null = null;
let csrfHeader: string | null = null;
export async function loadCsrfToken(): Promise<void>{
    
    const res = await fetch("/api/csrf-token",{credentials:"include"})
    const data = await res.json();
    csrfToken = data.token;
    csrfHeader = data.headerName;
    
}

export async function GetCsrf(): Promise<Record<string,string>>{
    await loadCsrfToken();

    return{
        [csrfHeader!]: csrfToken!
    };
}