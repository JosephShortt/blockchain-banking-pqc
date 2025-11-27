import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../contexts/UserContext";
import api from "../api";
function UserLogin() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const { setUserData, setAccountData } = useUser();
    const navigate = useNavigate();


    async function handleSubmit(e) {
        e.preventDefault()
        try {
            const response =  await api.post('/api/accounts/login', {
                email,
                password
            });

            // 200 OK, login successful
            setUserData(response.data.accountResponse);
            setAccountData(response.data.bankAccount)
            
            localStorage.setItem("userData", JSON.stringify(response.data.accountResponse));
            localStorage.setItem("accountData", JSON.stringify(response.data.bankAccount));
            
            console.log("Login successful:", response.data);
            navigate('/')

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
        </div>

    )

};
export default UserLogin;