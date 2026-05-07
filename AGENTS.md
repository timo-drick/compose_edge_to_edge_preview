# Agent Instructions: Compose Edge-to-Edge Preview

This document provides context and guidelines for AI agents working on the `compose_edge_to_edge_preview` project.

## Project Overview

The `compose_edge_to_edge_preview` project is a collection of libraries and samples designed to facilitate the development and testing of edge-to-edge designs in Jetpack Compose and Compose Multiplatform. It allows developers to simulate and test `WindowInsets` (status bar, navigation bar, camera cutouts) in Android Studio/IntelliJ IDEA previews, Robolectric tests, and on-device tests.

## Project Structure

- `edge_to_edge_preview_lib`: The core library for simulating `WindowInsets` in Compose previews. Supports Multiplatform.
- `edge_to_edge_preview_check_lib`: A library to highlight `WindowInsets` overlap issues in previews.
- `edge_to_edge_test_lib`: A library for automated testing of `WindowInsets` overlaps in integration and Robolectric tests.
- `samples/`: Demonstrates the usage of the libraries.
    - `edge_to_edge_preview`: Android-specific samples.
    - `multiplatform_preview_test`: Compose Multiplatform samples.
    - `preview_screenshot_test`: Samples for screenshot testing (using official Compose screenshot testing plugin).
- `device_recording/`: A tool to record `WindowInsets` from real devices (under development).
- `buildSrc/`: Contains shared build logic and versioning.
- `gradle/libs.versions.toml`: Version catalog for dependencies and plugins.

## AI Skills (Start Here for Integration Tasks)

When the task is about integrating one of this repository's libraries into another project, check `ai_skills/README.md` first and use the matching skill package.

Available integration skills:

- `ai_skills/integrate-edge-to-edge-preview-lib/SKILL.md`
  - Use for integrating `edge_to_edge_preview_lib` (`de.drick.compose:edge-to-edge-preview`) into Android or KMP Compose projects.
- `ai_skills/integrate-edge-to-edge-test-lib/SKILL.md`
  - Use for integrating `edge_to_edge_test_lib` (`de.drick.compose:edge-to-edge-test`) into Android test pipelines (instrumented and Robolectric).
- `ai_skills/integrate-edge-to-edge-preview-check-lib/SKILL.md`
  - Use for integrating `edge_to_edge_preview_check_lib` (`de.drick.compose:edge-to-edge-preview-check`) for preview-time overlap diagnostics.

Recommended usage flow for agents:

1. Open `ai_skills/README.md` to discover the right skill.
2. Read the selected `SKILL.md` fully before editing.
3. Use the linked `reference/integration-checklist.md` as a final verification pass.
4. Mirror the skill's dependency/source-set guidance exactly (Android vs KMP).

## Key Technologies

- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose / Compose Multiplatform**
- **Android Gradle Plugin (AGP) 9+**
- **Robolectric** for headless UI testing.
- **Paparazzi** for screenshot testing.
- **Maven Publish (Vanniktech)** for library publishing.

## Development Guidelines

### 1. Code Style and Conventions
- Follow standard Kotlin and Compose coding conventions.
- Maintain consistency with existing file naming and project structure.
- Ensure that all multiplatform code is placed in `commonMain` where possible.
- Use `gradle/libs.versions.toml` for all dependency and plugin versions.

### 2. Testing
- **Unit Tests**: Place in `src/test` of the respective module.
- **Integration Tests**: Place in `src/androidTest` for Android-specific tests.
- **Robolectric Tests**: Use for simulating different device configurations without an emulator.
- **Screenshot Tests**: Use Paparazzi or the built-in screenshot testing support in the samples.

#### Running Tests
- To run all tests: `./gradlew test connectedAndroidTest`
- To run tests for a specific module: `./gradlew :module_name:test`
- For Robolectric tests, ensure the SDK is set appropriately (e.g., `@Config(sdk = [34])`).

### 3. Adding New Features
- When adding new inset types or simulation modes, update `EdgeToEdgeTemplate` and related enums in `edge_to_edge_preview_lib`.
- Provide a sample in the `samples` directory to demonstrate the new feature.
- Add automated tests in `edge_to_edge_test_lib` to verify the functionality.

### 4. Documentation
- Update the respective `README.md` files when making significant changes to a library.
- Use KDoc for documenting public APIs.

## Common Tasks for Agents

- **Fixing Bug**: If a bug is reported in `WindowInsets` simulation, create a reproduction test in `edge_to_edge_test_lib` (using Robolectric if possible) before applying the fix.
- **Adding Device Configuration**: If a new device configuration (e.g., a specific camera cutout) needs to be supported, update the enums in `edge_to_edge_preview_lib`.
- **Improving Overlap Detection**: If the overlap detection logic in `edge_to_edge_preview_check_lib` or `edge_to_edge_test_lib` needs improvement, ensure it's validated against existing samples.

## Important Files

- `gradle/libs.versions.toml`: The single source of truth for all dependency versions.
- `ai_skills/README.md`: Index of reusable AI integration skills.
- `ai_skills/*/SKILL.md`: Task-focused workflows for integrating each published library artifact.
- `edge_to_edge_preview_lib/src/commonMain/kotlin/de/drick/compose/edgetoedgepreviewlib/edge_to_edge_template.kt`: The main entry point for the preview simulation.
- `edge_to_edge_test_lib/src/androidMain/kotlin/de/drick/compose/edgetoedgetestlib/window_insets_test.kt`: Contains the overlap assertion logic.
- `build.gradle.kts`: Root build file.
- `settings.gradle.kts`: Defines the project modules.
