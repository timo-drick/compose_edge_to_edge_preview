package de.drick.compose.edgetoedgepreview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SideEffect {
                if (Build.VERSION.SDK_INT >= 29) {
                    window.isNavigationBarContrastEnforced = false
                }
            }

            ComposeLibrariesTheme {
                // A surface container using the 'background' color from the theme
                InsetsTest()
                //SplitLayoutSample()
                //SplitLayoutRowSample()
            }
        }
    }
}


@Composable
fun InsetsTest(modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        //focusRequester.requestFocus()
    }
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Red)
            .onSizeChanged {
                Log.d("SmartInsets", "Size: $it")
            }
    ) {
        //SmartInsetsProvider(insets = WindowInsets.safeDrawing) { insetPadding ->
            val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
            Box(
                Modifier
                    //.padding(insetPadding)
                    .fadingEdge(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Blue)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                        .fillMaxSize()
                        .background(Color.Green)
                        .verticalScroll(rememberScrollState())
                        .padding(0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
                    Text("Title", modifier = Modifier.testTag("Test"))
                    /*Row {
                        Spacer(Modifier.weight(1f))
                        var text by remember { mutableStateOf("") }
                        TextField(
                            modifier = Modifier
                                .focusRequester(focusRequester),
                            value = text,
                            onValueChange = { text = it }
                        )
                    }*/
                    val density = LocalDensity.current
                    val layoutDirection = LocalLayoutDirection.current
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                    val screenWidthPx = with(density) { screenWidth.toPx().toInt() }
                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    val screenHeightPx = with(density) { screenHeight.toPx().toInt() }
                    val safeInsets = WindowInsets.safeDrawing
                    val insetsVertical = safeInsets.getTop(density) + safeInsets.getBottom(density)
                    val insetsHorizontal = safeInsets.getLeft(density, layoutDirection) + safeInsets.getRight(density, layoutDirection)
                    Text("screen size: $screenWidthPx x $screenHeightPx density: ${density.density}")
                    Text("screen size: ${screenWidthPx + insetsHorizontal} x ${screenHeightPx + insetsVertical} (including insets)")
                    val windowSize = currentWindowSize()
                    Text("Window size: ${windowSize.width} x ${windowSize.height}")
                    val view = LocalView.current
                    val rootViewWidth = view.rootView.width
                    val rootViewHeight = view.rootView.height
                    Text("root view  : $rootViewWidth x $rootViewHeight density: ${density.density}")
                    val navigationMode = LocalContext.current.navigationMode()
                    Text("navigation mode: $navigationMode")
                    InsetValues(WindowInsets.statusBars)
                    InsetValues(WindowInsets.navigationBars)
                    InsetValues(WindowInsets.captionBar)
                    InsetValues(WindowInsets.ime)
                    InsetValues(WindowInsets.displayCutout)
                    InsetValues(WindowInsets.tappableElement)
                    InsetValues(WindowInsets.systemGestures)
                    InsetValues(WindowInsets.waterfall)
                    Spacer(
                        modifier = Modifier
                            .testTag("last item")
                            .windowInsetsBottomHeight(WindowInsets.safeDrawing)
                    )
                }
                var animateStart by remember { mutableStateOf(false) }
                val detailVisible by animateFloatAsState(
                    targetValue = if (animateStart) 1f else 0f,
                    label = "animation started",
                    animationSpec = tween(2000)
                )
                val animSize = 150.dp
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { }
                        .offset(x = -animSize * detailVisible, y = -animSize * detailVisible)
                        .align(Alignment.BottomEnd),
                   // WindowInsets.safeDrawing
                ) { //insetsPadding ->
                    Row(
                        Modifier
                            //.consumeNonOverlappingInsets()
                            .smartInsets(WindowInsets.safeDrawing)
                            .background(Color.Red)
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                            //.padding(insetsPadding)
                            .background(Color.LightGray)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Test"
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { animateStart = animateStart.not() }) {
                            Text("Animate")
                        }
                    }
                }
                    /*Text(
                text = "Hello $name!",
                modifier = modifier
            )*/
            }
        //}
    }
}

fun Context.navigationMode(): NavigationMode? {
    return NavigationMode.entries.getOrNull(
        Settings.Secure.getInt(contentResolver, "navigation_mode", -1)
    )
}

fun Modifier.smartInsets(insets: WindowInsets) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        coordinates?.let {

        }
        placeable.place(IntOffset(x = 0, y = 0))
    }
}

@Composable
fun InsetValues(
    insets: WindowInsets,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val left = insets.getLeft(density, direction) / density.density
    val right = insets.getRight(density, direction) / density.density
    val top = insets.getTop(density) / density.density
    val bottom = insets.getBottom(density) / density.density

    Text("insets: $insets -> ($left, $top, $right, $bottom)", modifier)
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun GreetingPreview() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.Middle,
        isInvertedOrientation = false
    ) {
        ComposeLibrariesTheme {
            InsetsTest()
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun GreetingPreviewInverted() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.ThreeButton,
        cameraCutoutMode = CameraCutoutMode.Middle,
        isInvertedOrientation = true,
        isNavigationBarContrastEnforced = false
    ) {
        ComposeLibrariesTheme {
            InsetsTest()
        }
    }
}

fun Modifier.fadingEdge(paddingValues: PaddingValues) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val top = paddingValues
            .calculateTopPadding()
            .toPx()
        if (top > 0) {
            val fade = Brush.verticalGradient(
                //0.0f to Color.Black.copy(alpha = 0.3f),
                0.5f to Color.Black.copy(alpha = 0.3f),
                1.0f to Color.Black,
                endY = top
            )
            drawRect(
                brush = fade,
                size = Size(size.width, top),
                blendMode = BlendMode.DstIn
            )
        }
        val bottom = paddingValues
            .calculateBottomPadding()
            .toPx()
        if (bottom > 0) {
            val offset = size.height - bottom
            val fade = Brush.verticalGradient(
                0.0f to Color.Black,
                0.5f to Color.Black.copy(alpha = 0.3f),
                //1.0f to Color.Transparent,
                startY = offset,
                endY = size.height
            )
            drawRect(
                brush = fade,
                topLeft = Offset(0f, offset),
                size = Size(size.width, bottom),
                blendMode = BlendMode.DstIn
            )
        }
    }