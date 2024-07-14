package de.drick.compose.edgetoedgepreview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreview.layout_experiments.consumeNonOverlappingInsets
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
fun InsetsTest() {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        //focusRequester.requestFocus()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Red)
            .onSizeChanged {
                Log.d("SmartInsets", "Size: $it")
            }
    ) {
        //SmartInsetsProvider(insets = WindowInsets.safeDrawing) { insetPadding ->
            Box(
                Modifier
                    //.padding(insetPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Blue)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .background(Color.Green)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                    val view = LocalView.current
                    val rootViewWidth = view.rootView.width
                    val rootViewHeight = view.rootView.height
                    Text("root view  : $rootViewWidth x $rootViewHeight density: ${density.density}")
                    InsetValues(WindowInsets.statusBars)
                    InsetValues(WindowInsets.navigationBars)
                    InsetValues(WindowInsets.captionBar)
                    InsetValues(WindowInsets.ime)
                    InsetValues(WindowInsets.displayCutout)
                    InsetValues(WindowInsets.tappableElement)
                    InsetValues(WindowInsets.systemGestures)
                    InsetValues(WindowInsets.waterfall)
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
                            //.windowInsetsPadding(WindowInsets.safeDrawing)
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
    insets: WindowInsets
) {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val left = insets.getLeft(density, direction) / density.density
    val right = insets.getRight(density, direction) / density.density
    val top = insets.getTop(density) / density.density
    val bottom = insets.getBottom(density) / density.density

    Text("insets: $insets -> ($left, $top, $right, $bottom)")
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun GreetingPreview() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
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
fun GreetingPreviewInverted() {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        isInvertedOrientation = true
    ) {
        ComposeLibrariesTheme {
            InsetsTest()
        }
    }
}