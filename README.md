# Parking Management - Jump Park

Android parking management application developed in Kotlin using Clean Architecture, MVVM, and SOLID principles.

## Technologies Used

- **Kotlin** - Programming language
- **Kotlin Multiplatform (KMP)** - Code sharing between Android and Web
- **Jetpack Compose** - UI framework
- **Room Database** - Local database (Android)
- **Ktor** - HTTP client for API requests
- **Koin** - Dependency injection
- **Coroutines & Flow** - Asynchronous programming
- **Navigation Compose** - Screen navigation
- **Material Design 3** - Design system
- **MockK** - Mocking framework for testing
- **Turbine** - Library for testing Flows

## Architecture

The project follows **Clean Architecture** principles with the following layers:

- **Presentation**: ViewModels and Screens (Compose)
- **Domain**: Use Cases, Models, and Repository Interfaces
- **Data**: Repository implementations, API Service, Database, and Mappers

## Features

- ✅ User login
- ✅ Home screen with statistics (vehicles in the yard and payments)
- ✅ Vehicle entry
- ✅ List of vehicles in the yard
- ✅ Vehicle details and exit with automatic value calculation
- ✅ Work session closing
- ✅ Synchronization with Operational API
- ✅ Local data persistence

## Environment Setup

### Prerequisites

- Android Studio Narwhal Feature Drop | 2025.1.2 Patch 2 or higher
- JDK 11 or higher
- Android SDK 24+ (minSdk)
- Android emulator or physical device

### Steps to Run

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd GestodeEstacionamento
   ```

2. **Open the project in Android Studio**
   - File > Open > Select the project folder

3. **Sync dependencies**
   - Gradle will automatically download all dependencies

4. **Configure the user account**
   - Access: https://teste-admin.jumppark.com.br
   - Create a user account
   - Make sure you have only **one establishment** linked to the account
   - In the establishment, create at least **one parking pricing table** (Business > Parking pricing table)

5. **Run the application**
   - Connect a device or start an emulator
   - Click Run (Shift+F10) or use the play button

## How to Test

### 1. Login
- Open the application
- Enter the email and password of the account created on the site
- Click “Sign In”
- The app will automatically synchronize data (pricing tables and payment methods)

### 2. Home Screen
- View total vehicles in the yard
- View payments grouped by payment method
- View total payment amount
- Use the sync button (refresh icon) to update API data

### 3. Vehicle Entry
- Click “Vehicle Entry”
- Fill in:
  - Plate (ex: ABC1234)
  - Model (ex: Honda Civic)
  - Color (ex: White)
  - Select a pricing table
- Click “Register Entry”
- The vehicle will be saved locally and appear in the list

### 4. Vehicle List in the Yard
- Click “Vehicle List in the Yard”
- View all vehicles currently in the yard
- Click a vehicle to see details

### 5. Vehicle Exit
- On the vehicle detail screen:
  - View vehicle information
  - The stay value will be calculated automatically based on the pricing table
  - Select a payment method
  - Click “Process Exit”
  - Confirm the action in the dialog
  - The vehicle will be removed from the yard and payment recorded

### 6. Close Session
- On the home screen, click “Close Session”
- Confirm the action in the dialog
- All local data will be deleted
- You will be logged out and returned to the login screen

## Database Structure

The local database uses Room and includes the following tables:

- **vehicles**: Stores vehicle information
- **price_tables**: Stores pricing tables synchronized from the API
- **payment_methods**: Stores payment methods synchronized from the API
- **payments**: Stores completed payments

See the full UML diagram in `DATABASE_UML.md`

## API

The application integrates with the Jump Park Operational API:

- **Base URL**: `https://dev.app.jumpparkapi.com.br/api`
- **Endpoints used**:
  - `POST /user/login` - User login
  - `GET /{userId}/establishment/{establishmentId}/sync/manual` - Manual synchronization
  - `POST /{userId}/establishment/{establishmentId}/session/close/{sessionId}` - Close session

## Theme Colors

The application uses Jump Park’s visual identity colors:

- **Green**: `#4CAF50` (Primary)
- **Dark Blue**: `#1A237E` (Secondary)
- **White**: `#FFFFFF` (Background)

## Unit Tests

The project includes a unit testing suite covering the main application components. Currently, **27 tests are passing** successfully.

### Implemented and Functional Tests

1. **CalculateParkingFeeUseCaseTest** (8 tests) — Tests parking fee calculation logic:
   - Initial tolerance calculation
   - Application of “up to” and “from” rules
   - Additional period calculation
   - Maximum value enforcement
   - Partial period rounding

2. **LoginViewModelTest** (8 tests) — Tests authentication logic:
   - Initialization with empty state
   - Email and password updates
   - Required field validation
   - Successful login flow
   - Authentication error handling
   - Error clearing
   - Email whitespace trimming

