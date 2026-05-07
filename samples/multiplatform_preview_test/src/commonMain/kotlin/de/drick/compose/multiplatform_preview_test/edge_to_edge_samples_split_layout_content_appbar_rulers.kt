package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.multiplatform_preview_test.components.MutablePaddingValues
import de.drick.compose.multiplatform_preview_test.components.TestComponent
import de.drick.compose.multiplatform_preview_test.components.insetsAsPaddingValues
import de.drick.compose.multiplatform_preview_test.components.fitInsideHorizontal


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
    val rulers = WindowInsetsRulers.SafeDrawing.current
    Column(
        modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        val paddingNavBar = remember { MutablePaddingValues() }
        TestComponent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            middleModifier = Modifier.fitInside(rulers),
            title = "Main Content"
        )
        TestComponent(
            modifier = Modifier.fillMaxWidth().insetsAsPaddingValues(paddingNavBar, rulers),
            middleModifier = Modifier.padding(paddingNavBar),
            innerModifier = Modifier.height(70.dp),
            title = "Navigation Bar"
        )
    }
}

@Composable
private fun SampleLandscapeContentAppBar(
    modifier: Modifier = Modifier
) {
    val rulers = WindowInsetsRulers.SafeDrawing.current

    Row(
        modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        val paddingNavBar = remember { MutablePaddingValues() }
        TestComponent(
            modifier = Modifier.fillMaxHeight().insetsAsPaddingValues(paddingNavBar, rulers),
            middleModifier = Modifier.padding(paddingNavBar),
            innerModifier = Modifier.width(70.dp),
            title = "Navigation Bar",
            rotatedText = true
        )
        TestComponent(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            middleModifier = Modifier.fitInside(rulers),
            title = "Main Content"
        )
    }
}
