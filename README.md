# Pandemic Infection Tracker

A companion app for **Pandemic Legacy** that tracks infection cards during play.

## What it tracks

The app does **not** mirror the physical infection deck — you never know exactly which cities remain in it. Instead, you record what happens during play:

- **Record Draw** — tap any of the 48 possible cities when it is drawn; the same city can be recorded multiple times
- **Discard Pile** — cities currently in the discard pile
- **On Deck Top** — cities returned to the top after a reshuffle event

## Game actions

- **Record Draw** — tap a city from the full list when it is drawn from the infection deck; it is added to the discard pile
- **Draw from Deck Top** — tap a city in *On Deck Top* when that card is drawn; it moves back to the discard pile
- **Reshuffle Event** — pick the bottom-of-deck city, add it to the discard pile, then move the entire discard pile to *On Deck Top*
- **Reset** — clear all recorded draws and tracking

## Tech stack

- **Kotlin** with **Jetpack Compose** (Material 3)
- **Gradle** with Kotlin DSL
- Min SDK 26, Target SDK 35

## Getting started

1. Install [Android Studio](https://developer.android.com/studio) if you haven't already.
2. Open Android Studio → **File → Open** → select this folder:
   ```
   C:\Users\joshl\Projects\pandemic-infection-tracker
   ```
3. Wait for Gradle sync to finish.
4. Create or start an emulator: **Device Manager → Create Device** (e.g. Pixel 7, API 35).
5. Click **Run** to build and launch the app.

Main screen code lives in `app/src/main/java/com/pandemic/infectiontracker/MainActivity.kt`.

## Build from command line

```powershell
cd C:\Users\joshl\Projects\pandemic-infection-tracker
.\gradlew.bat assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.
