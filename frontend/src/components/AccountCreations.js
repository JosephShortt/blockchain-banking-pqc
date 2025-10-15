
import { useState } from 'react';
import axios from 'axios';
import { Navigate, useNavigate } from 'react-router-dom';
import { useUser } from '../contexts/UserContext';
function AccountCreation() {

    const [firstName, setFirstName] = useState("");
    const [surname, setSurname] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const { setUserData, setAccountData } = useUser();

    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await axios.post('http://localhost:8080/api/accounts', {
                firstName,
                surname,
                email,
                password
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
            )

            alert(`Account created for ${response.data.firstName}`)


        }
        catch (error) {
            console.error("Error creating account", error);
            alert("Faled to create account")
        }
        
        try {
            const response = await axios.post('http://localhost:8080/api/accounts/login', {
                email,
                password
            });

            // 200 OK, login successful
            setUserData(response.data.accountResponse);
            setAccountData(response.data.bankAccount)

            localStorage.setItem("userData", JSON.stringify(response.data.accountResponse));
            localStorage.setItem("accountData", JSON.stringify(response.data.bankAccount));

            navigate('/')

        } catch (error) {

            console.error("Error creating account:", error);

        }

    }


    return (
        <div>
            <h1>Account Creation</h1>

            <form onSubmit={handleSubmit}>

                <label>Enter your first name:
                    <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />

                </label>
                <br />

                <label>Enter your surname:
                    <input
                        type="text"
                        value={surname}
                        onChange={(e) => setSurname(e.target.value)}
                    />

                </label>
                <br />

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

}

export default AccountCreation;