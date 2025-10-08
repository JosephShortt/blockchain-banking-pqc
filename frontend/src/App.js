import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import AccountCreation from './components/AccountCreations';

function App() {
  return (
    <Router>
      <nav>
        <Link to="/create">Create Account</Link>
      </nav>

      <Routes>
        <Route path="/create" element={<AccountCreation />} />

      </Routes>
    </Router>
  );
}

export default App;
