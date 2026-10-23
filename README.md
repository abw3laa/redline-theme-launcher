# Redline Theme Launcher
Android TV launcher prototype for Redline MRT-43 / Android 13.

## Current foundation
- Kotlin + Jetpack Compose
- Six built-in themes with persistent selection
- Landscape TV layout and remote focus states
- Android TV and standard launcher entry points
- GitHub Actions builds a debug APK on every push to `main`

## Download APK
Open **Actions → Android APK → latest successful run → Artifacts** and download `redline-theme-launcher-debug`.

## Limitations
This is an early prototype. App tiles are currently visual placeholders and do not launch installed packages. Install over USB and open manually for initial testing. Keep the stock launcher enabled until device-specific behavior is verified.