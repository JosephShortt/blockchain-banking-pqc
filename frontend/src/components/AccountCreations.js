
import { useState } from 'react';
import axios from 'axios';
function AccountCreation() {

    const [firstName, setFirstName] = useState("");
    const [surname, setSurname] = useState("");
    const [customerId, setCustomerId] = useState("");
    const [email, setEmail] = useState("");


    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await axios.post('http://localhost:8080/api/accounts', {
                customerId,
                firstName,
                surname,
                email
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
            )
            alert(`Account created for ${response.data.email}`)
        }
        catch(error){
            console.error("Error creating account",error);
            alert("Faled to create account")
        }
       
    }


    return (
        <div>
            <h1>Account Creation</h1>

            <form onSubmit={handleSubmit}>
                <label>Enter your customer ID:
                    <input
                        type="text"
                        value={customerId}
                        onChange={(e) => setCustomerId(e.target.value)}
                    />

                </label>

                <label>Enter your first name:
                    <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />

                </label>

                <label>Enter your surname:
                    <input
                        type="text"
                        value={surname}
                        onChange={(e) => setSurname(e.target.value)}
                    />

                </label>

                <label>Enter your email:
                    <input
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                </label>

                <input type="submit" />
            </form>
        </div>

    )

}

export default AccountCreation;