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