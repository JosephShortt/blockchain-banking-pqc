import { useState } from "react";
import axios from "axios";
import { useEffect } from "react";
import { useUser } from "../contexts/UserContext";

function Home() {

  const { userData, accountData, setAccountData } = useUser();
  const [amount, setAmount] = useState(0);
  const [iban, setIban] = useState("");


  if (!userData) return <p>Please log in.</p>;


  async function handleAddFundsInput(e) {
    e.preventDefault()
    try {

      const response = await axios.post('http://localhost:8080/api/accounts/transaction',
        {
          account: accountData,
          amount
        }

      )
      console.log("Funds Added Successfully through inout :", response.data);
      localStorage.setItem("accountData", JSON.stringify(response.data));


    } catch (error) {
      console.error("Error adding funds:", error);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <div>
        <h2>Welcome, {userData.firstName}!</h2>
        <p>Email: {userData.email}</p>
        <p>IBAN: {accountData.iban}</p>
        <p>Account ID: {accountData.accountId}</p>
        <p>Balance: {accountData.balance}</p>

        <input type="text" value={iban} onChange={(e) => setIban(e.target.value)} placeholder="Enter Iban of account to send to" />

        <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Enter amount" />
        <button type="button"
          onClick={handleAddFundsInput} style={{ padding: '5px 10px' }}>Add</button>
      </div>

    </div>
  );
};

export default Home;
