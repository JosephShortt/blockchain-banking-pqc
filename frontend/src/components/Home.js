import { useState } from "react";
import axios from "axios";
import { useEffect } from "react";

function Home() {

  const [accountData = [], setAccountData] = useState();

  useEffect(() => {
    async function fetchAccountDetails() {
      try {
        const response = await axios.get("http://localhost:8080/api/accounts");
        console.log("Fetched accounts:", response.data);
        setAccountData(response.data)

      } catch (err) {
        console.error("Error fetching accounts:", err);
      }
    }
    fetchAccountDetails();
  }, []);

  const accounts = accountData.map((item) =>
    <div key={item.customerId}>
      <div style={{border: '3px solid rgba(0, 0, 0, 0.35)', margin:'10px', padding:'5px'}} >
        <p>
          <b>Customer ID: {item.customerId}</b>
        </p>

        <p>
          <b>Name: {item.firstName} {item.surname}</b>
        </p>
        <p>
          <b>Email: {item.email}</b>
        </p>
        <p>
          <b>Password: {item.password}</b>
        </p>
        <br />
      </div>
      
    </div>

  )



  console.log(accounts)

  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      
    }}>
      <div>
        {accounts}

      </div>
    </div >

  )

};

export default Home;
