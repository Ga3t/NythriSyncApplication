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
- **Node.js**: Version 18 or higher (for frontend)
- **npm**: Version 9 or higher (for frontend)

## User Guide

### How to Run the Frontend Application

Follow these step-by-step instructions to run the NythriSync web application on your computer:

#### Step 1: Install Prerequisites

Before running the frontend, make sure you have the required software installed:

1. **Install Node.js**:
   - Visit [nodejs.org](https://nodejs.org/)
   - Download and install Node.js version 18 or higher
   - This will also install npm (Node Package Manager)

2. **Verify Installation**:
   - Open a terminal or command prompt
   - Run: `node --version` (should show v18 or higher)
   - Run: `npm --version` (should show v9 or higher)

#### Step 2: Navigate to Frontend Directory

1. Open a terminal or command prompt
2. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

#### Step 3: Install Dependencies

1. Install all required packages by running:
   ```bash
   npm install
   ```
   - This may take a few minutes as it downloads all necessary dependencies
   - Wait for the installation to complete

#### Step 4: Install Angular CLI (if not already installed)

1. Install Angular CLI globally:
   ```bash
   npm install -g @angular/cli
   ```

#### Step 5: Start the Development Server

1. Start the frontend application:
   ```bash
   ng serve
   ```
   or alternatively:
   ```bash
   npm start
   ```

2. Wait for the compilation to complete. You should see a message like:
   ```
   ✔ Compiled successfully.
   ** Angular Live Development Server is listening on localhost:4200
   ```

#### Step 6: Access the Application

1. Open your web browser (Chrome, Firefox, Edge, etc.)
2. Navigate to: `http://localhost:4200`
3. The NythriSync web application should now be running

**Note**: The application will automatically reload if you make any changes to the source files. Keep the terminal window open while using the application.

### How to Download and Install the Android App

Follow these step-by-step instructions to install the NythriSync mobile application on your Android device:

#### Step 1: Download the APK File

1. Click on the download link below:
   **[Download NythriSync APK](releases/NythriSync-debug.apk)**

2. The download will start automatically
3. Wait for the download to complete
4. The APK file (`app-debug.apk`) will be saved to your device's Downloads folder

**Alternative**: If you're on a computer, download the file and transfer it to your Android device via USB, email, or cloud storage.

#### Step 2: Enable Installation from Unknown Sources

Since the app is not from the Google Play Store, you need to allow installation from unknown sources:

1. **On your Android device**, go to **Settings**
2. Navigate to **Security** or **Privacy** (location may vary by device)
3. Look for **"Install unknown apps"** or **"Unknown sources"**
4. Select your browser or file manager (depending on where you downloaded the file)
5. Enable **"Allow from this source"** or toggle the switch to **ON**

**Note**: On newer Android versions (Android 8.0+), you may need to allow installation for the specific app you used to download the file (e.g., Chrome, Files app).

#### Step 3: Install the APK

1. **Using File Manager**:
   - Open the **Files** or **File Manager** app on your device
   - Navigate to the **Downloads** folder
   - Tap on the `app-debug.apk` file
   - Tap **Install** when prompted
   - Wait for the installation to complete

2. **Using Downloads Notification**:
   - Pull down the notification bar
   - Tap on the download notification for `app-debug.apk`
   - Tap **Install** when prompted

#### Step 4: Launch the App

1. After installation is complete, you'll see an **"Open"** button
2. Tap **Open** to launch NythriSync immediately, or
3. Find the **NythriSync** app icon in your app drawer and tap it to open

#### Step 5: Grant Permissions (if needed)

1. When you first open the app, it may request certain permissions (Internet access, etc.)
2. Review the permissions and tap **Allow** to continue
3. You're now ready to use NythriSync on your Android device!

**Troubleshooting**:

- **"Install blocked"**: Make sure you've enabled "Install from unknown sources" for your browser/file manager
- **"App not installed"**: The file may be corrupted. Try downloading again
- **"Parse error"**: The APK file might be incomplete. Delete it and download again

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

