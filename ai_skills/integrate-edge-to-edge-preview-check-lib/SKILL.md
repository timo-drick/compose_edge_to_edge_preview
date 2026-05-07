# Skill: Integrate `edge_to_edge_preview_check_lib`

Use this skill when an AI agent must add visual window-insets overlap diagnostics to Compose previews by integrating:

- Module: `edge_to_edge_preview_check_lib`
- Maven artifact: `de.drick.compose:edge-to-edge-preview-check`

This skill covers Android Compose modules and Kotlin Multiplatform Compose modules that render previews through an Android target.

## Goal

After applying this skill, the target project should be able to highlight potential overlap issues in previews by using `TestWindowInsets { ... }` with semantic node matchers and inset checks.

## Prerequisites

- The target project uses Compose previews (`@Preview` or multipreview variants).
- The agent can edit Gradle files and at least one preview source file.
- For KMP flow, the module includes an Android target used for preview rendering.

## Step 1: Detect module type and preview source set

Classify where preview code lives before editing:

1. **Android-only Compose module**
   - Usually `com.android.application` / `com.android.library`.
   - Dependency is normally added in module `dependencies { implementation(...) }`.
2. **Kotlin Multiplatform module**
   - Usually `org.jetbrains.kotlin.multiplatform` + Compose plugin.
   - Add dependency to `commonMain` when preview composables are in shared code.

If preview composables live in another source set, add dependencies there instead.

## Step 2: Add `edge-to-edge-preview-check` dependency

### 2A. Android-only module

```kotlin
dependencies {
    implementation("de.drick.compose:edge-to-edge-preview-check:<version>")
}
```

### 2B. Kotlin Multiplatform module

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("de.drick.compose:edge-to-edge-preview-check:<version>")
            }
        }
    }
}
```

If the project uses a version catalog, prefer aliases instead of hardcoded coordinates.

## Step 3: Ensure matcher APIs are available in preview code

`TestWindowInsets` checks are matcher-driven and commonly use:

- `SemanticsMatcher`
- `SemanticsProperties`
- `hasClickAction()`

If these symbols are unresolved in preview source code, add Compose UI test matcher dependency in that same source set (for example via version catalog alias such as `composeUiTest`).

## Step 4: Add `TestWindowInsets` around preview content

Use `TestWindowInsets` in a preview and register checks via DSL.

Minimal pattern:

```kotlin
@Preview
@Composable
fun MyScreenPreview() {
    TestWindowInsets {
        onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .checkOverlap(WindowInsets.safeDrawing)
        onAllNodes(hasClickAction())
            .checkOverlap(WindowInsets.tappableElement)
    }

    MyScreen()
}
```

What this does:
- Draws visual overlay hints for inset/content intersections.
- Helps catch clipped labels or tappable UI near system bars/cutout areas.

## Step 5: Combine with `EdgeToEdgeTemplate` for deterministic insets

Android Studio `showSystemUi` previews can be inconsistent for some inset combinations (notably landscape navigation bars). For reliable diagnostics, wrap content with `EdgeToEdgeTemplate` and place `TestWindowInsets` inside it.

```kotlin
@Preview
@Composable
fun MyScreenPreviewDiagnostics() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        isInvertedOrientation = true
    ) {
        TestWindowInsets {
            onAllNodes(hasClickAction())
                .checkOverlap(WindowInsets.tappableElement)
        }
        MyScreen()
    }
}
```

## Step 6: Choose meaningful checks

Prefer at least these two matcher groups:

1. Text/content readability checks:
   - `SemanticsMatcher.keyIsDefined(SemanticsProperties.Text)` + `WindowInsets.safeDrawing`
2. Tap target accessibility checks:
   - `hasClickAction()` + `WindowInsets.tappableElement` (or `safeContent`)

Use the optional flags on `checkOverlap(...)` when needed:
- `excludeVerticalScrollSides`
- `excludeHorizontalScrollSides`

## Step 7: Verify integration

Minimum verification checklist:

1. Gradle sync succeeds.
2. Preview source compiles with imports:
   - `TestWindowInsets`
   - `SemanticsMatcher` / `hasClickAction`
   - `WindowInsets`
3. At least one preview renders with `TestWindowInsets` checks configured.
4. At least one preview combines check-lib with a deterministic inset configuration (`EdgeToEdgeTemplate` or equivalent controlled setup).

## Common pitfalls and fixes

1. **No diagnostics visible in preview**
   - Ensure `TestWindowInsets { ... }` is actually called in the preview path.
   - Confirm matchers target existing semantics nodes.
2. **`SemanticsMatcher` / `hasClickAction` unresolved**
   - Add Compose UI matcher dependency (commonly `ui-test`) to the same source set as the preview file.
3. **Landscape/system-bar behavior looks incorrect in plain `showSystemUi` preview**
   - Use `EdgeToEdgeTemplate(...)` for controlled inset simulation.
4. **KMP preview checks fail on non-Android target runtime**
   - Run this diagnostics flow through the Android preview path for now.

## Done criteria

Mark integration complete only when all are true:

- Dependency `de.drick.compose:edge-to-edge-preview-check` is present in the correct module/source set.
- Preview code includes `TestWindowInsets { ... }` with at least one real matcher-based `checkOverlap(...)` call.
- At least one check validates text/safe area or tap target/tappable inset behavior.
- Preview compiles and renders without unresolved symbols.

## Reference

- [`reference/integration-checklist.md`](reference/integration-checklist.md)
  - Compact handoff checklist for fast agent-to-agent transfer.