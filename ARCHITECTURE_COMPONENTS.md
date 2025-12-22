# System Architecture Components - Detailed Descriptions

This document provides detailed descriptions for each major component in the Hmong Translate Android App architecture. These descriptions are optimized for creating architectural diagrams using visualization tools like Gemini.

## Component Descriptions by Layer

### Presentation Layer Components

#### 1. MainActivity
**Type**: Android Activity  
**Purpose**: Main entry point and container for the application  
**Key Responsibilities**:
- Initialize application theme and navigation
- Host navigation graph
- Handle system-level events (back press, orientation changes)
- Manage permission requests

**Connections**:
- Contains → TranslationFragment, HistoryFragment, SettingsFragment
- Observes → MainViewModel

#### 2. TranslationFragment
**Type**: UI Fragment  
**Purpose**: Primary translation interface where users input and view translations  
**Key Responsibilities**:
- Display source and target language selectors
- Show text input field and translated output
- Handle swap language button
- Display translation loading states
- Show translation errors

**Connections**:
- Observes → TranslationViewModel
- Triggers → Translation use cases
- Navigates to → HistoryFragment

#### 3. HistoryFragment
**Type**: UI Fragment  
**Purpose**: Display user's translation history  
**Key Responsibilities**:
- Show list of past translations
- Enable search and filtering
- Allow deletion of history items
- Support favorite translations

**Connections**:
- Observes → HistoryViewModel
- Triggers → History management use cases
- Can navigate back to → TranslationFragment

#### 4. SettingsFragment
**Type**: UI Fragment  
**Purpose**: Application settings and preferences  
**Key Responsibilities**:
- Language preferences
- Theme selection (light/dark mode)
- Cache management
- About information

**Connections**:
- Observes → SettingsViewModel
- Updates → SharedPreferences

#### 5. TranslationViewModel
**Type**: ViewModel  
**Purpose**: Manage translation screen state and business logic  
**Key Responsibilities**:
- Hold translation UI state
- Process user translation requests
- Manage language selection state
- Handle error states
- Cache recent translations

**Connections**:
- Calls → TranslateTextUseCase
- Calls → DetectLanguageUseCase
- Exposes StateFlow/LiveData to → TranslationFragment

#### 6. HistoryViewModel
**Type**: ViewModel  
**Purpose**: Manage translation history state  
**Key Responsibilities**:
- Load translation history
- Filter and search history
- Delete history items
- Toggle favorite status

**Connections**:
- Calls → GetTranslationHistoryUseCase
- Calls → DeleteHistoryUseCase
- Exposes data to → HistoryFragment

### Domain Layer Components

#### 7. TranslateTextUseCase
**Type**: Use Case  
**Purpose**: Execute translation business logic  
**Key Responsibilities**:
- Validate translation request
- Check cache for existing translation
- Request translation from repository
- Save translation to history
- Handle translation errors

**Connections**:
- Called by → TranslationViewModel
- Uses → TranslationRepository
- Returns → Result<Translation>

#### 8. DetectLanguageUseCase
**Type**: Use Case  
**Purpose**: Automatically detect source language  
**Key Responsibilities**:
- Analyze input text
- Determine most likely source language
- Return confidence score
- Handle detection failures

**Connections**:
- Called by → TranslationViewModel
- Uses → TranslationRepository
- Returns → Result<Language>

#### 9. GetTranslationHistoryUseCase
**Type**: Use Case  
**Purpose**: Retrieve user's translation history  
**Key Responsibilities**:
- Fetch history from local database
- Apply filters and sorting
- Handle pagination
- Return formatted results

**Connections**:
- Called by → HistoryViewModel
- Uses → HistoryRepository
- Returns → Flow<List<Translation>>

#### 10. SaveTranslationUseCase
**Type**: Use Case  
**Purpose**: Persist translation to history  
**Key Responsibilities**:
- Validate translation data
- Save to local database
- Update cache
- Handle duplicates

**Connections**:
- Called by → TranslateTextUseCase
- Uses → HistoryRepository
- Returns → Result<Unit>

#### 11. Translation (Domain Model)
**Type**: Data Entity  
**Purpose**: Core translation domain object  
**Properties**:
- id: String
- sourceText: String
- translatedText: String
- sourceLanguage: Language
- targetLanguage: Language
- timestamp: Long
- isFavorite: Boolean

**Connections**:
- Used by → All use cases
- Transformed from → TranslationResponse (Data Layer)
- Transformed to → TranslationEntity (Database)

