# NythriSync

NythriSync is a comprehensive calorie counting application designed as a secure and effective system for tracking nutritional intake and managing dietary goals. The application requires constant Internet access to provide real-time synchronization and access to up-to-date nutritional information.

## Overview

The NythriSync calorie counting app was developed as a secure and effective system requiring constant Internet access. It was created using the latest technologies, which simplified the app development process and facilitated further modification and system operation. Designed as a microservice application using layered architecture, NythriSync provides a robust, scalable, and maintainable solution for calorie tracking.

## Architecture

NythriSync follows a **microservices architecture** with a **layered architecture** approach, ensuring:

- **Modularity**: Each service operates independently, making the system easy to modify and extend
- **Scalability**: Services can be scaled individually based on demand
- **Maintainability**: Clear separation of concerns simplifies development and debugging
- **Security**: Secure communication between services with proper authentication and authorization

## Technology Stack

The application utilizes modern technologies and best practices:

- **Backend**: Spring Boot microservices (Java)
- **Frontend**: Angular web application
- **Mobile**: Android application (Kotlin)
- **Infrastructure**: Docker, Docker Compose, API Gateway

## Project Structure

```
NythriSync/
├── androidApp/          # Android mobile application
├── backend/             # Backend microservices
│   └── calApp/
│       ├── api-gateway/        # API Gateway service
│       ├── user-service/       # User management service
│       ├── calorie-service/    # Calorie tracking service
│       ├── api-conection-service/  # API connection service
│       └── FoodSecretApiConnection/ # External API integration
├── frontend/            # Angular web application
└── README.md           # This file
```

## Download Mobile App

Download the NythriSync Android mobile application:

**[Download NythriSync APK](androidApp/NytriSync/app/build/outputs/apk/debug/app-debug.apk)**

> **Note**: After downloading, you may need to enable "Install from unknown sources" in your Android device settings to install the APK file.

## Features

- Real-time calorie tracking
- Nutritional information database
- User authentication and profile management
- Synchronized data across devices
- Secure data transmission
- Modern and intuitive user interface

## Requirements

- **Internet Connection**: Required for all features
- **Android**: Android device (for mobile app)
- **Web Browser**: Modern browser for web application

## Getting Started

### Backend Services

Navigate to the `backend/calApp` directory and refer to the README there for backend setup instructions.

### Frontend

Navigate to the `frontend` directory and refer to the README there for frontend setup instructions.

### Mobile App

Download the APK file using the link above and install it on your Android device.

## Security

NythriSync implements robust security measures to protect user data:

- Secure authentication and authorization
- Encrypted data transmission
- Secure API communication
- Protected user information

## Development

The application is built with modern development practices:

- Modular microservices architecture
- RESTful API design
- Clean code principles
- Comprehensive error handling
- Scalable infrastructure

## License

[Add your license information here]

## Contact

[Add contact information here]

