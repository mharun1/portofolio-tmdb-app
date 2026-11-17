# FilmKu - Android Movie Explorer (TMDB)

[![Android CI](https://github.com/your-username/portfolio-tmdb-app/actions/workflows/android-ci.yml/badge.svg)](https://github.com/your-username/portfolio-tmdb-app/actions/workflows/android-ci.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Navigation](https://img.shields.io/badge/Navigation-3-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com/guide/navigation/navigation-3)

A production-grade Android portfolio application built with **Modern Android Development (MAD)** standards. FilmKu connects to The Movie Database (TMDB) API to discover, search, rate, and bookmark movies with complete offline persistence.

---

## Screen Highlights

| Home (Now Playing & Categories) | Movie Detail & Interactive Rating | Offline Bookmarks & Management |
| :---: | :---: | :---: |
| *(Add your screenshot here: `docs/screenshots/home.png`)* | *(Add your screenshot here: `docs/screenshots/detail.png`)* | *(Add your screenshot here: `docs/screenshots/bookmarks.png`)* |

---

## Key Features

- **Home Feed**: Now-playing carousel and toggleable movie categories (Popular vs. Top Rated) driven by **Paging 3** infinite scrolling.
- **Search**: Instant debounced movie title query with paging results and empty/error states.
- **Movie Detail**: Full synopsis, duration, spoken languages, genre chips, cast avatars, and release date.
- **Offline Bookmarks**: Save favorites locally using **Room Database**; access them in airplane mode.
- **Local Rating**: Submit a personal 1–5 star rating stored in SQLite alongside TMDB's community score.
- **Multiple Backstack State Retention**: Powered by **Jetpack Navigation 3**; scroll positions, loaded paging items, and active category persist seamlessly across tab switches.
- **Settings & Theme**: Light, Dark, and System Default themes powered by **Jetpack DataStore Preferences**.
- **Edge-to-Edge & Dynamic UI**: Edge-to-edge system bars, Shimmer placeholders for network images, and zero-flicker transitions.

---

## Architecture & Tech Stack

Built following Google's **Guide to App Architecture** with **Clean Architecture** and strict **Unidirectional Data Flow (UDF)**.

```
┌────────────────────────────────────────────────────────┐
│                   :app (Android App)                   │
│   Jetpack Compose (Material 3) + Navigation 3 + Hilt   │
│   ViewModels expose UiState via StateFlow              │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
                ▼                        ▼
┌───────────────────────────┐  ┌─────────────────────────┐
│     :domain (Pure JVM)    │  │       :data (Android)   │
│  - Models (Movie, Detail) │  │  - Retrofit + OkHttp    │
│  - Repository Interfaces  │◄─┤  - Room Database (v2.8) │
│  - Use Cases              │  │  - Paging3 Source       │
└───────────────────────────┘  │  - DataStore Prefs      │
                               └─────────────────────────┘
```

### Libraries & Tools

| Layer / Concern | Technologies |
| :--- | :--- |
| **Language** | Kotlin 2.4 |
| **UI Framework** | Jetpack Compose (Material 3), Compose BOM, Icons Extended |
| **Navigation** | Jetpack Navigation 3 (`androidx.navigation3`) with multiple backstacks recipe |
| **Dependency Injection** | Dagger Hilt 2.60 |
| **Asynchronous & Reactive** | Kotlin Coroutines, Flow, StateFlow, snapshotFlow |
| **Pagination** | Jetpack Paging 3 (Compose + Testing) |
| **Local Persistence** | Room 2.8 (SQLite with KSP) + DataStore Preferences |
| **Network & Serialization** | Retrofit 3.0 + OkHttp Logging Interceptor + Kotlinx Serialization JSON |
| **Image Loading** | Coil 2.7 (Compose SubcomposeAsyncImage with Shimmer) |
| **Testing** | JUnit 4, Kotlinx Coroutines Test, MockWebServer, Paging Testing |

---

## Testing

The codebase includes **69 automated unit tests** across all architectural layers:

```bash
./gradlew testDebugUnitTest
```

- **`:domain`**: Use Case validation against repository contracts.
- **`:data`**: Network response serialization, Retrofit interceptors, Room DAO mapping, and PagingSource pagination contract tests via MockWebServer.
- **`:app`**: ViewModel state transitions, retry loops, concurrent in-flight requests, and Room-to-UI state synchronization.

---

## Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2+ (or newer)
- JDK 17+
- TMDB API Read Access Token (Free at [themoviedb.org](https://www.themoviedb.org/documentation/api))

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/portfolio-tmdb-app.git
   cd portfolio-tmdb-app
   ```

2. **Add TMDB API Token**:
   Create a `local.properties` file in the project root (if not present) and add your TMDB API Read Access Token:
   ```properties
   TMDB_ACCESS_TOKEN=your_v4_read_access_token_here
   ```

3. **Build & Run**:
   Open the project in Android Studio, select the `:app` configuration, and click **Run**.

   Or run via terminal:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Attribution & Legal

This product uses the TMDB API but is not endorsed or certified by TMDB.

---

## Author

- **Harun** — [GitHub Profile](https://github.com/your-username)
