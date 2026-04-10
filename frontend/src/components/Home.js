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
  const [txBlockNumbers, setTxBlockNumbers] = useState({});
  const [blockModal, setBlockModal] = useState(null);



  const fetchBlockNumber = async (tx, idx) => {
    if (txBlockNumbers[idx] !== undefined) return;
    try {
      const response = await axios.get(`${selectedBank.apiUrl}/api/accounts/transactions/block`, {
        params: { localTransactionId: tx.id }
      });
      setTxBlockNumbers(prev => ({ ...prev, [idx]: response.data }));
    } catch (error) {
      console.error('Error fetching block number:', error);
    }
  };

  const fetchBlockDetails = async (blockNumber) => {
    try {
      const response = await axios.get(`${selectedBank.apiUrl}/api/blockchain/block/${blockNumber}`);
      setBlockModal(response.data);
    } catch (error) {
      console.error('Error fetching block:', error);
    }
  };

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
        <div style={{
          background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
          borderRadius: '16px',
          padding: '28px',
          marginBottom: '20px',
          color: 'white',
          boxShadow: '0 8px 32px rgba(26, 26, 46, 0.3)'
        }}>
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
              onClick={() => {
                setSelectedTx(selectedTx === idx ? null : idx);
                fetchBlockNumber(tx, idx);
              }}
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
                    <p style={{ margin: '2px 0' }}>
                      Block: {txBlockNumbers[idx] !== undefined
                        ? (txBlockNumbers[idx] !== null
                          ? <span
                            onClick={(e) => { e.stopPropagation(); fetchBlockDetails(txBlockNumbers[idx]); }}
                            style={{ color: '#007bff', cursor: 'pointer', textDecoration: 'underline' }}
                          >
                            #{txBlockNumbers[idx]}
                          </span>
                          : 'Pending')
                        : 'Loading...'}
                    </p>
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
      {blockModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex', justifyContent: 'center', alignItems: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white', borderRadius: '8px',
            padding: '30px', maxWidth: '500px', width: '90%',
            maxHeight: '80vh', overflowY: 'auto'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3>Block #{blockModal.blockNumber}</h3>
              <button onClick={() => setBlockModal(null)}
                style={{ border: 'none', fontSize: '20px', cursor: 'pointer', background: 'none' }}>
                ✕
              </button>
            </div>
            <p><strong>Status:</strong> <span style={{
              backgroundColor: blockModal.status === 'FINALIZED' ? '#28a745' : '#ffc107',
              color: 'white', padding: '2px 8px', borderRadius: '12px', fontSize: '12px'
            }}>{blockModal.status}</span></p>
            <p><strong>Proposer:</strong> {blockModal.proposerId}</p>
            <p style={{ wordBreak: 'break-all' }}><strong>Hash:</strong> {blockModal.hash}</p>
            <p style={{ wordBreak: 'break-all' }}><strong>Previous Hash:</strong> {blockModal.prevHash}</p>
            <p><strong>Created:</strong> {new Date(blockModal.createdAt).toLocaleString()}</p>
          </div>
        </div>
      )}
    </div>
  );
}
export default Home;
