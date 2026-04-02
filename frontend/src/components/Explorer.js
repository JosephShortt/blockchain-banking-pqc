import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const ADMIN_USERNAME = 'admin';
const ADMIN_PASSWORD = 'admin123';

function Explorer() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [blocks, setBlocks] = useState([]);
    const [selectedBlock, setSelectedBlock] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleLogin = (e) => {
        e.preventDefault();
        if (username === ADMIN_USERNAME && password === ADMIN_PASSWORD) {
            setIsLoggedIn(true);
            setError('');
        } else {
            setError('Invalid credentials');
        }
    };


    const fetchChain = useCallback(async () => {
        setLoading(true);
        try {
            const response = await axios.get(`/api/blockchain/chain`);
            setBlocks(response.data.sort((a, b) => b.blockNumber - a.blockNumber));
        } catch (error) {
            console.error('Error fetching chain:', error);
        }
        setLoading(false);
    },[]);

    useEffect(() => {
        if (isLoggedIn) {
            fetchChain();
        }
    }, [isLoggedIn, fetchChain]);

    const fetchTransactions = async (blockNumber) => {
    try {
        const isProduction = process.env.NODE_ENV === 'production';
        const bankUrls = isProduction ? [
            'https://blockchainbank.duckdns.org/api',
            'https://blockchainbank.duckdns.org/bank-b/api',
            'https://blockchainbank.duckdns.org/bank-c/api'
        ] : [
            'https://localhost:8443',
            'https://localhost:8444',
            'https://localhost:8445'
        ];

        const results = await Promise.allSettled(
            bankUrls.map(url => 
                axios.get(`${url}/blockchain/block/${blockNumber}/transactions`)
            )
        );

        const allTxs = results
            .filter(r => r.status === 'fulfilled')
            .flatMap(r => r.value.data);

        setTransactions(allTxs);
        setSelectedBlock(blockNumber);
    } catch (error) {
        console.error('Error fetching transactions:', error);
    }
};

    const getStatusColor = (status) => {
        switch (status) {
            case 'FINALIZED': return '#28a745';
            case 'PROPOSED': return '#ffc107';
            default: return '#6c757d';
        }
    };

    if (!isLoggedIn) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <div style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '40px', boxShadow: '0 4px 8px rgba(0,0,0,0.1)', minWidth: '300px' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Admin Login</h2>
                    <p style={{ textAlign: 'center', color: '#666', marginBottom: '20px' }}>Blockchain Explorer</p>
                    {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && handleLogin(e)}
                            style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                        <button
                            onClick={handleLogin}
                            style={{ padding: '10px', backgroundColor: '#1a1a2e', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                        >
                            Login
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2>Blockchain Explorer</h2>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={fetchChain} style={{ padding: '8px 16px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Refresh
                    </button>
                </div>
            </div>

            {loading && <p>Loading chain...</p>}

            <div style={{ display: 'grid', gridTemplateColumns: selectedBlock ? '1fr 1fr' : '1fr', gap: '20px' }}>
                {/* Blocks List */}
                <div>
                    <h3>Blocks ({blocks.length})</h3>
                    {blocks.map((block) => (
                        <div
                            key={block.blockNumber}
                            onClick={() => fetchTransactions(block.blockNumber)}
                            style={{
                                border: `2px solid ${selectedBlock === block.blockNumber ? '#007bff' : '#ddd'}`,
                                borderRadius: '8px',
                                padding: '15px',
                                marginBottom: '10px',
                                cursor: 'pointer',
                                backgroundColor: selectedBlock === block.blockNumber ? '#f0f7ff' : 'white'
                            }}
                        >
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <strong>Block #{block.blockNumber}</strong>
                                <span style={{
                                    backgroundColor: getStatusColor(block.status),
                                    color: 'white',
                                    padding: '2px 8px',
                                    borderRadius: '12px',
                                    fontSize: '12px'
                                }}>
                                    {block.status}
                                </span>
                            </div>
                            <p style={{ margin: '5px 0', fontSize: '12px', color: '#666' }}>
                                Proposer: <strong>{block.proposerId}</strong>
                            </p>
                            <p style={{ margin: '5px 0', fontSize: '11px', color: '#999', wordBreak: 'break-all' }}>
                                Hash: {block.hash?.substring(0, 40)}...
                            </p>
                            <p style={{ margin: '5px 0', fontSize: '11px', color: '#999' }}>
                                {new Date(block.createdAt).toLocaleString()}
                            </p>
                        </div>
                    ))}
                </div>

                {/* Transactions Panel */}
                {selectedBlock !== null && (
                    <div>
                        <h3>Transactions in Block #{selectedBlock}</h3>
                        {transactions.length === 0 ? (
                            <p style={{ color: '#666' }}>No transactions in this block.</p>
                        ) : (
                            transactions.map((tx, idx) => (
                                <div key={idx} style={{
                                    border: '1px solid #ddd',
                                    borderRadius: '8px',
                                    padding: '15px',
                                    marginBottom: '10px',
                                    backgroundColor: '#f9f9f9'
                                }}>
                                    <p style={{ margin: '3px 0' }}><strong>Amount:</strong> €{tx.amount}</p>
                                    <p style={{ margin: '3px 0' }}><strong>From:</strong> {tx.senderIban} ({tx.senderBankId})</p>
                                    <p style={{ margin: '3px 0' }}><strong>To:</strong> {tx.receiverIban} ({tx.receiverBankId})</p>
                                    <p style={{ margin: '3px 0', fontSize: '11px', color: '#999', wordBreak: 'break-all' }}>
                                        <strong>Signature:</strong> {tx.senderSignature?.substring(0, 40)}...
                                    </p>
                                </div>
                            ))
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Explorer;