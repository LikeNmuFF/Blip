# ChatApp Kotlin - Native Android Chat Application

## Overview
A native Android chat application built with Kotlin and Jetpack Compose, connecting to a Flask backend with REST API and WebSocket support.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Networking**: Retrofit + OkHttp
- **WebSocket**: Socket.IO Client
- **State Management**: ViewModel + StateFlow
- **Storage**: DataStore (secure token storage)
- **Navigation**: Navigation Compose

## Project Structure
```
app/src/main/java/com/chatapp/
├── MainActivity.kt                 # App entry point with navigation
├── data/
│   ├── model/
│   │   └── Models.kt              # Data classes (User, Room, Message)
│   ├── network/
│   │   ├── ApiConfig.kt           # Backend URL configuration
│   │   ├── ApiService.kt          # Retrofit API interface
│   │   ├── RetrofitClient.kt      # HTTP client setup
│   │   └── SocketService.kt       # WebSocket client
│   └── local/
│       └── TokenStorage.kt        # DataStore for token persistence
└── ui/
    ├── AuthViewModel.kt           # Authentication state management
    ├── RoomViewModel.kt           # Room/message state management
    ├── ViewModelFactory.kt        # ViewModel factories
    ├── login/
    │   └── LoginScreen.kt         # Login UI
    ├── register/
    │   └── RegisterScreen.kt      # Registration UI
    ├── rooms/
    │   └── RoomListScreen.kt      # Room list UI
    └── chat/
        └── ChatRoomScreen.kt      # Real-time chat UI
```

## Setup & Build

### Prerequisites
1. **Android Studio**: https://developer.android.com/studio
2. **JDK 17**: https://adoptium.net/
3. **Android SDK**: API 24+ (included with Android Studio)

### Steps

1. **Open in Android Studio**
   ```
   File > Open > Select the ChatAppKotlin folder
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

3. **Configure Backend URL**
   
   Edit `app/src/main/java/com/chatapp/data/network/ApiConfig.kt`:
   
   ```kotlin
   // For Android emulator:
   const val BASE_URL = "http://10.0.2.2:5000"
   const val SOCKET_URL = "http://10.0.2.2:5000"
   
   // For physical device (same WiFi):
   const val BASE_URL = "http://YOUR_MACHINE_IP:5000"
   ```

4. **Run the App**
   - Click the green "Run" button (▶️) in Android Studio
   - Select an emulator or connected device
   - The app will build and install automatically

### Build APK

**Debug APK:**
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK:**
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

## Features
✅ User registration & login with JWT  
✅ Token persistence with DataStore  
✅ Room list with descriptions & icons  
✅ Real-time messaging via WebSocket  
✅ Message history loading  
✅ Auto-reconnection  
✅ Material 3 design  
✅ Responsive Compose UI  
✅ Clean architecture  

## Troubleshooting

### Connection Issues
- Ensure Flask backend is running: `python app.py`
- Check URL in `ApiConfig.kt` matches your setup
- For emulators, use `10.0.2.2`, NOT `localhost`
- For physical devices, use your machine's IP (find with `ipconfig`)

### Build Errors
```bash
./gradlew clean
./gradlew build
```

### WebSocket Not Connecting
- Verify Socket.IO is enabled in Flask backend
- Check network logs in Logcat
- Ensure `usesCleartextTraffic="true"` in AndroidManifest.xml

## Backend Requirements
- Flask backend running on port 5000
- CORS enabled (`cors_allowed_origins="*"`)
- JWT authentication
- WebSocket support via Flask-SocketIO

See `/mnt/c/Users/Hp/Documents/Python Projects/Python_WebApp/chatApp/` for backend code.
