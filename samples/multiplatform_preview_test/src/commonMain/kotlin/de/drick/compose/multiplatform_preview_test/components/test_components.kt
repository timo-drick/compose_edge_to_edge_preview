package de.drick.compose.multiplatform_preview_test.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ItemList(
    modifier: Modifier = Modifier
) {
    TestComponent(
        modifier = modifier.background(bgStripedGrey),
        title = "Item List"
    )
}

val bgStripedGreen = Brush.linearGradient(
    colors = listOf(Color.Green, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)
val bgStripedRed = Brush.linearGradient(
    colors = listOf(Color.Red, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)
val bgStripedGrey = Brush.linearGradient(
    colors = listOf(Color.LightGray, Color.White),
    start = Offset(0f, 20f),
    end = Offset(20f, 0f),
    tileMode = TileMode.Repeated
)

@Composable
fun ItemListContentPadding(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
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
fun TestComponent(
    title: String,
    modifier: Modifier = Modifier,
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
