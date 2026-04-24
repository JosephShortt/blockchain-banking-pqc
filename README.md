# Final Year Project
# Blockchain Bank — Post-Quantum Cryptographic Interbank Settlement System

A distributed blockchain-based interbank settlement system secured with CRYSTALS-Dilithium5 post-quantum cryptographic signatures, implementing Practical Byzantine Fault Tolerant (PBFT) consensus across three independent cloud-hosted bank nodes.

## Overview

This system simulates a real-world interbank settlement network consisting of three banks — Bank A, Bank B, and Bank C — each operating as an independent node in a permissioned blockchain network. Customers can open accounts, send money between banks, and view their transaction history. Interbank transactions are cryptographically signed using CRYSTALS-Dilithium5 — a NIST-standardised post-quantum digital signature algorithm — batched into blocks, and finalised through PBFT consensus. Upon consensus, receiver accounts are automatically credited and interbank reserve balances are updated to reflect net settlement positions.

The system is live at **https://blockchainbank.duckdns.org**

### Key Features

- Post-Quantum Security — every transaction signed with CRYSTALS-Dilithium5
- Distributed Blockchain — immutable ledger replicated across three cloud nodes
- PBFT Consensus — three-phase consensus protocol for distributed agreement
- Interbank Settlement — automatic account crediting and reserve settlement after consensus
- Multi-Cloud Deployment — Bank A on OCI, Banks B and C on AWS
- Blockchain Explorer — admin interface showing blocks, transactions and settlement positions
- Transaction Simulator — automated cross-bank transaction generation for demonstration
- CI/CD Pipeline — automated build and deployment via GitHub Actions

## Architecture

The system consists of three independent bank nodes:

| Node   | Cloud Provider | Role |
|--------|---------------|------|
| Bank A | Oracle Cloud Infrastructure | Primary node, Nginx reverse proxy, SSL termination, React frontend |
| Bank B | Amazon Web Services | Backend node |
| Bank C | Amazon Web Services | Backend node |

Each node runs:
- Spring Boot backend (port 8443)
- PostgreSQL database
- Docker container

Bank A additionally runs an Nginx reverse proxy that handles all inbound HTTPS traffic, serves the React frontend, and routes requests to Banks B and C.

### Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17, Spring Boot 3.5 |
| Frontend | React, Axios, React Router |
| Database | PostgreSQL 16 |
| Cryptography | CRYSTALS-Dilithium5 via Bouncy Castle |
| Containerisation | Docker |
| Reverse Proxy | Nginx |
| CI/CD | GitHub Actions |
| Cloud | Oracle Cloud Infrastructure, AWS EC2 |

---

## Prerequisites

- Java 17 or later
- Apache Maven 3.8 or later
- Node.js 18 or later
- Docker
- PostgreSQL 14 or later

---

## Database Setup

Create a PostgreSQL database named `blockchainbankdatabase` with the following schemas:

```sql
CREATE SCHEMA bank_a;
CREATE SCHEMA bank_b;
CREATE SCHEMA bank_c;
CREATE SCHEMA blockchain;
```

Hibernate will automatically create all required tables on first startup.

---

## Running Locally

### Backend

Set the following environment variables and run for each bank node:

**Bank A:**
```bash
export BANK_ID=bank-a
export BANK_NAME="Bank A"
export DB_SCHEMA=bank_a
export SERVER_PORT=8443
export DB_HOST=localhost
export DB_NAME=blockchainbankdatabase
export DB_USER=postgres
export DB_PASSWORD=
export SERVER_SSL_ENABLED=false
export BANK_A_URL=http://localhost:8443
export BANK_B_URL=http://localhost:8444
export BANK_C_URL=http://localhost:8445

cd backend
./mvnw spring-boot:run
```

Repeat for Bank B (port 8444, schema bank_b) and Bank C (port 8445, schema bank_c).

### Frontend

```bash
cd frontend
npm install
npm start
```

The frontend will be available at `http://localhost:3000`.

---

## Running with Docker

```bash
cd backend
./mvnw clean package -DskipTests
docker build -t bank-backend .

docker run -d --name bank-a --network host \
  -e BANK_ID=bank-a \
  -e BANK_NAME="Bank A" \
  -e DB_SCHEMA=bank_a \
  -e DB_HOST=localhost \
  -e DB_NAME=blockchainbankdatabase \
  -e DB_USER=postgres \
  -e DB_PASSWORD= \
  -e SERVER_SSL_ENABLED=false \
  -e BANK_A_URL=http://localhost:8443 \
  -e BANK_B_URL=http://localhost:8444 \
  -e BANK_C_URL=http://localhost:8445 \
  bank-backend
```

---

## Running Tests

Run the full unit test suite:

```bash
cd backend
./mvnw test
```

Run the cryptographic benchmark tests:

```bash
cd backend
./mvnw test -Dtest=CryptoBenchmarkTest
```

---

## Demo Accounts

The following accounts are pre-configured for use with the transaction simulator. All use password `demo`:

| Bank   | Email | IBAN |
|--------|-------|------|
| Bank A | demo1@banka.com | IE29BANKA00004 |
| Bank A | demo2@banka.com | IE29BANKA00005 |
| Bank A | demo3@banka.com | IE29BANKA00006 |
| Bank B | demo1@bankb.com | IE29BANKB00005 |
| Bank B | demo2@bankb.com | IE29BANKB00006 |
| Bank B | demo3@bankb.com | IE29BANKB00007 |
| Bank C | demo1@bankc.com | IE29BANKC00003 |
| Bank C | demo2@bankc.com | IE29BANKC00004 |
| Bank C | demo3@bankc.com | IE29BANKC00005 |

### Starting the Simulator

# Bank A
curl -k -X POST https://localhost:8443/api/simulate/start

# Bank B
curl -X POST http://localhost:8444/api/simulate/start

# Bank C
curl -X POST http://localhost:8445/api/simulate/start

### Stopping the Simulator

# Bank A
curl -k -X POST https://localhost:8443/api/simulate/stop

# Bank B
curl -X POST http://localhost:8444/api/simulate/stop

# Bank C
curl -X POST http://localhost:8445/api/simulate/stop

---

## Blockchain Explorer

The blockchain explorer is accessible at `/explorer` using:

- **Username:** admin
- **Password:** admin123

---

## CI/CD

The GitHub Actions workflow automatically builds and deploys on every push to the `main` branch:

1. Builds the Spring Boot backend with Maven
2. Builds the React frontend with Node.js
3. Packages both as Docker images and pushes to Docker Hub
4. SSH's into each cloud server and restarts the containers with the latest images
