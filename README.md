# 🍱 Tiffino – Online Tiffin & Meal Management System

Tiffino is a full-stack food and tiffin management application designed to make
daily meal ordering, subscription management, delivery tracking, and user
management simple and convenient.

The application provides separate functionalities for customers and
administrators, allowing users to explore meals, subscribe to tiffin plans,
manage orders, and track their meal services.

---
## 📌 Project Overview

Tiffino is designed to provide a convenient platform for customers to manage their daily meals and tiffin subscriptions.

### Main Modules

- 👤 User Management
- 🔐 Authentication & Security
- 🍱 Meal / Tiffin Management
- 🛒 Order Management
- 📅 Subscription Management
- 🎁 Gift Card Management
- 🛠️ Admin Management
- 🤖 AI Chat Assistant
- 💬 Real-Time Communication
- 🗄️ Database Management

---

# 🚀 Features

## 👤 User Features

- User registration
- User login
- Secure authentication
- User profile management
- Password management
- OTP verification
- Browse available meals
- View meal details
- Place food/tiffin orders
- View order history
- Manage subscriptions
- Cancel/manage subscriptions
- Gift card functionality
- AI chat assistant

---

## 🛠️ Admin Features

- Admin login
- Admin dashboard
- Manage users
- Manage meals
- Manage tiffin plans
- Manage orders
- Manage subscriptions
- Manage gift cards
- Monitor application data
- Manage application services

---

# 🔐 Authentication & Security

The application uses Spring Security for authentication and authorization.

### Security Features

- User authentication
- Admin authentication
- Role-based authorization
- Protected REST APIs
- Secure password handling
- Token-based authentication
- OTP verification
- Access control

### User Roles