#### 12. Language (Domain Model)
**Type**: Data Entity  
**Purpose**: Represents a supported language  
**Properties**:
- code: String (ISO 639-1)
- name: String
- nativeName: String
- isSupported: Boolean

**Connections**:
- Used by → Translation model
- Loaded from → LanguageRepository

### Data Layer Components

#### 13. TranslationRepository
**Type**: Repository Implementation  
**Purpose**: Central data management for translations  
**Key Responsibilities**:
- Coordinate between remote and local data sources
- Implement caching strategy
- Handle network/database errors
- Transform data models

**Connections**:
- Implements → TranslationRepository Interface
- Uses → TranslationApiService (Remote)
- Uses → TranslationCache
- Called by → TranslateTextUseCase, DetectLanguageUseCase

#### 14. HistoryRepository
**Type**: Repository Implementation  
**Purpose**: Manage translation history data  
**Key Responsibilities**:
- CRUD operations for history
- Query and filter history
- Manage favorites
- Handle data cleanup

**Connections**:
- Implements → HistoryRepository Interface
- Uses → TranslationDao (Local database)
- Called by → History-related use cases

#### 15. TranslationApiService
**Type**: Remote Data Source  
**Purpose**: Interface with external translation API  
**Key Responsibilities**:
- Make HTTP requests to translation API
- Handle API authentication
- Parse API responses
- Manage rate limiting
- Handle network errors

**Connections**:
- Uses → Retrofit client
- Called by → TranslationRepository
- Returns → TranslationResponse (API model)

**API Endpoints**:
- POST /translate - Translate text
- POST /detect - Detect language
- GET /languages - Get supported languages

#### 16. TranslationDao
**Type**: Local Data Source  
**Purpose**: Database access object for translations  
**Key Responsibilities**:
- Insert translation records
- Query translation history
- Update favorites
- Delete history items
- Complex queries (search, filter)

**Connections**:
- Part of → Room Database
- Used by → HistoryRepository
- Works with → TranslationEntity

**Database Operations**:
- insert(translation)
- getAll()
- getById(id)
- deleteById(id)
- searchByText(query)
- getFavorites()

#### 17. TranslationCache
**Type**: Cache Manager  
**Purpose**: In-memory caching for performance  
**Key Responsibilities**:
- Store recent translations
- Implement LRU eviction policy
- Provide fast lookup
- Manage cache size

**Connections**:
- Used by → TranslationRepository
- Stores → Translation objects
- Cache key format: "{sourceLanguage}_{targetLanguage}_{sourceText}"

#### 18. AppDatabase
**Type**: Room Database  
**Purpose**: SQLite database wrapper  
**Key Responsibilities**:
- Database creation and migrations
- Provide DAOs
- Handle database version management
- Ensure data integrity

**Connections**:
- Provides → TranslationDao
- Contains tables for → TranslationEntity, LanguageEntity
- Configured by → Hilt/Dagger modules

### Cross-Cutting Components

#### 19. NetworkMonitor
**Type**: Utility Service  
**Purpose**: Monitor network connectivity  
**Key Responsibilities**:
- Detect online/offline state
- Notify repositories of connectivity changes
- Expose connectivity state as Flow
- Handle different connection types (WiFi, Cellular)

**Connections**:
- Observes → ConnectivityManager
- Used by → Repositories
- Exposes → StateFlow<NetworkStatus>

#### 20. PreferencesManager
**Type**: Data Store  
**Purpose**: Manage user preferences and settings  
**Key Responsibilities**:
- Store user preferences
- Provide reactive preference updates
- Handle default values
- Type-safe preference access

**Preferences Stored**:
- Default source/target languages
- Theme preference
- Translation history enabled
- Auto-detect language setting

**Connections**:
- Uses → DataStore/SharedPreferences
- Used by → ViewModels, Repositories

#### 21. ErrorHandler
**Type**: Error Management  
**Purpose**: Centralized error handling and mapping  
**Key Responsibilities**:
- Map exceptions to user messages
- Log errors for monitoring
- Provide retry strategies
- Handle different error types

**Error Types Handled**:
- NetworkError (timeout, no connection)
- ApiError (rate limit, authentication)
- DatabaseError (write failure, constraint violation)
- ValidationError (invalid input)

**Connections**:
- Used by → All ViewModels and Repositories
- Reports to → Analytics and Crashlytics

