import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
function UserLogin() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [userData, setUserData] = useState(null);
    const [accountData, setAccountData] = useState(null);
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault()
        try {
            const response = await axios.post('http://localhost:8080/api/accounts/login', {
                email,
                password
            });

            // 200 OK, login successful
            setUserData(response.data.customer);
            setAccountData(response.data.bankAccount)
            console.log("Login successful:", response.data);


        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert("Invalid credentials");
            } else {
                console.error("Login error:", error);
            }
        }
    }

    return (
        <div>
            <h1>Login</h1>

            <form onSubmit={handleSubmit}>

                <label>Enter your email:
                    <input
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </label>
                <br />
                <label>Enter your Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </label>
                <br />

                <input type="submit" />
            </form>

            {userData && accountData && (
                <div>
                    <h2>Welcome, {userData.firstName}!</h2>
                    <p>Email: {userData.email}</p>
                    <p>Customer ID: {userData.customerId}</p>

                    <br />

                    <p><strong> Account Details</strong></p>
                    <p>Account ID: {accountData.accountId}</p>
                    <p>Account Type: {accountData.accountType}</p>
                    <p>Balance: {accountData.balance}</p>

                </div>
            )}
        </div>

    )

};
export default UserLogin;