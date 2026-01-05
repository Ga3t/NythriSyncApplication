# NutriSync

NutriSync is a microservices-based application designed for tracking daily calorie and macronutrient intake.  
It helps users monitor their diet to achieve goals such as weight loss, muscle gain, or weight maintenance.  
The system provides food logging, detailed nutrition analytics, and integrations with external APIs for accurate food data retrieval.

---

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)

---

## Overview

NutriSync allows users to track their meals and monitor nutritional intake throughout the day.  
By combining food logging, analytics, and barcode scanning, the application offers a comprehensive solution for maintaining a balanced diet.  

Users can register, set personal goals, record meals, and update their body weight.  
The system provides detailed insights into calorie and macronutrient consumption (proteins, fats, carbohydrates), as well as micronutrients such as sugar, fiber, and cholesterol.

A companion **mobile application** (built with Kotlin) provides a user-friendly interface and visual reports.  
Future plans include developing an **iOS application** written in Swift.  
Mobile app repository: [NutriSync Android App](https://github.com/Ga3t/NutriSync)

---

## Architecture

### Microservices Overview

The project is built using a **microservices architecture**, with each service responsible for a distinct area of functionality:

- **UserService**  
  Manages user registration, authentication, and profile information.  
  Includes goal management (weight loss, gain, or maintenance) and body weight updates.  
  Implements **Spring Security** and **JWT** for secure access control.

- **FoodDiaryService**  
  Handles daily meal logging and nutrient calculations.  
  - Records meals and food entries in the database  
  - Calculates calorie and macronutrient intake  
  - Supports barcode scanning for food items  

- **AnalyticsService**  
  Provides in-depth nutrition analysis and health metrics:  
  - Tracks consumption of sugar, fiber, cholesterol, and other nutrients  
  - Generates reports used in the mobile application  
  - Communicates asynchronously with **FoodDiaryService** through **Kafka**

- **IntegrationService**  
  Integrates with external APIs for food data retrieval:  
  - **OpenFoodFacts API** for scanning and identifying barcode-based products  
  - **FoodSecret API** for searching non-barcode food items  
  Uses **Redis** to cache frequently requested food data (e.g., “banana”) to improve performance and reduce API calls.

- **API Gateway**  
  Routes requests from clients (web or mobile) to the appropriate microservices and manages authentication.

---

### Asynchronous Communication

- **Kafka** is used for inter-service communication, ensuring reliable message delivery between the analytics and food diary services.

---

### Caching and Performance

- **Redis** is used for caching food information and API responses, reducing external API requests and improving response time.

---

### Scalability and Orchestration

- The application is containerized and can be deployed with **Kubernetes** for scalability and resilience.  
- **Spring Cloud** handles service discovery, configuration, and fault tolerance.

---

## Tech Stack

### Backend
- **Language:** Java 21  
- **Build Tool:** Maven  
- **Framework:** Spring Boot (Web, Security, Cloud)  
- **Authentication:** JWT  
- **Asynchronous Messaging:** Kafka  
- **Database:** PostgreSQL  
- **ORM:** Hibernate  
- **Caching:** Redis  
- **Architecture:** Microservices, Clean Architecture  
- **Utilities:** Lombok  

### Mobile Application
- **Language:** Kotlin  
- **Framework:** Android SDK  
- **Repository:** [NutriSync Android App](https://github.com/Ga3t/NutriSync)  
- **Planned:** iOS version using Swift  

### Infrastructure
- **API Gateway:** Spring Cloud Gateway  
- **Kafka:** Asynchronous inter-service communication  
- **Redis:** Caching of food and nutrition data  
- **Kubernetes:** Container orchestration and scaling  
- **Nginx (optional):** Load balancing and reverse proxying  

---

## Integrations
- **OpenFoodFacts API:** Retrieve detailed food information via barcode scanning  
- **FoodSecret API:** Search for foods without barcodes  
