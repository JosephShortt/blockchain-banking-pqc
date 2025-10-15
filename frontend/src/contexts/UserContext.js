// UserContext.jsx
import { createContext, useContext, useState } from "react";

const UserContext = createContext();

export function UserProvider({ children }) {
   const [userData, setUserData] = useState(() => {
    return JSON.parse(localStorage.getItem("userData")) || null;
  });

  const [accountData, setAccountData] = useState(() => {
    return JSON.parse(localStorage.getItem("accountData")) || null;
  });

  return (
    <UserContext.Provider value={{ userData, setUserData, accountData, setAccountData }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  return useContext(UserContext);
}