#### 22. Analytics Manager
**Type**: Analytics Service  
**Purpose**: Track user behavior and app performance  
**Key Responsibilities**:
- Log user events
- Track feature usage
- Monitor performance metrics
- Collect crash reports

**Events Tracked**:
- translation_performed
- language_changed
- history_viewed
- favorite_added
- app_error

**Connections**:
- Uses → Firebase Analytics
- Used by → ViewModels and Activities

## Component Interaction Patterns

### Translation Request Flow
```
TranslationFragment → TranslationViewModel → TranslateTextUseCase → TranslationRepository
                                                                              ↓
                                                                    TranslationApiService
                                                                              ↓
                                                                    TranslationResponse
                                                                              ↓
                                                                    Transform to Domain Model
                                                                              ↓
                                                                    Save to Cache & History
                                                                              ↓
                                                ← Translation Domain Model ←
                            ← UI Update ←
```

### Offline Detection Flow
```
NetworkMonitor detects offline → Notifies TranslationRepository → Switches to local-only mode
                                                                            ↓
                                                            Checks TranslationCache → Returns if exists
                                                                            ↓
                                                            Checks HistoryRepository → Returns if exists
                                                                            ↓
                                                            Shows offline error message
```

### History Loading Flow
```
HistoryFragment → HistoryViewModel → GetTranslationHistoryUseCase → HistoryRepository
                                                                              ↓
                                                                    TranslationDao.getAll()
                                                                              ↓
                                                                    List<TranslationEntity>
                                                                              ↓
                                                                    Transform to Domain Models
                                                                              ↓
                                              ← Flow<List<Translation>> ←
              ← Display history list ←
```

## Dependency Injection Structure

### Modules

#### AppModule
Provides:
- Application context
- Analytics Manager
- PreferencesManager

#### NetworkModule
Provides:
- Retrofit instance
- OkHttpClient
- TranslationApiService
- NetworkMonitor

#### DatabaseModule
Provides:
- AppDatabase instance
- TranslationDao
- Dispatchers for database operations

#### RepositoryModule
Provides:
- TranslationRepository
- HistoryRepository
- TranslationCache

#### UseCaseModule
Provides:
- All use case implementations
- Bound to their interfaces

## Data Models

### Data Transformation Chain

#### Translation API Response → Domain Model → Database Entity

**API Model (TranslationResponse)**:
```
{
  "translated_text": "Nyob zoo",
  "source_language": "en",
  "target_language": "hmn",
  "confidence": 0.98
}
```

**Domain Model (Translation)**:
```
Translation(
  id = UUID,
  sourceText = "Hello",
  translatedText = "Nyob zoo",
  sourceLanguage = Language(code="en", name="English"),
  targetLanguage = Language(code="hmn", name="Hmong"),
  timestamp = currentTimeMillis(),
  isFavorite = false
)
```

**Database Entity (TranslationEntity)**:
```
@Entity(tableName = "translations")
data class TranslationEntity(
  @PrimaryKey val id: String,
  val sourceText: String,
  val translatedText: String,
  val sourceLanguageCode: String,
  val targetLanguageCode: String,
  val timestamp: Long,
  val isFavorite: Boolean
)
```

## State Management

### UI State Models

#### TranslationUiState
```
sealed class TranslationUiState {
  object Idle
  object Loading
  data class Success(val translation: Translation)
  data class Error(val message: String)
}
```

#### HistoryUiState
```
data class HistoryUiState(
  val translations: List<Translation> = emptyList(),
  val isLoading: Boolean = false,
  val error: String? = null,
  val searchQuery: String = ""
)
```

## Security Components

#### KeystoreManager
**Purpose**: Securely store sensitive data  
**Responsibilities**:
- Store API keys in Android Keystore
- Encrypt/decrypt sensitive data
- Manage encryption keys

#### NetworkSecurityConfig
**Purpose**: Define network security policy  
**Configuration**:
- Enforce HTTPS
- Certificate pinning for API domain
- Clear text traffic disabled

## Performance Optimization Components

#### ImageLoader
**Purpose**: Efficient image loading and caching  
**Uses**: Coil library  
**Features**:
- Memory and disk caching
- Placeholder and error images
- Image transformations

#### WorkManager Jobs
**Purpose**: Background task scheduling  
**Jobs**:
- Clean old history entries
- Sync favorites to cloud (future)
- Update language support list

This document provides comprehensive component descriptions that can be used to create architectural diagrams, flow charts, and system design visualizations.
