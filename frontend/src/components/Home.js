import { useState } from "react";
import { useUser } from "../contexts/UserContext";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Home() {
  const navigate = useNavigate();
  const { userData, accountData, selectedBank } = useUser();
  const [amount, setAmount] = useState(0);
  const [iban, setIban] = useState("");
  const [password, setPassword] = useState("");
  const [transactions, setTransactions] = useState([]); // store transactions




  // useEffect runs once when the component mounts
  useEffect(() => {
    async function fetchTransactions() {
      try {
        const response = await axios.get(`${selectedBank.apiUrl}/api/accounts/transactions/${accountData.iban}`);
        setTransactions(response.data); // save transactions to state
      } catch (error) {
        console.error("Error loading transactions:", error);
      }
    }

    if (accountData?.iban && selectedBank) {
      fetchTransactions();
    }
  }, [accountData, selectedBank]); // runs when accountData changes (e.g., after login)

    // Redirect if no bank selected
  if (!selectedBank) {
    alert("Please Select a Bank First")
    navigate('/');
    return null;
  }


  if (!userData) return <p>Please log in.</p>;

  //Handle transactions 
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
      const response = await axios.post(`${selectedBank.apiUrl}/api/accounts/transaction`, 
        {
          account: accountData,
          amount,
          iban,
          password
        }

      )

      alert("Transaction Complete")

      window.location.reload();

      console.log("Transaction successful:", response.data);
      localStorage.setItem("accountData", JSON.stringify(response.data));

       // Refresh transactions after sending funds
      const updatedTransactions = await axios.get(`${selectedBank.apiUrl}/api/accounts/transactions/${accountData.iban}`);
      setTransactions(updatedTransactions.data);

    } catch (error) {
      console.error("Error sending funds:", error);
      alert(error.response.data);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <div>
        <div style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '20px', marginTop: '10px', marginBottom: '20px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }}>
          <h2>{selectedBank.name} - Dashboard</h2>
          <h3>Welcome, {userData.firstName}!</h3>
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
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Enter Password" />
        <button type="button"
          onClick={handleSendFunds} style={{ padding: '5px 10px' }}>Send</button>

        <h3>Transaction History</h3>
        <ul>
          {transactions.map((tx, idx) => (
            <li key={idx}>
              {tx.senderIban} → {tx.receiverIban}: {new Intl.NumberFormat('en-IE', { style: 'currency', currency: 'EUR' }).format(tx.amount)}
              <span> ({new Date(tx.timestamp).toLocaleString()})</span>
            </li>
          ))}
        </ul>

      </div>

    </div>
  );
};

export default Home;
