package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Preview(
    name = "portrait",
    group = "portrait",
    widthDp = 300,
    heightDp = 600,
)
@Preview(
    name = "landscape",
    group = "landscape",
    widthDp = 600,
    heightDp = 300,
)
annotation class SampleBlogPreviews

@SampleBlogPreviews
@Composable
private fun PreviewEdgeToEdgePortrait() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.End,
        showInsetsBorder = true,
    ) {
        PreviewContentAppBar()
    }
}

@SampleBlogPreviews
@Composable
private fun PreviewEdgeToEdgePortrait2() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
    ) {
        PreviewContentAppBar()
    }
}

@SampleBlogPreviews
@Composable
private fun PreviewEdgeToEdgePortrait4() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        isInvertedOrientation = true
    ) {
        PreviewContentAppBar()
    }
}

@Preview(
    name = "Desktop",
    widthDp = 700,
    heightDp = 500,
)
@Composable
private fun PreviewEdgeToEdgePortraitDesktop() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.None,
        statusBarMode = InsetMode.Off,
        navigationBarMode = InsetMode.Off,
        captionBarMode = InsetMode.Visible,
        showInsetsBorder = true,
        isInvertedOrientation = true
    ) {
        PreviewContentAppBar()
    }
}

@Composable
private fun PreviewContentAppBar() {
    if (isLandscape()) {
        SampleLandscapeContentAppBar()
    } else {
        SamplePortraitContentAppBar()
    }
}


@Composable
private fun SamplePortraitContentAppBar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        TestComponentWindowInsets(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            title = "Main Content",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal +
                        WindowInsetsSides.Top
            )
        )
        TestComponentWindowInsets(
            modifier = Modifier.fillMaxWidth(),
            innerModifier = Modifier.height(70.dp),
            title = "Navigation Bar",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal +
                        WindowInsetsSides.Bottom
            )
        )
    }
}

@Composable
private fun SampleLandscapeContentAppBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        TestComponentWindowInsets(
            modifier = Modifier.fillMaxHeight(),
            innerModifier = Modifier.width(70.dp),
            title = "Navigation Bar",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Start +
                        WindowInsetsSides.Vertical),
            rotatedText = true
        )
        TestComponentWindowInsets(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            title = "Main Content",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.End +
                        WindowInsetsSides.Vertical
            )
        )
    }
}
