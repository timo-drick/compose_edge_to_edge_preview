package de.drick.compose.edgetoedgepreviewlib

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.core.graphics.Insets
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
    val gestureNavSize: Dp = 30.dp,
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

    val systemGestureHorizontalSize = with(LocalDensity.current) { cfg.gestureNavSize.roundToPx() }

    val windowInsets = buildInsets {
        var insets = Insets.of(0,0,0,0)
        if (cfg.statusBarMode != InsetMode.Off) {
            val statusBarInsets = setInset(
                top = statusBarHeight,
                type = WindowInsetsCompat.Type.statusBars(),
                isVisible = cfg.statusBarMode == InsetMode.Visible
            )
            insets = insets.union(statusBarInsets)
        }
        val navSize =
            if (cameraCutoutPos == InsetPos.BOTTOM && navigationPos == InsetPos.BOTTOM) navigationBarSize + cameraCutoutSize else navigationBarSize
        if (cfg.navigationBarMode != InsetMode.Off) {
            val navigationBarInsets = setInset(
                pos = navigationPos,
                type = WindowInsetsCompat.Type.navigationBars(),
                size = navSize,
                isVisible = cfg.navigationBarMode == InsetMode.Visible
            )
            insets = insets.union(navigationBarInsets)
        }
        if (cfg.cameraCutoutMode != CameraCutoutMode.None) {
            val cameraCutoutInsets = setInset(
                pos = cameraCutoutPos,
                type = WindowInsetsCompat.Type.displayCutout(),
                size = cameraCutoutSize,
                isVisible = true
            )
            insets = insets.union(cameraCutoutInsets)
        }
        if (cfg.captionBarMode != InsetMode.Off) {
            val captionBarInsets = setInset(
                pos = InsetPos.TOP,
                type = WindowInsetsCompat.Type.captionBar(),
                size = captionBarSize,
                isVisible = true
            )
            insets = insets.union(captionBarInsets)
        }
        setInset(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom,
            type = WindowInsetsCompat.Type.tappableElement(),
            isVisible = true
        )
        setInset(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom,
            type = WindowInsetsCompat.Type.mandatorySystemGestures(),
            isVisible = true
        )
        when (cfg.navMode) {
            NavigationMode.ThreeButton -> {
                setInset(
                    top = statusBarHeight, //TODO maybe also consider display cutout
                    bottom = if (navigationPos == InsetPos.BOTTOM) navSize else 0,
                    type = WindowInsetsCompat.Type.systemGestures(),
                    isVisible = true
                )
            }
            NavigationMode.Gesture -> {
                //TODO add top and bottom
                setInset(
                    left = systemGestureHorizontalSize + insets.left,
                    right = systemGestureHorizontalSize + insets.right,
                    top = insets.top,
                    bottom = insets.bottom,
                    type = WindowInsetsCompat.Type.systemGestures(),
                    isVisible = true
                )
            }
        }
    }
    WindowInsetsInjector(
        windowInsets = windowInsets
    ) {
        Box(modifier.fillMaxSize()) {
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
                    .clearAndSetSemantics { },
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
                        .drawWithContent {
                            // draw status bar only when it is visible
                            if (cfg.statusBarMode == InsetMode.Visible) drawContent()
                        }
                        .clearAndSetSemantics { },
                    isDarkMode = isDarkMode
                )
            }
            if (cfg.captionBarMode != InsetMode.Off) {
                CaptionBar(
                    modifier = Modifier
                        .height(captionBarHeightDp)
                        .align(Alignment.TopCenter)
                        .zIndex(1000f)
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
            if (cfg.showInsetsBorder) {
                HighlightInsets(windowInsets)
            }
        }
    }
}

fun Insets.union(other: Insets): Insets = Insets.max(this, other)
