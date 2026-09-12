# 🧪 Lab Test Booking System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react)

**A modern, intelligent lab test booking and sample queue management system with real-time processing estimation**

[Features](#-features) • [Quick Start](#-quick-start) • [API Docs](#-api-documentation) • [Tech Stack](#-tech-stack) • [Demo](#-demo)

</div>

---

## 🌟 Features

### For Patients
- 🗓️ **Smart Booking** - View available time slots and book lab tests
- 📊 **Real-time Tracking** - Monitor sample status from collection to report
- ⏱️ **Processing Estimation** - Get estimated wait times for your test results
- 📄 **Digital Reports** - Access your test reports securely online

### For Lab Technicians
- 🔬 **Sample Queue Management** - Prioritized queue based on test type and booking time
- 📋 **Status Updates** - Update sample status through the workflow
- 📈 **Workload Visibility** - View pending samples by state

### For Administrators
- 🏥 **Lab Test Management** - Create and manage different types of lab tests
- 🕐 **Time Slot Configuration** - Set up available time slots with capacity limits
- 🔐 **Role-Based Access Control** - Separate access for Admin, Technician, and Patient 
- 📊 **Multi-lab Support** - Manage multiple laboratories

---

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.x
- Node.js 18+ (for frontend)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/heyyadiii/lab-test-booking-system
   cd lab-test-booking-system
   ```

2. **Configure Database**

   Create a MySQL database:
   ```sql
   CREATE DATABASE lab_test_db;
   ```

3. **Set Environment Variables**
   ```bash
export DB_URL=jdbc:mysql://localhost:3306/lab_test_db
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key-min-256-bits
export SEED_DEFAULT_USERS=true
export FRONTEND_URL=http://localhost:3000
   ```

   Or update `src/main/resources/application.properties`

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Backend API will be available at `http://localhost:8089`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Run development server**
   ```bash
   npm run dev
   ```

Frontend will be available at `http://localhost:3000`
---

### User Registration

New users can register through the application.

Public registration is restricted to the `PATIENT` role.
---

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register - Register new user
POST /api/auth/login    - Login and get JWT token
```

### Patient Endpoints

```http
GET  /api/bookings/lab-tests          - View available lab tests
GET  /api/bookings/slots              - View available time slots
POST /api/bookings                    - Create a booking
GET  /api/bookings                    - View my bookings
GET  /api/reports/{sampleId}          - Get test report
GET  /api/samples/{id}/estimation     - Get processing time estimate
GET /api/bookings/{id}                 - View Booking Details
```

### Technician Endpoints

```http
GET /api/samples/queue/{state}    - View sample queue by state
PUT /api/samples/{id}/status      - Update sample status
POST /api/reports                 - Create test report
```

### Admin Endpoints

```http
POST /api/admin/lab-tests             - Create lab test
POST /api/admin/time-slots            - Create time slot
PUT  /api/admin/time-slots/{id}       - Update slot capacity
```

### Laboratory Endpoints

```http
GET    /api/laboratories
GET    /api/laboratories/active
GET    /api/laboratories/{id}
POST   /api/laboratories
PUT    /api/laboratories/{id}
DELETE /api/laboratories/{id}
```


For complete API documentation with request/response examples, visit the OpenAPI documentation at `http://localhost:8089/swagger-ui/index.html` when running the application.

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Security:** Spring Security + JWT
- **Database:** MySQL 8.x with Spring Data JPA
- **Build Tool:** Maven
- **Additional:** Lombok, Bean Validation, iText PDF, SpringDoc OpenAPI


### Frontend
- **Framework:** React 19
- **Build Tool:** Vite
- **Styling:** Modern CSS3

---

## 📁 Project Structure

```
lab-test-booking-system/
├── src/main/java/com/labtest/
│   ├── config/           # Security, JWT, async configuration
│   ├── controller/       # REST API controllers
│   ├── dto/              # Request/Response objects
│   ├── entity/           # JPA entities
│   ├── exception/        # Custom exceptions & global handler
│   ├── repository/       # Data access layer
│   ├── security/         # JWT authentication & security
│   ├── service/          # Business logic
│   └── util/             # Helper utilities
├── frontend/
│   └── src/
│       ├── components/   # React components
│       ├── services/     # API service layer
│       └── styles/       # CSS styles
└── pom.xml              # Maven configuration
```

---

## 🔒 Security

- JWT-based authentication with secure token generation
- Role-based access control (RBAC)
- Password hashing with BCrypt
- Protected endpoints with Spring Security
- CORS configuration for frontend integration
- Patient data ownership validation
- Stateless JWT authentication

---

## 🎨 Key Highlights

- ✅ **Smart Queue Management** - Manage samples through the processing workflow
- ✅ **Real-time Estimation** - Calculate wait times based on queue position
- ✅ **State Machine** - Validated sample state transitions
- ✅ **Multi-laboratory Support** - Scalable to multiple lab locations
- ✅ **RESTful API** - Clean, well-documented API design
- ✅ **Responsive UI** - Modern, mobile-friendly interface
- ✅ **Optimistic Locking** - Handle concurrent time-slot booking updates

---

## ⚠️ Disclaimer

This project is developed for educational and portfolio purposes.

The laboratory test results generated by the application are simulated data and are not real medical test results.


## 📝 License

This project is open source and available for educational purposes.

---

## 👤 Author

**Aditya Sisodiya**

- GitHub: [@heyyadiii](https://github.com/heyyadiii)

---

<div align="center">

**⭐ Star this repo if you find it helpful!**

Made with ❤️ using Spring Boot and React

</div>