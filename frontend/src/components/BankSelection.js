import { useNavigate } from 'react-router-dom';
import { useUser } from '../contexts/UserContext';

function BankSelection() {
    const navigate = useNavigate();
    const { setSelectedBank } = useUser();

    const banks = [
        { id: 'bank-a', name: 'Bank A', apiUrl: 'https://localhost:8443' },
        { id: 'bank-b', name: 'Bank B', apiUrl: 'https://localhost:8444' },
        { id: 'bank-c', name: 'Bank C', apiUrl: 'https://localhost:8445' }
    ];

    const handleBankSelect = (bank) => {
        setSelectedBank(bank);
        localStorage.setItem("selectedBank", JSON.stringify(bank));
        navigate('/register');
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '50px' }}>
            <h1>Select Your Bank</h1>
            <div style={{ display: 'flex', gap: '20px', marginTop: '30px' }}>
                {banks.map(bank => (
                    <button 
                        key={bank.id}
                        onClick={() => handleBankSelect(bank)}
                        style={{ padding: '20px 40px', fontSize: '18px', cursor: 'pointer' }}
                    >
                        {bank.name}
                    </button>
                ))}
            </div>
        </div>
    );
}

export default BankSelection;