3. **VehicleEntryViewModelTest** (7 tests) — Tests vehicle entry logic:
   - Initialization with empty state
   - Plate update and uppercase conversion
   - Model and color updates
   - Pricing table selection
   - Repository pricing table loading
   - Field whitespace trimming on registration

4. **VehicleDetailViewModelTest** (11 tests) — Tests vehicle details and exit logic:
   - Initialization with empty state
   - Vehicle loading and value calculation
   - Error display when vehicle not found
   - Value recalculation
   - Payment method selection
   - Exit validation
   - Successful exit processing
   - Date/time formatting
   - Error clearing
   - Success state reset
   - Zero-value exit allowed (within tolerance)

### Running Tests

To run all unit tests in Android Studio:

1. Right-click `app/src/test`
2. Select “Run 'Tests in test'”

Or via command line:

```bash
./gradlew test
```

To run a specific test:

```bash
./gradlew test --tests "com.example.gestodeestacionamento.domain.usecase.CalculateParkingFeeUseCaseTest"
```

### Test Dependencies

- **JUnit 4** - Testing framework
- **MockK** - Kotlin mocking framework
- **Turbine** - Flow testing
- **kotlinx-coroutines-test** - Coroutine testing
- **androidx.arch.core:core-testing** - ViewModel testing

## Kotlin Multiplatform (KMP)

The project is configured to support Kotlin Multiplatform, allowing code sharing between Android and Web.

### KMP Structure

```
app/src/
├── commonMain/          # Shared code between platforms
│   └── kotlin/.../platform/  # expect/actual abstractions
├── androidMain/         # Android-specific implementations
│   └── kotlin/.../platform/  # Android implementations
└── jsMain/              # Web-specific implementations
    └── kotlin/.../platform/  # Web implementations
```

### Implemented Expect/Actual Abstractions

1. **PlatformStorage** — Local storage:
   - Android: DataStore
   - Web: LocalStorage

2. **DatabaseFactory** — Database factory:
   - Android: Room Database
   - Web: LocalStorage (simplified)

3. **Navigation** — Navigation:
   - Android: Navigation Compose
   - Web: Navigation Compose

4. **ViewModel** — ViewModels:
   - Android: Koin ViewModel
   - Web: Koin ViewModel

### Running on Android

```bash
./gradlew :app:assembleDebug
```

### Running on Web

```bash
# Development build
./gradlew :app:jsBrowserDevelopmentWebpack

# Production build
./gradlew :app:jsBrowserProductionWebpack
```

Generated files will be located in:

- Development: `app/build/dist/js/developmentExecutable/`
- Production: `app/build/dist/js/productionExecutable/`

For more details about KMP migration:

- `KMP_MIGRATION_GUIDE.md` — Migration guide
- `README_KMP.md` — Complete KMP documentation

## APK Generation

To generate the debug APK:

```bash
./gradlew assembleDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

To generate the release APK:

```bash
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/app-release.apk`

## Important Notes

1. **Single Session**: The API allows only one open session per user per establishment. If issues occur, close the session directly on the website under “Session records > Close session”.

2. **Single Establishment**: For correct testing, the account must have only one linked establishment.

3. **Pricing Table**: At least one parking pricing table must be created before using the app.

4. **Local Data**: All vehicles and payments are stored locally. Closing the session deletes all data.

## Database UML Diagram

Refer to `DATABASE_UML.md` to view the full database diagram.

## ANDROID PROJECT SCREENSHOTS

<img width="300" height="700" alt="Screenshot_20251220_002039" src="https://github.com/user-attachments/assets/e20fd059-150b-4d2b-9649-7b79e82c0ecc" />
<img width="300" height="700" alt="Screenshot_20251220_192254" src="https://github.com/user-attachments/assets/18531f2c-212e-4cd9-8073-4b1a701139f5" />
<img width="300" height="700" alt="Screenshot_20251220_192241" src="https://github.com/user-attachments/assets/65eb322d-b871-490d-9d6a-90e5df0be966" />
<img width="300" height="700" alt="Screenshot_20251220_192232" src="https://github.com/user-attachments/assets/acc2d534-13f0-472b-957a-981940f0acfa" />
<img width="300" height="700" alt="Screenshot_20251220_202706" src="https://github.com/user-attachments/assets/212b5dfb-da13-42f5-9df4-2b6be1f8281f" />
<img width="300" height="700" alt="Screenshot_20251220_202908" src="https://github.com/user-attachments/assets/dfba2a20-dbdc-4948-ad45-70c12f1c6435" />
<img width="300" height="700" alt="Screenshot_20251220_002051" src="https://github.com/user-attachments/assets/609727d8-c4bd-4fb5-8d4c-db8809d9cfa5" />
<img width="600" height="700" alt="Screenshot_1" src="https://github.com/user-attachments/assets/a8d927a7-e6f9-414c-8b2b-2dbfdd61e28b" />
<img width="1000" height="600" alt="image" src="https://github.com/user-attachments/assets/909bc89a-5df8-45c2-8f34-f4068f9c0646" />
