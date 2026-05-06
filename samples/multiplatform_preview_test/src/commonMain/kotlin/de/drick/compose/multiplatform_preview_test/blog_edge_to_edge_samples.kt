package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.multiplatform_preview_test.components.AppNavigationBar
import de.drick.compose.multiplatform_preview_test.components.ItemList
import de.drick.compose.multiplatform_preview_test.components.ItemListContentPadding
import de.drick.compose.multiplatform_preview_test.components.TestComponent
import de.drick.compose.multiplatform_preview_test.components.bgStripedGreen
import de.drick.compose.multiplatform_preview_test.components.bgStripedGrey
import de.drick.compose.multiplatform_preview_test.components.bgStripedRed


@Preview(
    widthDp = 411,
    heightDp = 838
)
annotation class GridScreenPreview

@Preview(
    widthDp = 838,
    heightDp = 411
)
annotation class GridLandscapeScreenPreview


@Composable
fun BaseLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        isNavigationBarContrastEnforced = false
    ) {
        Box(
            modifier
                .fillMaxSize()
                .background(Color.White)) {
            content()
        }
    }
}

@GridScreenPreview
@Composable
private fun TestLayoutNoPadding() {
    BaseLayout {
        Column {
            ItemList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            AppNavigationBar()
        }
    }
}

@GridScreenPreview
@Composable
private fun TestLayoutSimplePadding() {
    BaseLayout {
        Column(
            Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            LazyColumn() {

            }
            ItemList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            AppNavigationBar()
        }
    }
}

@GridScreenPreview
@Composable
private fun TestLayoutListContentPaddingNaive() {
    BaseLayout {
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
            //.windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            val contentPadding = WindowInsets.safeDrawing.asPaddingValues()
            ItemListContentPadding(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = contentPadding
            )
            TestComponent(
                modifier = Modifier
                    .background(bgStripedRed)
                    .height(contentPadding.calculateTopPadding())
                    .fillMaxWidth(),
                title = "Navigation Bar top padding",
                style = MaterialTheme.typography.headlineSmall
            )
            AppNavigationBar()
            TestComponent(
                modifier = Modifier
                    .background(bgStripedGreen)
                    .height(contentPadding.calculateBottomPadding())
                    .fillMaxWidth(),
                title = "Navigation Bar bottom padding",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}


@Preview
@Composable
private fun TestLayoutListContentPadding() {
    Box(Modifier.size(200.dp, 400.dp)) {
        BaseLayout {
            Column(
                modifier = Modifier
                    .background(color = Color.LightGray)
                //.windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                val listContentPadding = WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Top)
                    .asPaddingValues()
                ItemListContentPadding(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = listContentPadding
                )
                val navigationBarPadding = WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                TestComponent(
                    modifier = Modifier
                        .background(bgStripedRed)
                        .height(navigationBarPadding.calculateTopPadding())
                        .fillMaxWidth(),
                    title = "Navigation Bar top padding",
                    style = MaterialTheme.typography.headlineSmall
                )
                AppNavigationBar()
                TestComponent(
                    modifier = Modifier
                        .background(bgStripedGreen)
                        .height(navigationBarPadding.calculateBottomPadding())
                        .fillMaxWidth(),
                    title = "Navigation Bar bottom padding",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@GridLandscapeScreenPreview
@Composable
private fun TestLandscapeLayoutListContentPadding() {
    BaseLayout {
        Row(
            modifier = Modifier
                .background(color = Color.LightGray)
        ) {
            Column(
                Modifier.width(120.dp)
            ) {
                val navigationBarPadding = WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                TestComponent(
                    modifier = Modifier
                        .background(bgStripedGreen)
                        .height(navigationBarPadding.calculateTopPadding())
                        .fillMaxWidth(),
                    title = "Navigation Bar top padding",
                    style = MaterialTheme.typography.headlineSmall
                )
                AppNavigationBar(
                    modifier = Modifier.weight(1f),
                    isLandscape = true
                )
                TestComponent(
                    modifier = Modifier
                        .background(bgStripedGreen)
                        .height(navigationBarPadding.calculateBottomPadding())
                        .fillMaxWidth(),
                    title = "Navigation Bar bottom padding",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            val listContentPadding = WindowInsets.safeDrawing
                .only(WindowInsetsSides.Top)
                .asPaddingValues()
            ItemListContentPadding(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                contentPadding = listContentPadding
            )
        }
    }
}

@GridLandscapeScreenPreview
@Composable
private fun TestWindowInsetsAll() {
    BaseLayout {
        SplitLayoutVerticalNaive(
            modifier = Modifier.background(Color.LightGray),
            first = { modifier ->
                TestComponentWindowInsets(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(120.dp),
                    title = "App Navigation Bar",
                    rotatedText = true
                )
            },
            second = { modifier ->
                TestComponentWindowInsets(
                    modifier = modifier
                        .fillMaxHeight()
                        .weight(1f),
                    title = "Item list")
            }
        )
    }
}


@Composable
fun TestComponentWindowInsets(
    title: String,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    rotatedText: Boolean = false,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    contentPadding: PaddingValues? = null
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var sizeInsets by remember { mutableStateOf(IntSize.Zero) }
    val insetsPadding = if (contentPadding != null)
        Modifier.padding(contentPadding)
    else
        Modifier.windowInsetsPadding(windowInsets)
    TestComponent(
        modifier = modifier
            .onSizeChanged { sizeInsets = it }
            .background(bgStripedGreen)
            .then(insetsPadding)
            .background(bgStripedGrey)
            .onSizeChanged { size = it }
            .then(innerModifier),
        title = title,
        style = style,
        rotatedText = rotatedText
    ) {
        //Text("${size.width}x${size.height} (${sizeInsets.width}x${sizeInsets.height})")
    }
}
