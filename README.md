# Inventory Management System

This project is split into two applications:

- `backend/` -Spring Boot REST API with JWT security
- `frontend/` - React frontend integrated with the backend APIs

## Run Backend

```powershell
cd backend
$env:GRADLE_OPTS='-Djavax.net.ssl.trustStore=C:\Users\294541\.gradle\certs\zscaler-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit'
.\gradlew.bat bootRun
```

Backend URL:

```text
http://localhost:8080
```

## Run Frontend

Open a second terminal:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Frontend URL:

```text
http://localhost:5173
```

Default admin login:

```text
username: admin
password: admin123
```

