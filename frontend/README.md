# NutriSync Frontend

Angular frontend application for the NutriSync calorie tracking microservices application.

## Prerequisites

- Node.js (v18 or higher)
- npm (v9 or higher)
- Angular CLI (v17 or higher)

## Installation

1. Install Node.js dependencies:
```bash
npm install
```

2. Install Angular CLI globally (if not already installed):
```bash
npm install -g @angular/cli
```

## Development

Run the development server:
```bash
ng serve
```

Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Build

Build for production:
```bash
ng build --configuration production
```

The build artifacts will be stored in the `dist/` directory.

## Configuration

Update the API Gateway URL in `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8090' // Update with your API Gateway URL
};
```

For production, update `src/environments/environment.prod.ts` with your production API Gateway URL.

## Features

- **Authentication**: Login and registration with JWT token management
- **Dashboard**: Main page showing daily calorie intake, macronutrients, and water consumption
- **Food Logging**: Add meals and track food items
- **Calendar View**: View calorie consumption over time
- **Analytics**: Weekly and custom date range reports
- **Profile Management**: Set and update user details, goals, and weight tracking

## Project Structure

```
src/
├── app/
│   ├── core/                    # Core functionality
│   │   ├── guards/              # Route guards
│   │   ├── interceptors/        # HTTP interceptors
│   │   ├── models/              # Data models
│   │   └── services/            # Core services
│   ├── features/                # Feature modules
│   │   ├── auth/                # Authentication module
│   │   └── dashboard/           # Dashboard module
│   ├── app.component.*          # Root component
│   └── app-routing.module.ts    # Root routing
├── environments/                # Environment configurations
└── styles.scss                  # Global styles
```

## API Integration

The frontend integrates with the following backend endpoints via the API Gateway:

- `/auth/*` - Authentication endpoints
- `/calapp/*` - Calorie tracking endpoints
- `/userdetails/*` - User profile endpoints
- `/analyse/*` - Analytics endpoints
- `/product/*` - Product search endpoints
- `/foodsecret/*` - Food search endpoints

## Technologies Used

- Angular 17
- Angular Material
- RxJS
- TypeScript
- SCSS

## License

This project is part of the NutriSync application.













