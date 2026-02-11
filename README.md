# GymPool Tracker

A modern, clean, and efficient Android application for tracking gym workouts and swimming sessions. Built with **Kotlin**, **Jetpack Compose**, and **Clean Architecture**.

## Overview

GymPool Tracker replaces your notebook with a smart, digital log. It allows users to create workouts, log exercises with detailed set information (reps, weight, RPE), and visualize their progress over time. The app is designed to be fast, offline-first, and easy to use during intense training sessions.

## Key Features

* **Workout Logging:** Create custom workouts or start from predefined routines.
* **Exercise Tracking:** Log sets, reps, weight (kg/lbs), and RPE (Rate of Perceived Exertion).
* **Rest Timer:** Automatic countdown timer (90s default) triggers after completing a set.
* **Routine Management:** Save your favorite workouts as routines for quick access.
* **History & Statistics:** View past workouts and track your consistency.
* **Autocomplete:** Smart suggestions for exercise names based on your history.
* **Personal Records:** Automatically tracks your PRs for every exercise.
* **Dark/Light Mode:** Fully supports system themes.

## Tech Stack

This project uses modern Android development tools and best practices:

* **Language:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
* **Database:** [Room](https://developer.android.com/training/data-storage/room) (SQLite)
* **Asynchronicity:** [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

## Project Structure

The project follows strict separation of concerns:

com.patrykadamski.gympooltracker
├── data             # Repository implementations, Data Sources (Room DAO), Mappers
├── domain           # Business Logic (UseCases), Repository Interfaces, Models
├── presentation     # UI (Screens, Components), ViewModels, Navigation
└── di               # Dependency Injection Modules (Hilt)


## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/GymPoolTracker.git](https://github.com/your-username/GymPoolTracker.git)
    ```
2.  **Open in Android Studio:**
    * File -> Open -> Select the cloned folder.
3.  **Sync Gradle:**
    * Allow Android Studio to download dependencies.
4.  **Run:**
    * Connect an Android device or start an Emulator.
    * Click the **Run** button.

## Future Roadmap

* [ ] **Progress Charts:** Visual graphs for volume and 1RM trends.
* [ ] **Body Weight Tracker:** Log and track body weight changes.
* [ ] **Cloud Sync:** Backup data to Google Drive/Firebase.
* [ ] **Wear OS Support:** Companion app for watches.

## Contributing

Contributions are welcome. Please fork the repository and create a pull request for any features or bug fixes.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/NewFeature`)
3.  Commit your Changes (`git commit -m 'Add some NewFeature'`)
4.  Push to the Branch (`git push origin feature/NewFeature`)
5.  Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

---
**Created by Patryk Adamski**
