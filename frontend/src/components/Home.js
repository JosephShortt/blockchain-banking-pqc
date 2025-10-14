import { useState } from "react";
import axios from "axios";
import { useEffect } from "react";
import { useUser } from "../contexts/UserContext";

function Home() {

  const { userData, accountData } = useUser();

   if (!userData) return <p>Please log in.</p>;

return (
    <div>
      <h2>Welcome, {userData.firstName}!</h2>
      <p>Email: {userData.email}</p>
      <p>Account ID: {accountData.accountId}</p>
      <p>Balance: {accountData.balance}</p>
    </div>
  );
};

export default Home;
