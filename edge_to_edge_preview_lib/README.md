# Jetpack Compose previews for edge-to-edge design

Library which enables you to show WindowInsets in Jetpack Compose previews. Supported insets are the status bar, navigation bar and a camera display cutout. It also is able to show many different configurations of the system bars.

![multiple_preview_sample.png](docu%2Fmultiple_preview_sample.png)

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

# Why is it good to have previews for WindowInsets?
I wrote an article about this here:
https://medium.com/@timo_86166/jetpack-compose-previews-for-edge-to-edge-design-a03b3a3713f3
