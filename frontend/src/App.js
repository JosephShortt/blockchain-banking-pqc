import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import BankSelection from './components/BankSelection.js';
import AccountCreation from './components/AccountCreations';
import UserLogin from './components/UserLogin.js';
import Home from './components/Home';
import Explorer from './components/Explorer';
import { useNavigate } from 'react-router-dom';
import { useUser } from './contexts/UserContext.js';

function Navigation() {
  const navigate = useNavigate();
  const { selectedBank } = useUser();

  return (

    <div style={{
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '0 24px',
      height: '60px',
      borderBottom: '1px solid #eee',
      position: 'fixed',
      top: 0,
      width: '100%',
      backgroundColor: 'white',
      zIndex: 1000,
      boxSizing: 'border-box',
      boxShadow: '0 2px 8px rgba(0,0,0,0.06)'
    }}>

      <div>
        <button style={{ border: 'none', padding: 0, }}> <img src="/logo.png" alt="home" onClick={() => navigate('/')} style={{ width: '50px', height: 'auto', cursor: 'pointer' }} /></button>
      </div>

      <div style={{ textAlign: 'center', flexGrow: 1 }}>
        <h1 style={{ margin: 0, fontSize: '32px', fontWeight: '600', color: '#1a1a2e' }}>{selectedBank ? selectedBank.name : 'Bank'} </h1>
      </div>


      <div style={{ display: 'flex', gap: '8px' }}>
        <button onClick={() => navigate('/register')} style={{
          padding: '8px 16px', border: '1px solid #1a1a2e', borderRadius: '8px',
          backgroundColor: 'transparent', color: '#1a1a2e', cursor: 'pointer', fontSize: '14px'
        }}>Register</button>
        <button onClick={() => navigate('/login')} style={{
          padding: '8px 16px', border: '1px solid #1a1a2e', borderRadius: '8px',
          backgroundColor: 'transparent', color: '#1a1a2e', cursor: 'pointer', fontSize: '14px'
        }}>Login</button>
        <button onClick={() => navigate('/dashboard')} style={{
          padding: '8px 16px', border: 'none', borderRadius: '8px',
          backgroundColor: '#1a1a2e', color: 'white', cursor: 'pointer', fontSize: '14px'
        }}>Dashboard</button>
        <button onClick={() => navigate('/explorer')} style={{
          padding: '8px 16px', border: 'none', borderRadius: '8px',
          backgroundColor: '#e8b400', color: '#1a1a2e', cursor: 'pointer', fontSize: '14px',
          fontWeight: '600'
        }}>Admin</button>
      </div>
    </div >
  );
}


function App() {

  return (
    <>
      <Router>
        <Navigation />
        <div style={{ paddingTop: '70px' }}>
          <Routes>
            <Route path="/" element={<BankSelection />} />
            <Route path="/register" element={<AccountCreation />} />
            <Route path="/login" element={<UserLogin />} />
            <Route path="/dashboard" element={<Home />} />
            <Route path="/explorer" element={<Explorer />} />
          </Routes>
        </div>
      </Router>

    </>
  );
}

export default App;
