import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../contexts/UserContext";
import axios from "axios";

function UserLogin() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const { setUserData, setAccountData, selectedBank } = useUser();
    const navigate = useNavigate();


    async function handleSubmit(e) {
        e.preventDefault()
        try {
            const response =  await axios.post(`${selectedBank.apiUrl}/api/accounts/login`, {
                email,
                password
            });

            // 200 OK, login successful
            setUserData(response.data.accountResponse);
            setAccountData(response.data.bankAccount)
            
            localStorage.setItem("userData", JSON.stringify(response.data.accountResponse));
            localStorage.setItem("accountData", JSON.stringify(response.data.bankAccount));
            localStorage.setItem("selectedBank",JSON.stringify(selectedBank));
            
            console.log("Login successful:", response.data);
            navigate('/dashboard')

        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert("Invalid credentials");
            } else {
                console.error("Login error:", error);
            }
        }
    }

     const input = {
        padding: '10px 14px', borderRadius: '8px',
        border: '1px solid #ddd', fontSize: '14px',
        width: '100%', boxSizing: 'border-box', outline: 'none'
    };


    
  return (
        <div style={{ background: '#f5f7fa', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <div style={{ background: 'white', borderRadius: '12px', padding: '36px', width: '100%', maxWidth: '400px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
                <div style={{ textAlign: 'center', marginBottom: '28px' }}>
                    <h2 style={{ margin: '0 0 6px', color: '#1a1a2e' }}>Welcome Back</h2>
                    <p style={{ margin: 0, color: '#999', fontSize: '14px' }}>{selectedBank?.name}</p>
                </div>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    <div>
                        <p style={{ margin: '0 0 6px', fontSize: '13px', color: '#555', fontWeight: '500' }}>Email</p>
                        <input style={input} type="text" value={email} onChange={e => setEmail(e.target.value)} placeholder="Enter your email" />
                    </div>
                    <div>
                        <p style={{ margin: '0 0 6px', fontSize: '13px', color: '#555', fontWeight: '500' }}>Password</p>
                        <input style={input} type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Enter your password" />
                    </div>
                    <button type="submit" style={{
                        marginTop: '8px', padding: '12px', background: '#1a1a2e',
                        color: 'white', border: 'none', borderRadius: '8px',
                        fontSize: '15px', fontWeight: '600', cursor: 'pointer'
                    }}>
                        Login
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: '#999' }}>
                    Don't have an account?{' '}
                    <span onClick={() => navigate('/register')} style={{ color: '#1a1a2e', cursor: 'pointer', fontWeight: '600' }}>
                        Register
                    </span>
                </p>
            </div>
        </div>
    );

};
export default UserLogin;