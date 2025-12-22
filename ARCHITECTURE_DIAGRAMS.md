# Architecture Diagram Descriptions for Visualization

This document provides structured descriptions specifically formatted for creating architectural diagrams using AI visualization tools like Gemini, Mermaid, or PlantUML.

## Overall System Architecture - 3-Layer View

### Layer 1: Presentation Layer (UI)
**Components**:
- MainActivity (container)
- TranslationFragment (main screen)
- HistoryFragment (history view)
- SettingsFragment (settings)
- TranslationViewModel
- HistoryViewModel
- SettingsViewModel

**Visual representation**: Display as top layer with blue color scheme
**Connections**: ViewModels connect to Use Cases below

### Layer 2: Domain Layer (Business Logic)
**Components**:
- TranslateTextUseCase
- DetectLanguageUseCase
- GetTranslationHistoryUseCase
- SaveTranslationUseCase
- DeleteHistoryUseCase
- Translation (model)
- Language (model)

**Visual representation**: Display as middle layer with green color scheme
**Connections**: Use Cases connect to Repositories below

### Layer 3: Data Layer (Data Management)
**Components**:
- TranslationRepository
- HistoryRepository
- TranslationApiService (remote)
- TranslationDao (local)
- TranslationCache
- AppDatabase
- NetworkMonitor

**Visual representation**: Display as bottom layer with orange color scheme
**Connections**: 
- Repositories connect to external API (outside system)
- Repositories connect to local database

---

## Component-Level Architecture Diagram

### Main Components and Connections

#### User Interface Block
```
[User Device]
    ↓
[MainActivity]
    ├─→ [TranslationFragment]
    ├─→ [HistoryFragment]
    └─→ [SettingsFragment]
```

#### Translation Flow
```
[TranslationFragment]
    ↓ (observes)
[TranslationViewModel]
    ↓ (calls)
[TranslateTextUseCase]
    ↓ (uses)
[TranslationRepository]
    ├─→ [TranslationCache] (check cache)
    ├─→ [TranslationApiService] (if not cached)
    └─→ [HistoryRepository] (save result)
```

#### History Flow
```
[HistoryFragment]
    ↓ (observes)
[HistoryViewModel]
    ↓ (calls)
[GetTranslationHistoryUseCase]
    ↓ (uses)
[HistoryRepository]
    ↓ (queries)
[TranslationDao]
    ↓ (reads from)
[AppDatabase]
```

#### Data Sources
```
[TranslationRepository]
    ├─→ [Remote Data Source]
    │       └─→ [Google Translate API]
    └─→ [Local Data Source]
            ├─→ [Room Database]
            └─→ [In-Memory Cache]
```

---

## Detailed Data Flow Diagram

### Translation Request Sequence

**Step 1: User Input**
```
User enters text → TranslationFragment captures input
```

**Step 2: Language Selection**
```
User selects languages → Fragment updates ViewModel state
```

**Step 3: Translation Request**
```
ViewModel.translate(text, sourceLang, targetLang)
    ↓
TranslateTextUseCase.execute(request)
    ↓
Check TranslationCache
    ├─→ If found: Return cached translation
    └─→ If not found: Continue to API
```

**Step 4: API Call**
```
TranslationRepository.translate()
    ↓
NetworkMonitor.isOnline()?
    ├─→ Yes: TranslationApiService.translate()
    └─→ No: Return error
```

**Step 5: Process Response**
```
API Response
    ↓
Transform to Domain Model
    ↓
Save to TranslationCache
    ↓
SaveTranslationUseCase (save to history)
    ↓
Return Translation to ViewModel
```

**Step 6: UI Update**
```
ViewModel receives Translation
    ↓
Update UI State to Success
    ↓
TranslationFragment displays result
```

### Error Handling Flow
```
[Any Error Occurs]
    ↓
[ErrorHandler.handle(exception)]
    ↓
[Map to User Message]
    ├─→ [NetworkError] → "Check internet connection"
    ├─→ [ApiError] → "Translation service unavailable"
    ├─→ [ValidationError] → "Invalid input"
    └─→ [UnknownError] → "Something went wrong"
    ↓
[Log to Analytics/Crashlytics]
    ↓
[Return Error to ViewModel]
    ↓
[Display Error in UI]
```

