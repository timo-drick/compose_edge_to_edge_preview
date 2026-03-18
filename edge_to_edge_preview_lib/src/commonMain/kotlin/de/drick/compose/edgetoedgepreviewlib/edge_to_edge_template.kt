package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class NavigationMode {
    ThreeButton,
    Gesture
}

enum class InsetMode {
    Visible,
    /**
     * Setting status or navigation bars visibility to Hidden can be used to test
     * code that uses WindowInsets.systemBarsIgnoringVisibility.
     * inset will be 0 but insetIgnoringVisibility will be normal
     */
    Hidden,
    /**
     * Insets visible and hidden insets are 0
     */
    Off
}

data class InsetsConfig(
    val navMode: NavigationMode = NavigationMode.ThreeButton,
    val cameraCutoutMode: CameraCutoutMode = CameraCutoutMode.Middle,
    val cameraCutoutSize: Dp = 52.dp,
    /**
     * When inverted in landscape mode it would be that the camera cutout is on the right and
     * navigation buttons will be on the left
     */
    val isInvertedOrientation: Boolean = false,
    val showInsetsBorder: Boolean = false,
    val statusBarMode: InsetMode = InsetMode.Visible,
    val statusBarSize: Dp = 24.dp,
    val navigationBarMode: InsetMode = InsetMode.Visible,
    val navigationBarSize: Dp = if (navMode == NavigationMode.ThreeButton) 48.dp else 32.dp,
    val isNavigationBarContrastEnforced: Boolean = true,
    val captionBarMode: InsetMode = InsetMode.Off,
    val captionBarSize: Dp = 42.dp,
    val gestureNavSize: Dp = 30.dp,
) {
    companion object {
        val Default = InsetsConfig()
        val GestureNav = InsetsConfig(navMode = NavigationMode.Gesture)
        val InvertedOrientation = InsetsConfig(isInvertedOrientation = true)
        val DesktopMode = InsetsConfig(
            statusBarMode = InsetMode.Off,
            navigationBarMode = InsetMode.Off,
            captionBarMode = InsetMode.Visible,
            cameraCutoutMode = CameraCutoutMode.None
        )
    }
}

@Composable @ReadOnlyComposable expect fun isLandscape(): Boolean

@Composable
fun EdgeToEdgeTemplate(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    isLandscape: Boolean = isLandscape(),
    navMode: NavigationMode = NavigationMode.ThreeButton,
    cameraCutoutMode: CameraCutoutMode = CameraCutoutMode.Middle,
    /**
     * In landscape mode it would be that the camera cutout is on the right and
     * navigation buttons will be on the left
     */
    isInvertedOrientation: Boolean = false,
    showInsetsBorder: Boolean = false,
    statusBarMode: InsetMode = InsetMode.Visible,
    navigationBarMode: InsetMode = InsetMode.Visible,
    isNavigationBarContrastEnforced: Boolean = true,
    captionBarMode: InsetMode = InsetMode.Off,
    content: @Composable () -> Unit
) {
    val config = InsetsConfig(
        navMode = navMode,
        statusBarMode = statusBarMode,
        navigationBarMode = navigationBarMode,
        captionBarMode = captionBarMode,
        cameraCutoutMode = cameraCutoutMode,
        isInvertedOrientation = isInvertedOrientation,
        showInsetsBorder = showInsetsBorder,
        isNavigationBarContrastEnforced = isNavigationBarContrastEnforced
    )
    EdgeToEdgeTemplate(
        modifier = modifier,
        cfg = config,
        isDarkMode = isDarkMode,
        isLandscape = isLandscape,
        content = content
    )
}

@Composable
expect fun EdgeToEdgeTemplate(
    cfg: InsetsConfig,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    isLandscape: Boolean = isLandscape(),
    content: @Composable () -> Unit
)
