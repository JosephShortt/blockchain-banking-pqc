
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../contexts/UserContext';
import api from '../api';
import axios from 'axios';

function AccountCreation() {

    const [firstName, setFirstName] = useState("");
    const [surname, setSurname] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const { setUserData, setAccountData, selectedBank } = useUser();

    const navigate = useNavigate();

    // Redirect if no bank selected
    if (!selectedBank) {
        navigate('/');
        return null;
    }

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await api.post(`${selectedBank.apiUrl}/api/accounts`, {
                firstName,
                surname,
                email,
                password,
                bankId: selectedBank.bankId
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
            )

            alert(`Account created at ${selectedBank.bankName} for ${response.data.firstName}`);


        }
        catch (error) {
            console.error("Error creating account", error);
            alert(error.response.data);
        }
        
        try {
            const response =  await api.post(`${selectedBank.apiUrl}/api/accounts/login`, {
                email,
                password
            });

            // 200 OK, login successful
            setUserData(response.data.accountResponse);
            setAccountData(response.data.bankAccount)

            localStorage.setItem("userData", JSON.stringify(response.data.accountResponse));
            localStorage.setItem("accountData", JSON.stringify(response.data.bankAccount));
            localStorage.setItem("selectedBank", JSON.stringify(selectedBank));

            navigate('/')

        } catch (error) {

            console.error("Error creating account:", error);

        }

    }


    return (
        <div>
            <h1>Account Creation  - {selectedBank.bankName}</h1>

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