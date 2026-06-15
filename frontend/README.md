# Inventory Management Frontend

React frontend for Milestone 1.

## Features

- Login with JWT
- Register user
- Store token in `localStorage`
- Dashboard metrics
- Inventory list
- Add inventory item
- Update inventory item
- Delete inventory item
- Search and filter
- Low-stock view
- Role-aware admin actions

## Run

Start the Spring Boot backend first:

```powershell
$env:GRADLE_OPTS='-Djavax.net.ssl.trustStore=C:\Users\294541\.gradle\certs\zscaler-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit'
cd ..\backend
.\gradlew.bat bootRun
```

Then install Node.js if needed and run:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Open:

```text
http://localhost:5173
```

Default login:

```text
username: admin
password: admin123
```
