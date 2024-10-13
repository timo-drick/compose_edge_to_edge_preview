package de.drick.compose.edgetoedgepreview

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import de.drick.compose.edgetoedgepreview.layout_experiments.SplitLayoutHorizontal
import de.drick.compose.edgetoedgepreview.layout_experiments.SplitLayoutVertical
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@SampleBlogPreviews
@Composable
private fun PreviewEdgeToEdgePortraitListDetail() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.None,
        isInvertedOrientation = false,
        showInsetsBorder = true,
    ) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SampleLandscapeListDetail()
        } else {
            SamplePortraitListDetail()
        }
    }
}

@Composable
fun SamplePortraitListDetail(
    modifier: Modifier = Modifier
) {
    SplitLayoutHorizontal(
        modifier = modifier,
        insets = WindowInsets.safeDrawing,
        first = {
            TestComponentWindowInsets(
                modifier = Modifier.fillMaxSize(),
                title = "List",
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal +
                            WindowInsetsSides.Top
                )
            )
        },
        second = {
            TestComponentWindowInsets(
                modifier = Modifier.fillMaxSize(),
                title = "Detail",
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal +
                            WindowInsetsSides.Bottom
                )
            )
        }
    )
}

@Composable
fun SampleLandscapeListDetail(
    modifier: Modifier = Modifier
) {
    SplitLayoutVertical(
        modifier = modifier,
        //insets = WindowInsets.safeDrawing,
        first = {
            TestComponentWindowInsets(
                modifier = Modifier.fillMaxSize(),
                title = "List",
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Start +
                            WindowInsetsSides.Vertical)
            )
        },
        second = {
            TestComponentWindowInsets(
                modifier = Modifier.fillMaxSize(),
                title = "Detail",
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.End +
                            WindowInsetsSides.Vertical
                )
            )
        }
    )
}
