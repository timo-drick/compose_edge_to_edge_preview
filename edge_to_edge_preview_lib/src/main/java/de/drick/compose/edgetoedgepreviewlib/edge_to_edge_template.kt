package de.drick.compose.edgetoedgepreviewlib

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat

class WindowInsetsPreviewProvider : PreviewParameterProvider<InsetsConfig> {
    override val values = sequenceOf(
        InsetsConfig(),
        InsetsConfig(navMode = NavigationMode.Gesture),
        InsetsConfig(navMode = NavigationMode.Gesture, isInvertedOrientation = true)
    )
}

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
    val useHiddenApiHack: Boolean = false
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

@Composable
fun EdgeToEdgeTemplate(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    isLandscape: Boolean = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE,
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
    useHiddenApiHack: Boolean = false,
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
        isNavigationBarContrastEnforced = isNavigationBarContrastEnforced,
        useHiddenApiHack = useHiddenApiHack
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
fun EdgeToEdgeTemplate(
    cfg: InsetsConfig,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    isLandscape: Boolean = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE,
    content: @Composable () -> Unit
) {
    val navigationPos = when {
        cfg.navMode == NavigationMode.Gesture -> InsetPos.BOTTOM
        isLandscape && cfg.isInvertedOrientation -> InsetPos.LEFT
        isLandscape -> InsetPos.RIGHT
        else -> InsetPos.BOTTOM
    }
    val cameraCutoutPos = when {
        isLandscape.not() && cfg.isInvertedOrientation -> InsetPos.BOTTOM
        isLandscape && cfg.isInvertedOrientation.not() -> InsetPos.LEFT
        isLandscape -> InsetPos.RIGHT
        else -> InsetPos.TOP
    }

    val navigationBarSizeDp = cfg.navigationBarSize
    val cameraCutoutSizeDp = if (cfg.cameraCutoutMode != CameraCutoutMode.None) cfg.cameraCutoutSize else 0.dp

    val statusBarHeightDp = if (cameraCutoutPos == InsetPos.TOP) max(cfg.statusBarSize, cameraCutoutSizeDp) else cfg.statusBarSize
    val captionBarHeightDp = cfg.captionBarSize

    val statusBarHeight = with(LocalDensity.current) { statusBarHeightDp.roundToPx() }
    val navigationBarSize = with(LocalDensity.current) { navigationBarSizeDp.roundToPx() }
    val cameraCutoutSize = with(LocalDensity.current) { cameraCutoutSizeDp.roundToPx() }
    val captionBarSize = with(LocalDensity.current) { captionBarHeightDp.roundToPx() }

    val windowInsets = buildInsets {
        if (cfg.statusBarMode != InsetMode.Off) {
            setInset(
                pos = InsetPos.TOP,
                type = WindowInsetsCompat.Type.statusBars(),
                size = statusBarHeight,
                isVisible = cfg.statusBarMode == InsetMode.Visible
            )
        }
        if (cfg.navigationBarMode != InsetMode.Off) {
            val navSize =
                if (cameraCutoutPos == InsetPos.BOTTOM && navigationPos == InsetPos.BOTTOM) navigationBarSize + cameraCutoutSize else navigationBarSize
            setInset(
                pos = navigationPos,
                type = WindowInsetsCompat.Type.navigationBars(),
                size = navSize,
                isVisible = cfg.navigationBarMode == InsetMode.Visible
            )
        }
        if (cfg.cameraCutoutMode != CameraCutoutMode.None) {
            setInset(
                pos = cameraCutoutPos,
                type = WindowInsetsCompat.Type.displayCutout(),
                size = cameraCutoutSize,
                isVisible = true
            )
        }
        if (cfg.captionBarMode != InsetMode.Off) {
            setInset(
                pos = InsetPos.TOP,
                type = WindowInsetsCompat.Type.captionBar(),
                size = captionBarSize,
                isVisible = true
            )
        }
    }
    ViewInsetInjector(windowInsets, cfg.useHiddenApiHack) {
        Box(modifier.fillMaxSize()) {
            val borderModifier =
                if (cfg.showInsetsBorder) Modifier.border(2.dp, Color.Red) else Modifier
            val cameraCutoutAlignment = when (cameraCutoutPos) {
                InsetPos.LEFT -> AbsoluteAlignment.CenterLeft
                InsetPos.TOP -> AbsoluteAlignment.TopLeft
                InsetPos.RIGHT -> AbsoluteAlignment.CenterRight
                InsetPos.BOTTOM -> AbsoluteAlignment.BottomLeft
            }
            content()
            CameraCutout(
                modifier = Modifier
                    .zIndex(1001f)
                    .align(cameraCutoutAlignment)
                    .then(borderModifier)
                    .clearAndSetSemantics {  },
                cutoutMode = cfg.cameraCutoutMode,
                isVertical = isLandscape,
                cutoutSize = cameraCutoutSizeDp
            )
            val statusBarCutoutPadding = when {
                cfg.cameraCutoutMode == CameraCutoutMode.Start && cameraCutoutPos == InsetPos.TOP ->
                    PaddingValues(start = cameraCutoutSizeDp)

                cfg.cameraCutoutMode == CameraCutoutMode.End && cameraCutoutPos == InsetPos.TOP ->
                    PaddingValues(end = cameraCutoutSizeDp)

                else -> PaddingValues()
            }
            if (cfg.statusBarMode != InsetMode.Off) {
                StatusBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .padding(statusBarCutoutPadding)
                        .height(statusBarHeightDp)
                        .align(Alignment.TopCenter)
                        .zIndex(1000f)
                        .then(borderModifier)
                        .drawWithContent {
                            // draw status bar only when it is visible
                            if (cfg.statusBarMode == InsetMode.Visible) drawContent()
                        }.clearAndSetSemantics { },
                    isDarkMode = isDarkMode
                )
            }
            if (cfg.captionBarMode != InsetMode.Off) {
                CaptionBar(
                    modifier = Modifier
                        .height(captionBarHeightDp)
                        .align(Alignment.TopCenter)
                        .zIndex(1000f)
                        .then(borderModifier)
                        .clearAndSetSemantics { },
                    isDarkMode = isDarkMode
                )
            }

            val navigationBarAlignment = when (navigationPos) {
                InsetPos.LEFT -> AbsoluteAlignment.CenterLeft
                InsetPos.TOP -> AbsoluteAlignment.TopLeft
                InsetPos.RIGHT -> AbsoluteAlignment.CenterRight
                InsetPos.BOTTOM -> AbsoluteAlignment.BottomLeft
            }
            if (cfg.navigationBarMode != InsetMode.Off) {
                NavigationBar(
                    size = navigationBarSizeDp,
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.displayCutout.only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                            )
                        )
                        .align(navigationBarAlignment)
                        .zIndex(1000f)
                        .then(borderModifier)
                        .zIndex(if (cfg.navigationBarMode == InsetMode.Visible) 1000f else 0f)
                        .drawWithContent {
                            // draw navigation bar only when it is visible
                            if (cfg.navigationBarMode == InsetMode.Visible) drawContent()
                        }
                        .clearAndSetSemantics { },
                    isVertical = isLandscape,
                    isDarkMode = isDarkMode,
                    navMode = cfg.navMode,
                    backgroundAlpha = if (cfg.isNavigationBarContrastEnforced) 0.5f else 0f
                )
            }
        }
    }
}
