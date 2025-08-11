Unify Finance
Unify Finance is a unified financial tool that allows users to access and manage all their bank data from one location.

Tech Stack
Backend

Java Spring Boot (folder: financialapp)

JPA / Hibernate

PostgreSQL

Runs on port 8080

Frontend

React + Vite + TypeScript (folder: financial-frontend)

Runs on port 5173

Getting Started
1. Backend Setup
Install and configure PostgreSQL on your machine.

Update application.properties with your own PostgreSQL username, password, and database name.

Create a .env file in the backend root containing:
PLAID_CLIENT_ID=your_client_id
PLAID_SECRET=your_secret
PLAID_ENV=sandbox_or_production
ENCRYPTION_KEY=your_encryption_key

Install dotenv if required (npm install dotenv in backend directory, though Spring Boot may not need it depending on config).

2. Frontend Setup
Navigate to financial-frontend and install dependencies:

npm install
Start the development server:
npm run dev


Running the App

Backend:
cd financialapp
./mvnw spring-boot:run

Frontend:
cd financial-frontend
npm run dev

Visit the app at:
http://localhost:5173

Live Demo
If you don’t want to run the project locally, you can visit:
unifyfinance.ca

Contact
For questions or feedback, email:
joshuamoreirapersonal@gmail.com

Privacy Policy and TOS found at unifyfinance.ca