```text
USER
ADMIN
SUPER_ADMIN
🍱
## 🚀 Features

### 👤 User Features
- User registration and login
- Secure authentication
- Browse available tiffin/meal plans
- View meal details
- Place food orders
- Manage subscriptions
- View order history
- Manage user profile
- OTP-based verification
- Gift card functionality

### 🛠️ Admin Features
- Admin dashboard
- Manage users
- Manage meals and tiffin plans
- Manage orders
- Manage subscriptions
- Manage gift cards
- User and service monitoring
- Application management

### 🤖 AI Features
- AI-powered chat interface
- User interaction through AI assistant
- AI-based query handling
- Real-time chat interface

### 🔐 Security
- Spring Security
- Authentication and authorization
- Role-based access
- Secure API endpoints
- Token-based authentication

---

## 🏗️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- REST APIs
- WebSocket

### Frontend
- HTML
- CSS
- JavaScript

### Database
- MySQL

### Development Tools
- IntelliJ IDEA
- Maven
- Git
- GitHub
- Postman

---

🍱 Tiffin / Meal Management

Users can browse different meal and tiffin options.

The system provides information such as:

Meal name
Meal description
Meal price
Meal category
Availability
Subscription options

Administrators can manage meal-related information.

🛒 Order Management

Users can place food/tiffin orders through the application.

Order Features
Create order
View order details
View order history
Track order status
User-specific orders
Admin order management
Order Status
PENDING
CONFIRMED
PREPARING
OUT_FOR_DELIVERY
DELIVERED
CANCELLED

----------------------------
📅 Subscription Management

Tiffino supports subscription-based meal services.

Users can:

View subscription plans
Subscribe to a plan
View active subscription
Manage subscription
Cancel subscription
View subscription history
Subscription Types
DAILY
WEEKLY
MONTHLY
-----------------------
🎁 Gift Card Module

The application provides gift card functionality.

Features
Gift card creation
Gift card validation
Gift card redemption
Gift card balance management
Gift card history

----------------------------
🤖 AI Chat Assistant

Tiffino includes an AI-powered chat interface that allows users to interact with an AI assistant.

AI Features
AI-powered conversations
User query processing
Chat interface
Backend AI API
Real-time interaction
Example API
POST /api/ai/ask

Example request:

{
  "message": "Show me available tiffin plans"
}
💬 WebSocket / Real-Time Communication

The application includes WebSocket functionality for real-time communication.

WebSocket can be used for:

Real-time chat
Live notifications
Real-time application updates
User communication

------------------------------
🛠️ Technology Stack
Backend
Java
Spring Boot
Spring MVC
Spring Security
Spring Data JPA
Hibernate
REST API
WebSocket
Maven
Frontend
HTML5
CSS3
JavaScript
Database
MySQL
JPA
Hibernate
Development Tools
IntelliJ IDEA
Git
GitHub
Postman
MySQL Workbench
--------------------------
🗄️ Database

Tiffino uses MySQL as the primary relational database.
Spring Data JPA and Hibernate are used for database interaction and ORM.
Database Name
tiffino
------------------------------
📊 Database Architecture

The database stores information related to:

Users
  │
  ├── Orders
  │
  ├── Subscriptions
  │
  ├── Gift Cards
  │
  └── Authentication

Meals
  │
  └── Orders

Subscriptions
  │
  └── Users

Orders
  │
  ├── Users
  └── Meals
--------------------
📋 Main Database Modules
👤 Users

Stores user information.

Typical information:
User ID
Name
Email
Password
Phone Number
Role
Account Status
Created Date

--------------------------
🍱 Meals

Stores available meal/tiffin information.

Typical information:
Meal ID
Meal Name
Description
Price
Category
Availability
Created Date

--------------------------
🛒 Orders

Stores customer order information.

Typical information:
Order ID
User ID
Meal ID
Order Date
Quantity
Total Amount
Order Status

---------------------------
📅 Subscriptions

Stores customer subscription information.

Typical information:
Subscription ID
User ID
Plan Name
Start Date
End Date
Price
Status

------------------------
🎁 Gift Cards

Stores gift card information.

Typical information:
Gift Card ID
Gift Card Code
Amount
Balance
Expiry Date
Status
User ID

-----------------------------
🔗 Database Relationships
User → Orders

One user can place multiple orders.

USER 1 ───────── * ORDER
User → Subscription

A user can have multiple subscription records.

USER 1 ───────── * SUBSCRIPTION
User → Gift Card

Gift cards can be associated with users.

USER 1 ───────── * GIFT_CARD
Meal → Orders

A meal can be associated with multiple orders.

MEAL 1 ───────── * ORDER
⚙️ Database Configuration

Update:

src/main/resources/application.properties

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/tiffino
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

Replace YOUR_PASSWORD with your local MySQL password.
--------------
🗂️ Project Structure
tiffino_main/
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   │
│   │   │   └── com/
│   │   │       └── tiffino/
│   │   │           └── tiffino/
│   │   │               │
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── util/
│   │   │               └── TiffinoMainApplication.java
│   │   │
│   │   └── resources/
│   │       │
│   │       ├── static/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
└── README.md

----------
🏛️ Application Architecture

Tiffino follows a layered architecture.

                  CLIENT
                    │
                    ▼
               CONTROLLER
                    │
                    ▼
                 SERVICE
                    │
                    ▼
                REPOSITORY
                    │
                    ▼
                 DATABASE
Controller Layer

Handles HTTP requests and REST APIs.

Service Layer

Contains business logic.

Repository Layer

Handles database operations using Spring Data JPA.

Database Layer

MySQL stores application data.

🔌 REST API Modules

The application provides REST APIs for different modules.

Authentication APIs
/register
/login
/verify-otp
User APIs
/user
/user/profile
/user/update
Meal APIs
/meals
/meals/{id}
Order APIs
/orders
/orders/{id}
Subscription APIs
/subscriptions
/subscriptions/{id}
Gift Card APIs
/gift-cards
/gift-cards/redeem
AI APIs
POST /api/ai/ask

API paths may vary depending on the controller implementation.

📮 API Testing

APIs can be tested using:

Postman
Swagger UI
Browser for GET APIs

Example:

POST /api/ai/ask

Request:

{
  "message": "What are today's available meals?"
}
🔑 Configuration & Environment Variables

Sensitive information should not be committed to GitHub.

Examples:

Database Password
JWT Secret
AI API Key
Email Password
Third-Party API Keys
Private Tokens

Use environment variables wherever possible.
----------------

▶️ How to Run the Project

Step 1 – Clone Repository
git clone https://github.com/AsmitaGadekar28/Tiffino-Application.git
Step 2 – Open in IntelliJ IDEA

Open the cloned project using IntelliJ IDEA.

Step 3 – Configure MySQL

Create the database:

CREATE DATABASE tiffino;

Then update:

src/main/resources/application.properties

with your MySQL username and password.

Step 4 – Install Dependencies

Run:

mvn clean install

Maven will download all required dependencies.

Step 5 – Run Application

Run:

TiffinoMainApplication.java

Or use:

mvn spring-boot:run
--------------------------
🌐 Application URL

After starting the Spring Boot application:

http://localhost:8080

-----------------------------
🧪 Testing

Run the test suite:

mvn test

Test files are located under:

src/test/

-------------------------------
📸 Screenshots

Add screenshots of the application here.

Example:

## Home Page

![Home Page](screenshots/home.png)

## Login Page

![Login Page](screenshots/login.png)

## User Dashboard

![Dashboard](screenshots/dashboard.png)

## AI Chat

![AI Chat](screenshots/ai-chat.png)

## Admin Dashboard

![Admin Dashboard](screenshots/admin.png)
----------------------

🔒 Security Best Practices

Never commit sensitive information such as:

API Keys
Passwords
JWT Secrets
Database Credentials
Private Tokens

Use environment variables or local configuration files.

🚀 Future Enhancements
Online payment gateway
Razorpay / Stripe integration
Live food delivery tracking
Mobile application
Push notifications
Email notifications
SMS notifications
Advanced AI recommendations
AI-based meal recommendations
Customer reviews and ratings
Coupon management
Advanced admin analytics
Cloud deployment
Docker support
CI/CD pipeline
☁️ Deployment

The application can be deployed using cloud platforms such as:

AWS
Azure
Google Cloud
Render
Railway

The MySQL database can also be hosted using a cloud database service.


👩‍💻 Author
Asmita Gadekar

GitHub:

https://github.com/AsmitaGadekar28

Project Repository:

https://github.com/AsmitaGadekar28/Tiffino-Application

📄 License

This project is developed for educational and application development purposes.

⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.
