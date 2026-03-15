# Email OTP Verification System

A simple Email OTP Verification system built using **Spring Boot and Redis**.  
The application sends a one-time password (OTP) to a user's email for verification.

## 🚀 Features
- Generate secure 6-digit OTP
- Send OTP via email using Gmail SMTP
- Store OTP temporarily in Redis
- OTP expires automatically after 1 minute
- Verify OTP entered by the user
- Simple registration form UI

## 🛠 Tech Stack
- Java
- Spring Boot
- Redis
- JavaMailSender
- Gmail SMTP
- HTML
- Thymeleaf
- Maven
- Embedded Tomcat

## ⚙️ How It Works

1. User enters **name, email, and gender**
2. System generates a **6-digit OTP**
3. OTP is **sent to the user's email**
4. OTP is **stored in Redis with expiration**
5. User enters OTP on verification page
6. System validates OTP

If OTP matches → **Registration Successful**

If OTP is wrong → **OTP Mismatch**

If OTP expires → **OTP Expired**

## 📂 Project Structure