---

## Network Architecture Diagram

### API Communication
```
[Android App]
    ↓ HTTPS (TLS 1.2+)
[OkHttp Client]
    ├─→ Interceptor Chain
    │   ├─→ Authentication Interceptor (add API key)
    │   ├─→ Logging Interceptor (debug mode)
    │   └─→ Network Interceptor (monitor)
    ↓
[Retrofit Service]
    ↓ JSON/HTTP
[Translation API Server]
    └─→ Response (JSON)
    ↓
[Moshi/Gson Parser]
    ↓
[Data Model Objects]
```

### Offline Mode Architecture
```
[Network Request]
    ↓
[NetworkMonitor.isConnected()]
    ├─→ Yes: Proceed to API
    └─→ No: Switch to offline mode
            ↓
        [Check TranslationCache]
            ├─→ Found: Return cached
            └─→ Not found: Check history
                    ↓
                [HistoryRepository.find(text)]
                    ├─→ Found: Return from history
                    └─→ Not found: Show offline error
```

---

## Database Architecture

### Database Schema
```
[AppDatabase]
    ├─→ translations_table
    │   ├─ id (PRIMARY KEY)
    │   ├─ source_text
    │   ├─ translated_text
    │   ├─ source_language_code
    │   ├─ target_language_code
    │   ├─ timestamp
    │   └─ is_favorite
    │
    └─→ languages_table
        ├─ code (PRIMARY KEY)
        ├─ name
        ├─ native_name
        └─ is_supported
```

### Database Operations Flow
```
[ViewModel]
    ↓
[Use Case]
    ↓
[Repository]
    ↓
[DAO Interface]
    ↓
[Room Database]
    ↓
[SQLite Database File]
```

---

## Dependency Injection Graph

### Application Level
```
[Application]
    ↓
[Hilt Application Component]
    ├─→ [Singleton Scope]
    │   ├─ AppDatabase
    │   ├─ Retrofit
    │   ├─ PreferencesManager
    │   └─ Analytics Manager
    │
    └─→ [Activity Scope]
        └─→ [ViewModels]
            └─→ [Use Cases]
                └─→ [Repositories]
```

### Module Dependencies
```
[AppModule]
    └─→ Provides: Context, PreferencesManager

[NetworkModule]
    ├─→ Depends on: AppModule
    └─→ Provides: Retrofit, ApiService

[DatabaseModule]
    ├─→ Depends on: AppModule
    └─→ Provides: Database, DAOs

[RepositoryModule]
    ├─→ Depends on: NetworkModule, DatabaseModule
    └─→ Provides: Repositories

[ViewModelModule]
    ├─→ Depends on: RepositoryModule
    └─→ Provides: ViewModels
```

---

## Security Architecture

### API Key Protection
```
[API Key Storage]
    ↓
[Android Keystore]
    ↓ (encrypted)
[KeystoreManager]
    ↓ (decrypt on demand)
[TranslationApiService]
    ↓ (add to request header)
[API Request]
```

### Network Security
```
[App]
    ↓ (enforced by network_security_config.xml)
[HTTPS Only Policy]
    ↓
[Certificate Pinning]
    ↓ (verify server cert)
[Secure Connection]
    ↓
[API Server]
```

---

## Caching Strategy

### Multi-Level Cache Architecture
```
[Translation Request]
    ↓
[Level 1: In-Memory Cache]
    ├─→ Hit: Return immediately (fastest)
    └─→ Miss: Continue
            ↓
        [Level 2: Database Cache]
            ├─→ Hit: Return from DB (fast)
            └─→ Miss: Continue
                    ↓
                [Level 3: API Call]
                    ↓
                [Fetch from Server]
                    ↓
                [Save to all cache levels]
                    ↓
                [Return to user]
```

### Cache Eviction Policy
```
[In-Memory Cache]
    └─→ LRU (Least Recently Used)
        └─→ Max size: 100 translations
            └─→ Evict oldest when full

[Database Cache]
    └─→ Time-based expiration
        └─→ Max age: 30 days
            └─→ Cleanup job runs daily
```

---

## State Management Architecture

