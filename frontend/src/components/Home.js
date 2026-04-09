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
  const [currentBalance, setCurrentBalance] = useState(accountData?.balance);
  const [selectedTx, setSelectedTx] = useState(null);



  // useEffect runs once when the component mounts
  useEffect(() => {
    async function fetchData() {
      try {
        const balanceResponse = await axios.get(`${selectedBank.apiUrl}/api/accounts/balance/${accountData.iban}`);
        setCurrentBalance(balanceResponse.data);

        const response = await axios.get(`${selectedBank.apiUrl}/api/accounts/transactions/${accountData.iban}`);
        setTransactions(response.data); // save transactions to state

      } catch (error) {
        console.error("Error loading data:", error);
      }
    }

    if (accountData?.iban && selectedBank) {
      fetchData();
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

   const isSent = (tx) => tx.senderIban === accountData.iban;
  const otherIban = (tx) => isSent(tx) ? tx.receiverIban : tx.senderIban;
  const formatAmount = (tx) => {
    const formatted = new Intl.NumberFormat('en-IE', { style: 'currency', currency: 'EUR' }).format(tx.amount);
    return isSent(tx) ? `-${formatted}` : `+${formatted}`;
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <div style={{ width: '500px' }}>

        {/* Account Card */}
        <div style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '20px', marginTop: '10px', marginBottom: '20px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }}>
          <h2>{selectedBank.name} - Dashboard</h2>
          <h3>Welcome, {userData.firstName}!</h3>
          <p>Email: {userData.email}</p>
          <p>IBAN: {accountData.iban}</p>
          <p>Account ID: {accountData.accountId}</p>
          <p>Balance: {new Intl.NumberFormat('en-IE', { style: 'currency', currency: 'EUR' }).format(currentBalance)}</p>
        </div>

        {/* Send Funds */}
        <div style={{ marginBottom: '20px' }}>
          <input type="text" value={iban} onChange={(e) => setIban(e.target.value)} placeholder="Enter IBAN to send to" style={{ padding: '8px', marginRight: '8px', width: '200px' }} />
          <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Amount" style={{ padding: '8px', marginRight: '8px', width: '100px' }} />
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" style={{ padding: '8px', marginRight: '8px', width: '100px' }} />
          <button type="button" onClick={handleSendFunds} style={{ padding: '8px 16px' }}>Send</button>
        </div>

        {/* Transaction History */}
        <h3>Transaction History</h3>
        <div>
          {transactions.slice().reverse().map((tx, idx) => (
            <div
              key={idx}
              onClick={() => setSelectedTx(selectedTx === idx ? null : idx)}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '12px 16px',
                marginBottom: '6px',
                borderRadius: '8px',
                border: '1px solid #eee',
                cursor: 'pointer',
                backgroundColor: selectedTx === idx ? '#f8f9fa' : 'white',
                boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
              }}
            >
              <div>
                <div style={{ fontWeight: '500', fontSize: '14px' }}>
                  {isSent(tx) ? `To: ${otherIban(tx)}` : `From: ${otherIban(tx)}`}
                </div>

                {/* Expanded details */}
                {selectedTx === idx && (
                  <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
                    <p style={{ margin: '2px 0' }}>From: {tx.senderIban}</p>
                    <p style={{ margin: '2px 0' }}>To: {tx.receiverIban}</p>
                    <p style={{ margin: '2px 0' }}>Time: {new Date(tx.timestamp).toLocaleString()}</p>
                    <p style={{ margin: '2px 0' }}>Type: {tx.transactionType}</p>
                  </div>
                )}
              </div>

              <div style={{
                fontWeight: 'bold',
                fontSize: '15px',
                color: isSent(tx) ? '#dc3545' : '#28a745'
              }}>
                {formatAmount(tx)}
              </div>
            </div>
          ))}
        </div>

        {/* Transaction Detail Modal */}
      </div>
    </div>
  );
}
export default Home;
