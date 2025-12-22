# Hmong-Translate-Android-App

A mobile translation application for seamless translation between Hmong and other languages.

## Documentation

### Architecture Documentation
The system architecture is documented in detail across multiple files:

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Main system architecture document covering MVVM + Clean Architecture pattern, components, technology stack, and development guidelines
- **[ARCHITECTURE_COMPONENTS.md](ARCHITECTURE_COMPONENTS.md)** - Detailed descriptions of 22+ individual components with their responsibilities, connections, and data flows
- **[ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)** - Structured descriptions optimized for creating visual diagrams using AI tools like Gemini
- **[ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md)** - Guide on how to use the architecture documentation to create visual diagrams

### Quick Start
1. Read [ARCHITECTURE.md](ARCHITECTURE.md) for a comprehensive overview
2. Use [ARCHITECTURE_GUIDE.md](ARCHITECTURE_GUIDE.md) to learn how to create diagrams
3. Copy sections from [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md) to generate visualizations with Gemini

## Features (Planned)
- Text translation between Hmong and multiple languages
- Translation history with offline support
- Favorite translations
- Language detection
- Clean and intuitive user interface

## Architecture Highlights
- **Pattern**: MVVM + Clean Architecture
- **Language**: Kotlin
- **UI**: Jetpack Compose / XML Views
- **Database**: Room (SQLite)
- **Network**: Retrofit + OkHttp
- **Async**: Coroutines + Flow
- **DI**: Hilt/Dagger

## License
See [LICENSE](LICENSE) for details.