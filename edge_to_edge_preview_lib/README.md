# Edge to Edge Preview Library

[![Maven Central](https://img.shields.io/maven-central/v/de.drick.compose/edge-to-edge-preview.svg)](https://mvnrepository.com/artifact/de.drick.compose/edge-to-edge-preview)

A Compose Multiplatform library for creating previews with edge-to-edge designs (WindowInsets simulation) in Android Studio and IntelliJ IDEA.

It now also supports the preview in Compose Multiplatform 1.9.0 and newer. But please note that the IDE will still use the Android preview system for rendering the preview. So everything should work the same as in a Android only project. But of course the limitations of the builtin Multiplatform Preview are there. (Only works when a Android target is defined in the project)

## Features

- Simulate WindowInsets in Compose previews
- Multiplatform support
- Multiple device configurations (navigation modes, camera cutouts)
- Visual insets border highlighting
- Highly configurable preview templates

## Installation

### Android Project

Add the dependency:
[![Maven Central](https://img.shields.io/maven-central/v/de.drick.compose/edge-to-edge-preview.svg)](https://mvnrepository.com/artifact/de.drick.compose/edge-to-edge-preview)
```kotlin
dependencies {
    implementation("de.drick.compose:edge-to-edge-preview:<version>")
}
```

### Multiplatform Project

Add to your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application") // or com.android.library
}

kotlin {
    // Define your targets
    androidTarget("android")
    jvm("desktop")
    // ... other targets
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.components.uiToolingPreview)
                
                // Add edge-to-edge-preview library
                implementation("de.drick.compose:edge-to-edge-preview:<version>")
            }
        }
    }
}
```

## Usage

### Basic Android Preview

```kotlin
import androidx.compose.ui.tooling.preview.Preview
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.InsetMode

@Preview
@Composable
fun PreviewEdgeToEdge() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        statusBarMode = InsetMode.Visible,
        navigationBarMode = InsetMode.Visible,
        isInvertedOrientation = false
    ) {
        // Your MainAppComposable()
        MyAppContent()
    }
}
```

### Multiplatform Preview

For multiplatform projects, use the Compose Multiplatform preview annotation:

```kotlin
import org.jetbrains.compose.ui.tooling.preview.Preview
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode

@Preview
@Composable
fun PreviewEdgeToEdge() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true
    ) {
        // Your MainAppComposable()
        MyAppContent()
    }
}
```

### Advanced Example with WindowInsets

```kotlin
@Preview
@Composable
fun PreviewWithWindowInsets() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        useHiddenApiHack = false,
        isNavigationBarContrastEnforced = false
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Your content that respects WindowInsets
                ItemList(modifier = Modifier.weight(1f))
                AppNavigationBar()
            }
        }
    }
}
```

## Configuration Options

### NavigationMode
- `NavigationMode.ThreeButton` - Traditional three-button navigation
- `NavigationMode.Gesture` - Gesture-based navigation

### CameraCutoutMode
- `CameraCutoutMode.None` - No camera cutout
- `CameraCutoutMode.Middle` - Center camera cutout
- `CameraCutoutMode.Start` - Camera cutout at start
- `CameraCutoutMode.End` - Camera cutout at end

### InsetMode
- `InsetMode.Visible` - Insets are visible and active
- `InsetMode.Hidden` - Insets are hidden but still reserve space (useful for testing `WindowInsets.systemBarsIgnoringVisibility`)
- `InsetMode.Off` - Insets are completely off (both visible and hidden insets are 0)

### Other Options
- `modifier` - Modifier to apply to the template
- `isDarkMode` - Enable dark theme mode (default: `isSystemInDarkTheme()`)
- `isLandscape` - Set landscape orientation (default: detected automatically)
- `showInsetsBorder` - Highlight insets with colored borders
- `statusBarMode` - Status bar visibility mode (InsetMode)
- `navigationBarMode` - Navigation bar visibility mode (InsetMode)
- `captionBarMode` - Caption bar visibility mode (InsetMode, default: `InsetMode.Off`)
- `isInvertedOrientation` - Invert device orientation (in landscape: camera cutout on right, nav buttons on left)
- `isNavigationBarContrastEnforced` - Enforce navigation bar contrast (Android only)
- `useHiddenApiHack` - Enable hidden API workarounds (Android only)

## Sample Projects

For complete usage examples, see:
- **Android samples**: [edge_to_edge_preview module](../edge_to_edge_preview)
- **Multiplatform samples**: [multiplatform_preview_test module](../multiplatform_preview_test)

These samples demonstrate various edge-to-edge layouts, WindowInsets handling, and preview configurations.

## License

The Unlicense - see [LICENSE](../LICENSE) for details.
