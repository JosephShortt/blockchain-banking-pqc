import { useState } from "react";

function UserLogin(){
    
    const [email,setEmail] = useState("");
    const [password,setPassword] = useState("");

    function handleSubmit(e){

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