import { useNavigate } from 'react-router-dom';
import { useUser } from '../contexts/UserContext';

function BankSelection() {
    const navigate = useNavigate();
    const { setSelectedBank } = useUser();

    const isProduction = process.env.NODE_ENV === 'production';


     const banks = isProduction ? [
        // Production - use actual server IPs and domain
        { id: 'bank-a', name: 'Bank A', apiUrl: 'https://blockchainbank.duckdns.org:8443',  color: '#1a1a2e'  },
        { id: 'bank-b', name: 'Bank B', apiUrl: 'https://blockchainbank.duckdns.org/bank-b', color: '#16213e' },
        { id: 'bank-c', name: 'Bank C', apiUrl: 'https://blockchainbank.duckdns.org/bank-c', color: '#0f3460'  }
    ] : [
        // Development - use localhost
        { id: 'bank-a', name: 'Bank A', apiUrl: 'https://localhost:8443', color: '#1a1a2e' },
        { id: 'bank-b', name: 'Bank B', apiUrl: 'https://localhost:8444', color: '#16213e'  },
        { id: 'bank-c', name: 'Bank C', apiUrl: 'https://localhost:8445', color: '#0f3460' }
    ];

    const handleBankSelect = (bank) => {
        setSelectedBank(bank);
        localStorage.setItem("selectedBank", JSON.stringify(bank));
        navigate('/login');
    };

    return (
        <div style={{ background: '#f5f7fa', minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            
            <div style={{ textAlign: 'center', marginBottom: '48px' }}>
                <img src="/logo.png" alt="logo" style={{ width: '120px', marginBottom: '16px' }} />
                <h1 style={{ margin: '0 0 8px', fontSize: '28px', fontWeight: '700', color: '#1a1a2e' }}>Blockchain Bank</h1>
                <p style={{ margin: 0, color: '#999', fontSize: '14px', letterSpacing: '2px', textTransform: 'uppercase' }}>Post-Quantum Secured</p>
            </div>

            <p style={{ margin: '0 0 32px', color: '#555', fontSize: '16px' }}>Select your bank to continue</p>

            <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap', justifyContent: 'center' }}>
                {banks.map(bank => (
                    <div
                        key={bank.id}
                        onClick={() => handleBankSelect(bank)}
                        style={{
                            background: 'white',
                            borderRadius: '16px',
                            padding: '32px 40px',
                            cursor: 'pointer',
                            boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
                            textAlign: 'center',
                            width: '160px',
                            transition: 'transform 0.2s, box-shadow 0.2s',
                            border: '2px solid transparent'
                        }}
                        onMouseEnter={e => {
                            e.currentTarget.style.transform = 'translateY(-4px)';
                            e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.12)';
                            e.currentTarget.style.borderColor = bank.color;
                        }}
                        onMouseLeave={e => {
                            e.currentTarget.style.transform = 'translateY(0)';
                            e.currentTarget.style.boxShadow = '0 2px 12px rgba(0,0,0,0.08)';
                            e.currentTarget.style.borderColor = 'transparent';
                        }}
                    >
                        <div style={{
                            width: '56px', height: '56px', borderRadius: '14px',
                            background: bank.color, margin: '0 auto 16px',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            fontSize: '22px', color: 'white', fontWeight: '700'
                        }}>
                            {bank.name.charAt(bank.name.length - 1)}
                        </div>
                        <p style={{ margin: '0 0 4px', fontWeight: '600', fontSize: '16px', color: '#1a1a2e' }}>{bank.name}</p>
                        <p style={{ margin: 0, fontSize: '12px', color: '#999' }}>Click to select</p>
                    </div>
                ))}
            </div>

            <p style={{ marginTop: '48px', fontSize: '12px', color: '#bbb', textAlign: 'center' }}>
                Secured with CRYSTALS-Dilithium Post-Quantum Cryptography
            </p>
        </div>
    );
}

export default BankSelection;