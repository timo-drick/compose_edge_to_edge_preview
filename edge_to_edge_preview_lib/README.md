Add dependency:

[![Maven Central](https://img.shields.io/maven-central/v/de.drick.compose/edge-to-edge-preview.svg)](https://mvnrepository.com/artifact/de.drick.compose/edge-to-edge-preview)

```kotlin
dependencies {
    implementation("de.drick.compose:edge-to-edge-preview:<version>")
}
```

Quickstart:

Just use the EdgeToEdgeTemplate composable around your content you want to preview. And it will simulate WindowInsets. You can specify different configurations.

```kotlin
@Preview
@Composable
fun PreviewEdgeToEdge() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        isStatusBarVisible = true,
        isNavigationBarVisible = true,
        isInvertedOrientation = false
    ) {
        //Your MainAppComposable()
        //...
    }
}
```