### Unidirectional Data Flow
```
[User Action]
    ↓
[UI Event]
    ↓
[ViewModel]
    ↓ (dispatch action)
[Use Case]
    ↓ (business logic)
[Repository]
    ↓ (data operation)
[Data Source]
    ↓ (result)
[Repository]
    ↓ (transform)
[Use Case]
    ↓ (emit state)
[ViewModel State]
    ↓ (observe)
[UI Update]
```

### State Types
```
[UI State]
    ├─→ Idle (initial state)
    ├─→ Loading (operation in progress)
    ├─→ Success (data available)
    └─→ Error (operation failed)
```

---

## Deployment Architecture

### Build Pipeline
```
[Source Code]
    ↓
[Git Repository]
    ↓ (push/merge)
[CI/CD Pipeline]
    ├─→ [Lint & Code Analysis]
    ├─→ [Unit Tests]
    ├─→ [Build APK/AAB]
    ├─→ [Integration Tests]
    └─→ [Sign & Package]
        ↓
    [Release Artifacts]
        ├─→ Debug APK (development)
        ├─→ Staging AAB (testing)
        └─→ Release AAB (production)
            ↓
        [Google Play Console]
            ├─→ Internal Testing
            ├─→ Closed Beta
            ├─→ Open Beta
            └─→ Production
```

### Runtime Architecture
```
[User Device]
    └─→ [Android OS]
        └─→ [Hmong Translate App]
            ├─→ [App Process]
            │   ├─ UI Thread
            │   ├─ Background Threads (Coroutines)
            │   └─ Database Thread
            │
            ├─→ [Local Storage]
            │   ├─ SQLite Database
            │   ├─ Shared Preferences
            │   └─ Cache Directory
            │
            └─→ [Network Layer]
                └─→ [External Services]
                    ├─ Translation API
                    ├─ Analytics Service
                    └─ Crash Reporting
```

---

## Monitoring & Analytics Architecture

### Analytics Flow
```
[User Action]
    ↓
[Event Trigger]
    ↓
[AnalyticsManager.logEvent()]
    ↓
[Firebase Analytics SDK]
    ↓ (batch & send)
[Firebase Console]
    └─→ [Dashboards & Reports]
```

### Crash Reporting Flow
```
[App Crash]
    ↓
[CrashHandler]
    ↓
[Collect Stack Trace & Context]
    ↓
[Firebase Crashlytics SDK]
    ↓ (upload when online)
[Crashlytics Console]
    └─→ [Crash Reports & Analysis]
```

### Performance Monitoring
```
[App Lifecycle Events]
    ├─→ App Start
    ├─→ Screen Load
    ├─→ Network Request
    └─→ Database Query
        ↓
    [Performance Metrics]
        ↓
    [Firebase Performance SDK]
        ↓
    [Performance Console]
        └─→ [Performance Reports]
```

---

## Testing Architecture

### Testing Pyramid
```
[UI Tests] (Few) - Espresso/Compose Test
    ↓
[Integration Tests] (Some) - API + Database
    ↓
[Unit Tests] (Many) - ViewModels, Use Cases, Utils
```

### Test Structure
```
[Source Code]
    ├─→ main/
    │   └─ Production code
    │
    ├─→ test/
    │   └─ Unit tests (JUnit, Mockito)
    │       ├─ ViewModelTest
    │       ├─ UseCaseTest
    │       ├─ RepositoryTest
    │       └─ UtilsTest
    │
    └─→ androidTest/
        └─ Instrumentation tests
            ├─ UITest (Espresso)
            ├─ DatabaseTest (Room)
            └─ EndToEndTest
```

---

## Future Architecture Extensions

### Planned Enhancements
```
[Current Architecture]
    └─→ [Phase 1: Voice Translation]
        ├─ Speech Recognition Module
        ├─ Text-to-Speech Module
        └─ Audio Processing Pipeline
            ↓
        [Phase 2: Camera Translation]
            ├─ ML Kit OCR
            ├─ Image Processing
            └─ Real-time Translation
                ↓
            [Phase 3: Cloud Sync]
                ├─ Backend API
                ├─ User Authentication
                └─ Cross-device Sync
                    ↓
                [Phase 4: Offline ML]
                    ├─ On-device ML Models
                    ├─ TensorFlow Lite
                    └─ Model Management
```

---

These descriptions are optimized for creating visual diagrams. Each section can be used independently to generate specific architectural views using visualization tools.
