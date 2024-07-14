@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package de.drick.compose.edgetoedgepreview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreview.layout_experiments.SplitLayoutVerticalNaive
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import kotlin.math.roundToInt


@Preview(
    widthDp = 411,
    heightDp = 838
)
annotation class GridPreviewScreen

@Preview(
    widthDp = 838,
    heightDp = 411
)
annotation class GridPreviewLandscapeScreen


@Composable
fun BaseLayout(
    content: @Composable () -> Unit
) {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = true,
        useHiddenApiHack = false
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)) {
            content()
        }
    }
}

@GridPreviewScreen
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

@GridPreviewScreen
@Composable
fun TestLayoutSimplePadding() {
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

@GridPreviewScreen
@Composable
fun TestLayoutListContentPaddingNaive() {
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


@GridPreviewScreen
@Composable
fun TestLayoutListContentPadding() {
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

@GridPreviewLandscapeScreen
@Composable
fun TestLandscapeLayoutListContentPadding() {
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

@GridPreviewLandscapeScreen
@Composable
fun TestWindowInsetsAll() {
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
fun ItemList(
    modifier: Modifier
) {
    TestComponent(
        modifier = modifier.background(bgStripedGrey),
        title = "Item List"
    )
}

private val bgStripedGreen = Brush.linearGradient(
    colors = listOf(Color.Green, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)
private val bgStripedRed = Brush.linearGradient(
    colors = listOf(Color.Red, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)
private val bgStripedGrey = Brush.linearGradient(
    colors = listOf(Color.LightGray, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)

@Composable
fun ItemListContentPadding(
    modifier: Modifier,
    contentPadding: PaddingValues
) {
    Column(modifier) {
        TestComponent(
            modifier = Modifier
                .background(bgStripedGreen)
                .height(contentPadding.calculateTopPadding())
                .fillMaxWidth(),
            title = "Item List top padding",
            style = MaterialTheme.typography.bodyLarge
        )
        TestComponent(
            modifier = Modifier
                .background(bgStripedGrey)
                .weight(1f)
                .fillMaxWidth(),
            title = "Item List"
        )
        TestComponent(
            modifier = Modifier
                .background(bgStripedRed)
                .height(contentPadding.calculateBottomPadding())
                .fillMaxWidth(),
            title = "Item List bottom padding",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}


@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    if (isLandscape) {
        TestComponent(
            modifier = modifier
                .background(bgStripedGrey)
                .fillMaxSize(),
            title = "App Navigation Bar",
            rotatedText = true
        )
    } else {
        TestComponent(
            modifier = modifier
                .background(bgStripedGrey)
                .height(120.dp)
                .fillMaxWidth(),
            title = "App Navigation Bar"
        )
    }
}


@Composable
fun insetsPaddingValues(insets: WindowInsets): PaddingValues {
    val density = LocalDensity.current
    var paddingValues by remember {
        mutableStateOf(insets.asPaddingValues(density))
    }
    // Using an invisible spacer to get access to a Modifier
    Spacer(
        Modifier.onConsumedWindowInsetsChanged {
            paddingValues = insets.exclude(it)
                .asPaddingValues(density)
        }
    )
    return paddingValues
}

@Composable
fun insetsExcludingConsumed(insets: WindowInsets): WindowInsets {
    var paddingValues by remember { mutableStateOf(insets) }
    // Using an invisible spacer here to just monitor the consumed
    Spacer(
        Modifier.onConsumedWindowInsetsChanged {
            paddingValues = insets.exclude(it)
        }
    )
    return paddingValues
}

@Composable
fun TestComponentWindowInsets(
    modifier: Modifier,
    innerModifier: Modifier = Modifier,
    title: String,
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

@Composable
fun TestComponent(
    modifier: Modifier,
    title: String,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    rotatedText: Boolean = false,
    childContent: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Black)
            .padding(2.dp),
    ) {
        if (rotatedText) {
            val tm = rememberTextMeasurer()
            Canvas(modifier = Modifier.fillMaxSize()) {
                val measuredText = tm.measure(
                    text = title,
                    style = style,
                    constraints = Constraints(maxWidth = size.height.roundToInt()),
                )
                val xOffset = (measuredText.size.height.toFloat() + size.width) / 2f
                val yOffset = (size.height - measuredText.size.width.toFloat()) / 2f
                rotate(
                    degrees = 90f,
                    pivot = Offset.Zero
                ) {
                    translate(
                        top = -xOffset,
                        left = yOffset
                    ) {
                        drawText(
                            textLayoutResult = measuredText
                        )
                    }
                }
            }
        } else {
            if (childContent == null) {
                Text(title, modifier = Modifier.align(Alignment.Center), style = style)
            } else {
                Text(title, modifier = Modifier.align(Alignment.Center), style = style)
                childContent()
            }
        }
    }
}