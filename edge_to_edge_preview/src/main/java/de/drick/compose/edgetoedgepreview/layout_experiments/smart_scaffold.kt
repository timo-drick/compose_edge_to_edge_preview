package de.drick.compose.edgetoedgepreview.layout_experiments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreview.TestComponentWindowInsets
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Preview(
    name = "portrait",
    device = "spec:width=300dp,height=600dp,dpi=440"
)
@Composable
private fun PreviewEdgeToEdgePortrait2() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
    ) {
        SmartScaffold(
            topBar = {
                TestComponentWindowInsets(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Top bar",
                    innerModifier = Modifier.height(80.dp)
                )
            },
            bottomBar = {
                TestComponentWindowInsets(
                    modifier = Modifier.fillMaxWidth(),
                    innerModifier = Modifier.height(70.dp),
                    title = "Navigation Bar",
                )
            },
            content = {
                TestComponentWindowInsets(
                    modifier = Modifier.fillMaxSize(),
                    title = "Main content",
                    innerModifier = Modifier.fillMaxSize()
                )
            }
        )
    }
}

@Composable
fun SmartScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val consumedInsetsTopBar = remember { MutablePaddingValues() }
    val consumedInsetsBottomBar = remember { MutablePaddingValues() }
    val consumedInsetsContent = remember { MutablePaddingValues() }
    Layout(
        modifier = modifier,
        content = {
            Box(
                Modifier
                    .layoutId("topBar")
                    .consumeWindowInsets(consumedInsetsTopBar)
            ) {
                topBar()
            }
            Box(
                Modifier
                    .layoutId("bottomBar")
                    .consumeWindowInsets(consumedInsetsBottomBar)
            ) {
                bottomBar()
            }
            Box(
                Modifier
                    .layoutId("content")
                    .consumeWindowInsets(consumedInsetsContent)
            ) {
                content()
            }
        }
    ) { measurable, constraints ->
        val topBarMeasurable = measurable.first { it.layoutId == "topBar" }
        val bottomBarMeasurable = measurable.first { it.layoutId == "bottomBar" }
        val contentMeasurable = measurable.first { it.layoutId == "content" }
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val topBarPlaceable = topBarMeasurable.measure(constraints)
        val bottomBarPlaceable = bottomBarMeasurable.measure(constraints)
        val maxContentHeight = height - topBarPlaceable.height - bottomBarPlaceable.height
        val contentConstraints = constraints.copy(
            maxHeight = maxContentHeight,
            minHeight = constraints.minHeight
        )
        val contentPlaceable = contentMeasurable.measure(contentConstraints)
        //TODO calculate consumed insets
        consumedInsetsTopBar.bottom = topBarPlaceable.height.toDp()
        consumedInsetsContent.top = topBarPlaceable.height.toDp()
        consumedInsetsContent.bottom = bottomBarPlaceable.height.toDp()
        consumedInsetsBottomBar.top = bottomBarPlaceable.height.toDp()
        layout(width, height) {
            topBarPlaceable.place(0, 0)
            contentPlaceable.place(0, topBarPlaceable.height)
            bottomBarPlaceable.place(0, height - bottomBarPlaceable.height)
        }
    }
}
