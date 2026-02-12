import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import BankSelection from './components/BankSelection.js';
import AccountCreation from './components/AccountCreations';
import UserLogin from './components/UserLogin.js';
import Home from './components/Home';
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
      padding: '10px 20px',
      borderBottom: '1px solid #ccc',
      position: 'fixed',
      top: 0,
      width: '100%',
      backgroundColor: 'white',
      zIndex: 1000
    }}>

      <div>
        <button style={{ border: 'none', padding: 0, }}> <img src="/logo.png" alt="home" onClick={() => navigate('/')} style={{ width: '50px', height: 'auto', cursor: 'pointer' }} /></button>
      </div>

      <div style={{ textAlign: 'center', flexGrow: 1 }}>
        <h1 style={{ margin: 0 }}>{selectedBank ? selectedBank.name : 'Bank'} </h1>
      </div>


      <div style={{ display: 'flex', gap: '10px', marginRight: '30px' }}>
        <button onClick={() => navigate('/register')} style={{ padding: '5px 10px' }}>Create Account</button>
        <button onClick={() => navigate('/login')} style={{ padding: '5px 10px' }}>Login</button>
        <button onClick={() => navigate('/dashboard')} style={{ padding: '5px 10px' }}>Dashboard</button>
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
          </Routes>
        </div>
      </Router>

    </>
  );
}

export default App;
