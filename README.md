# 💸 Finapp — Personal Finance Dashboard

**Finapp** is a full-stack personal finance web app that connects to real bank accounts using Plaid, tracks spending, and displays it in a React dashboard. Built from scratch using Spring Boot, React + TypeScript, PostgreSQL, and Vite.

## Features

- Secure login with Spring Security
- Plaid bank account linking
- Transaction and balance tracking
- Dashboard with dynamic account tiles
- Monorepo with frontend and backend

## Tech Stack

- **Frontend:** React, TypeScript, Vite
- **Backend:** Java 17, Spring Boot, Spring Security
- **Database:** PostgreSQL
- **Integration:** Plaid API
- **Tools:** Node.js, Maven, Git

## Project Structure

/financial-app/
├── financialapp/ (Spring Boot backend)
├── financial-frontend/ (React + Vite frontend)
└── README.md



## How to Run This Project

**Requirements:**
- Java 17
- Node.js (v18+)
- PostgreSQL running locally
- Git

### 1. Clone the repo


git clone https://github.com/MapleWasntTaken/finance-app.git
cd finance-app
2. Run the Backend

cd financialapp
./mvnw spring-boot:run
If you're on Windows:

mvnw.cmd spring-boot:run
Make sure PostgreSQL is running and the database finapp exists.

In financialapp/src/main/resources/application.properties, configure:

properties

Edit
spring.datasource.url=jdbc:postgresql://localhost:5432/finapp
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

make a env file with 
PLAID_CLIENT_ID=your_plaid_client_id
PLAID_SECRET=your_plaid_secret
PLAID_ENV=sandbox
ENCRYPTION_KEY=your_key
3. Run the Frontend in terminal
cd frontendfilepath
npm install
npm run dev
App will run at http://localhost:5173

4. Log In and Use the App
Visit the site

Sign in using the login form

Link a bank account (Plaid sandbox)

See accounts and transactions

Built By
Joshua Moreira
BSc Computing (Security Specialization), Queen’s University

This project was built entirely from scratch with no templates. Every system and decision was designed, written, debugged, and integrated manually. This readme was made by chat gpt. My email is joshuamoreirapersonal@gmail.com if you have any questions. 
