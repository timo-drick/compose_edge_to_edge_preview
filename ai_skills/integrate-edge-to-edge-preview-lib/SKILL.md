# Skill: Integrate `edge_to_edge_preview_lib`

Use this skill when an AI agent must add edge-to-edge Compose preview simulation to an existing project by integrating:

- Module: `edge_to_edge_preview_lib`
- Maven artifact: `de.drick.compose:edge-to-edge-preview`

This skill covers Android Compose projects and Kotlin Multiplatform (Compose Multiplatform) projects.

## Goal

After applying this skill, the target project should be able to render edge-to-edge preview variants with simulated `WindowInsets` (status bar, navigation bar, camera cutout, optional caption bar), powered by `EdgeToEdgeTemplate`.

## Prerequisites

- The target project uses Compose.
- For Compose Multiplatform previews, the target project has:
  - Compose Multiplatform `1.10.1+` (recommended: latest stable)
  - An Android target configured (required for current preview rendering path)
- The agent can edit Gradle files and at least one preview file.

## Step 1: Detect project type

Classify the target module before editing:

1. **Android-only Compose module**
   - Usually uses `com.android.application` / `com.android.library` and Kotlin Android plugin.
2. **Kotlin Multiplatform module**
   - Uses `org.jetbrains.kotlin.multiplatform`, Compose plugin, and has `sourceSets`.

Then follow the matching branch below.

## Step 2A: Add dependency (Android-only)

Add the library to the module that owns preview composables:

```kotlin
dependencies {
    implementation("de.drick.compose:edge-to-edge-preview:<version>")
}
```

If the project uses a version catalog, prefer adding aliases there instead of hardcoding the coordinate.

## Step 2B: Add dependency (Kotlin Multiplatform)

In `kotlin { sourceSets { ... } }`:

1. Add the edge-to-edge dependency to `commonMain`.
2. Ensure preview dependencies are present:
   - `commonMain`: `ui-tooling-preview`
   - `androidMain`: `ui-tooling` (for IDE preview tooling support)
3. Ensure an Android target exists in the same KMP module (preview path depends on it).

Example:

```kotlin
kotlin {
    android {
        namespace = "your.namespace"
        compileSdk = 36
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:<compose_version>")
                implementation("de.drick.compose:edge-to-edge-preview:<version>")
            }
        }
        androidMain.dependencies {
            implementation("org.jetbrains.compose.ui:ui-tooling:<compose_version>")
        }
    }
}
```

## Step 3: Wrap previews with `EdgeToEdgeTemplate`

In a preview composable, wrap screen content with `EdgeToEdgeTemplate`.

Minimal example:

```kotlin
@Preview
@Composable
fun MyScreenEdgeToEdgePreview() {
    EdgeToEdgeTemplate {
        MyScreen()
    }
}
```

Configured example:

```kotlin
@Preview
@Composable
fun MyScreenEdgeToEdgeConfiguredPreview() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        statusBarMode = InsetMode.Visible,
        navigationBarMode = InsetMode.Visible,
        captionBarMode = InsetMode.Off,
        isInvertedOrientation = false,
        isNavigationBarContrastEnforced = false
    ) {
        MyScreen()
    }
}
```

## Step 4: Select realistic preview variants

Create at least a small matrix of variants to catch inset issues early:

- Navigation modes: `ThreeButton`, `Gesture`
- Cutout modes: `None`, `Middle` (or `Start` / `End` when relevant)
- Orientation path: portrait and landscape (`isLandscape` or orientation-based previews)
- One hidden-bars variant (`InsetMode.Hidden`) when testing `systemBarsIgnoringVisibility` behavior

Use `showInsetsBorder = true` in diagnostics previews.

## Step 5: Verify integration

Minimum verification checklist:

1. Gradle sync succeeds.
2. Preview compiles with imports:
   - `EdgeToEdgeTemplate`
   - `NavigationMode`
   - `CameraCutoutMode`
   - `InsetMode` (when used)
3. Preview renders at least one variant without runtime/preview errors.
4. At least one configured preview demonstrates non-default insets behavior.

## API quick reference

- `EdgeToEdgeTemplate(...)`
  - Main wrapper composable for simulating edge-to-edge environment.
- `InsetsConfig`
  - Full configuration object (alternative to direct named parameters).
- `NavigationMode`
  - `ThreeButton`, `Gesture`
- `InsetMode`
  - `Visible`, `Hidden`, `Off`
- `CameraCutoutMode`
  - `None`, `Middle`, `Start`, `End`

## Common pitfalls and fixes

1. **KMP preview does not render edge-to-edge simulation**
   - Check Compose Multiplatform version is `1.10.1+`.
   - Verify Android target is configured in the KMP module.
2. **Unresolved preview/tooling classes**
   - Add `ui-tooling-preview` to `commonMain`.
   - Add `ui-tooling` to `androidMain`.
3. **Insets appear wrong in test preview**
   - Confirm `statusBarMode` / `navigationBarMode` are not accidentally set to `Off`.
   - Temporarily enable `showInsetsBorder = true` to debug.

## Done criteria

Mark the integration complete only when all are true:

- Dependency is present in the correct source set/module.
- At least one preview uses `EdgeToEdgeTemplate` successfully.
- At least one non-default configuration variant is present.
- Project/module compiles after changes.

## Reference

- [`reference/integration-checklist.md`](reference/integration-checklist.md)
  - Compact handoff checklist for fast agent-to-agent transfer.
