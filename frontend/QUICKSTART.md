# NutriSync Frontend - Quick Start Guide

## Getting Started

### Step 1: Install Prerequisites

1. **Install Node.js** (v18 or higher)
   - Download from: https://nodejs.org/
   - Verify installation: `node --version`

2. **Install Angular CLI globally**
   ```bash
   npm install -g @angular/cli
   ```

### Step 2: Install Dependencies

Navigate to the frontend directory and install dependencies:

```bash
cd calApp/frontend
npm install
```

This will install all required packages including Angular, Angular Material, and other dependencies.

### Step 3: Configure API Gateway URL

Update the API Gateway URL in `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8090' // Your API Gateway URL
};
```

### Step 4: Start Development Server

```bash
ng serve
```

The application will be available at `http://localhost:4200/`

## Features Overview

### 1. Authentication
- **Login**: `/login` - Login with username/email and password
- **Registration**: `/register` - Create a new account

### 2. Dashboard (`/dashboard/main`)
- View daily calorie intake and goals
- Track macronutrients (proteins, carbs, fats)
- Monitor water consumption
- Add meals for breakfast, lunch, dinner, and snacks
- View meals by date

### 3. Calendar View (`/dashboard/calendar`)
- View calorie consumption over time
- Calendar visualization for selected year
- Track daily meal logging

### 4. Analytics (`/dashboard/analytics`)
- Weekly reports
- Custom date range reports
- Detailed breakdown of nutrients and calories

### 5. Profile (`/dashboard/profile`)
- Set user details (height, weight, age, sex, activity level, goal)
- Update profile information
- Log weight updates

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/              # Core services, guards, interceptors
│   │   │   ├── guards/        # Route guards (AuthGuard)
│   │   │   ├── interceptors/  # HTTP interceptors (Auth, Error)
│   │   │   ├── models/        # TypeScript interfaces/models
│   │   │   └── services/      # Core services (Auth, API)
│   │   ├── features/
│   │   │   ├── auth/          # Authentication module
│   │   │   └── dashboard/     # Dashboard module
│   │   ├── app.module.ts      # Root module
│   │   └── app-routing.module.ts
│   ├── environments/          # Environment configurations
│   └── styles.scss            # Global styles
├── angular.json               # Angular CLI configuration
├── package.json               # Dependencies
└── tsconfig.json              # TypeScript configuration
```

## API Integration

The frontend integrates with your microservices backend through the API Gateway (port 8090):

- **Authentication**: `/auth/login`, `/auth/registration`, `/auth/refreshtoken`
- **Calorie Tracking**: `/calapp/savemeal`, `/calapp/showmeal`, `/calapp/mainpage`, etc.
- **User Details**: `/userdetails/setuserdetails`, `/userdetails/info`, etc.
- **Analytics**: `/analyse/reports`, `/analyse/reportforweek`
- **Food Search**: `/product/search`, `/foodsecret/search`

## Build for Production

```bash
ng build --configuration production
```

Output will be in the `dist/nutrisync-frontend/` directory.

## Troubleshooting

### Port 4200 already in use
```bash
ng serve --port 4201
```

### API connection errors
- Verify API Gateway is running on port 8090
- Check CORS settings on backend
- Verify API Gateway URL in `environment.ts`

### Missing dependencies
```bash
npm install
```

### Angular CLI not found
```bash
npm install -g @angular/cli@latest
```

## Next Steps

1. Customize the UI theme in `src/styles.scss`
2. Add more features as needed
3. Configure production environment URL
4. Set up CI/CD pipeline
5. Add unit tests

## Support

For issues or questions, refer to the main README.md or the backend API documentation.













