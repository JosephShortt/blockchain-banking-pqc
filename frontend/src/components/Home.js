import { useState } from "react";
import { useUser } from "../contexts/UserContext";
import api from "../api";
function Home() {

  const { userData, accountData } = useUser();
  const [amount, setAmount] = useState(0);
  const [iban, setIban] = useState("");


  if (!userData) return <p>Please log in.</p>;


  async function handleSendFunds(e) {
    e.preventDefault()

    if (!iban || iban.trim() === "") {
      alert("Please enter an IBAN before making a transaction.");
      return;
    }

    if (amount <= 0) {
      alert("Please enter a valid amount greater than zero.");
      return;
    }


    try {
      const response = await api.post('/api/accounts/transaction',
        {
          account: accountData,
          amount,
          iban
        }

      )

      alert("Transaction Complete")

      window.location.reload();

      console.log("Transaction successful:", response.data);
      localStorage.setItem("accountData", JSON.stringify(response.data));

    } catch (error) {
      console.error("Error sending funds:", error);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <div>
        <div style={{border: '1px solid #ccc', borderRadius: '8px', padding: '20px', marginTop:'10px',marginBottom: '20px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'}}>
          <h2>Welcome, {userData.firstName}!</h2>
          <p>Email: {userData.email}</p>
          <p>IBAN: {accountData.iban}</p>
          <p>Account ID: {accountData.accountId}</p>

          <p>Balance: {new Intl.NumberFormat('en-IE', {
            style: 'currency',
            currency: 'EUR'
          }).format(accountData.balance)}</p>
        </div>


        <input type="text" value={iban} onChange={(e) => setIban(e.target.value)} placeholder="Enter Iban of account to send to" />

        <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Enter amount" />
        <button type="button"
          onClick={handleSendFunds} style={{ padding: '5px 10px' }}>Send</button>
      </div>

    </div>
  );
};

export default Home;
