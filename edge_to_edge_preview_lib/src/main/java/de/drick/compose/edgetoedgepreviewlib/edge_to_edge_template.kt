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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat

class NavigationPreviewProvider : PreviewParameterProvider<NavigationMode> {
    override val values = sequenceOf(NavigationMode.ThreeButton, NavigationMode.Gesture)
}

enum class NavigationMode {
    ThreeButton,
    TwoButton,
    Gesture
}

@Composable
fun EdgeToEdgeTemplate(
    modifier: Modifier = Modifier,
    navMode: NavigationMode = NavigationMode.ThreeButton,
    cameraCutoutMode: CameraCutoutMode = CameraCutoutMode.Middle,
    isInvertedOrientation: Boolean = false,// in landscape mode it would be that the camera cutout is
    // on the right and navigation buttons will be on the left
    showInsetsBorder: Boolean = true,
    // Setting status or navigation bars visibility to false can be used to test
    // code that uses WindowInsets.systemBarsIgnoringVisibility
    isStatusBarVisible: Boolean = true,
    isStatusBarContrastEnforced: Boolean = false,
    isNavigationBarVisible: Boolean = true,
    isNavigationBarContrastEnforced: Boolean = true,
    useHiddenApiHack: Boolean = false,
    content: @Composable () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val navigationPos = when {
        navMode == NavigationMode.Gesture -> InsetPos.BOTTOM
        isLandscape && isInvertedOrientation -> InsetPos.LEFT
        isLandscape -> InsetPos.RIGHT
        else -> InsetPos.BOTTOM
    }
    val cameraCutoutPos = when {
        isLandscape.not() && isInvertedOrientation -> InsetPos.BOTTOM
        isLandscape && isInvertedOrientation.not() -> InsetPos.LEFT
        isLandscape -> InsetPos.RIGHT
        else -> InsetPos.TOP
    }

    val isDarkMode = isSystemInDarkTheme()
    val navigationBarSizeDp = if (navMode == NavigationMode.ThreeButton) 48.dp else 32.dp
    val cameraCutoutSizeDp = if (cameraCutoutMode != CameraCutoutMode.None) 52.dp else 0.dp
    val statusBarHeightDp = if (cameraCutoutPos == InsetPos.TOP) max(24.dp, cameraCutoutSizeDp) else 24.dp

    val statusBarHeight = with(LocalDensity.current) { statusBarHeightDp.roundToPx() }
    val navigationBarSize = with(LocalDensity.current) { navigationBarSizeDp.roundToPx() }
    val cameraCutoutSize = with(LocalDensity.current) { cameraCutoutSizeDp.roundToPx() }
    val windowInsets = buildInsets {
        setInset(
            pos = InsetPos.TOP,
            type = WindowInsetsCompat.Type.statusBars(),
            size = statusBarHeight,
            isVisible = isStatusBarVisible
        )
        val navSize = if (cameraCutoutPos == InsetPos.BOTTOM && navigationPos == InsetPos.BOTTOM) navigationBarSize + cameraCutoutSize else navigationBarSize
        setInset(
            pos = navigationPos,
            type = WindowInsetsCompat.Type.navigationBars(),
            size = navSize,
            isVisible = isNavigationBarVisible
        )
        if (cameraCutoutMode != CameraCutoutMode.None) {
            setInset(
                pos = cameraCutoutPos,
                type = WindowInsetsCompat.Type.displayCutout(),
                size = cameraCutoutSize,
                isVisible = true
            )
        }
    }
    ViewInsetInjector(windowInsets, useHiddenApiHack) {
        Box(modifier.fillMaxSize()) {
            val borderModifier =
                if (showInsetsBorder) Modifier.border(2.dp, Color.Red) else Modifier
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
                    .then(borderModifier),
                cutoutMode = cameraCutoutMode,
                isVertical = isLandscape,
                cutoutSize = cameraCutoutSizeDp
            )
            val statusBarCutoutPadding = when {
                cameraCutoutMode == CameraCutoutMode.Start && cameraCutoutPos == InsetPos.TOP ->
                    PaddingValues(start = cameraCutoutSizeDp)

                cameraCutoutMode == CameraCutoutMode.End && cameraCutoutPos == InsetPos.TOP ->
                    PaddingValues(end = cameraCutoutSizeDp)

                else -> PaddingValues()
            }
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
                        if (isStatusBarVisible) drawContent()
                    },
                isDarkMode = isDarkMode
            )

            val navigationBarAlignment = when (navigationPos) {
                InsetPos.LEFT -> AbsoluteAlignment.CenterLeft
                InsetPos.TOP -> AbsoluteAlignment.TopLeft
                InsetPos.RIGHT -> AbsoluteAlignment.CenterRight
                InsetPos.BOTTOM -> AbsoluteAlignment.BottomLeft
            }
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
                    .zIndex(if (isNavigationBarVisible) 1000f else 0f)
                    .drawWithContent {
                        // draw navigation bar only when it is visible
                        if (isNavigationBarVisible) drawContent()
                    },
                isVertical = isLandscape,
                isDarkMode = isDarkMode,
                navMode = navMode,
                alpha = if (isNavigationBarContrastEnforced) 0.5f else 1f
            )
        }
    }
}
