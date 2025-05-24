# Payment-Microservice
A Spring Boot-based Payment Microservice that validates incoming payment requests, generates transaction IDs, and asynchronously processes payments using Kafka and Stripe. It supports idempotent payment handling, stores transaction metadata in PostgreSQL, and is designed to integrate easily with scalable microservice ecosystems.

**Features**

- 🔒 Validates and accepts payment requests via REST API
- 🧾 Generates unique transaction IDs
- 📤 Publishes payment data to Kafka Message Queues
- 📥 Kafka consumer processes the payment using Stripe API
- ✅ Idempotent requests (no double charges)
- 📦 Stores transactions with payment method id (for recurrent transactions) in PostgreSQL
- 🐳 Dockerized setup for local development or cloud deployment
- 💳 Supports transaction made by Card and Apple Pay


**Tech Stack**

- Frontend - ReactJS & Javascript
- Backend Framework - Spring Boot          
- Messaging Queue   - Apache Kafka         
- Payment Gateway   - Stripe API           
- Database          - PostgreSQL   
- Containerization  - Docker, Docker Compose
- Build Tool        - Maven      
- Language          - Java 17  
