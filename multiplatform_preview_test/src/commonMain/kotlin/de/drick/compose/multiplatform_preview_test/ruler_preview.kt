package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fitInside
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.RectRulers
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastCoerceIn
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import kotlin.math.roundToInt


@Preview
@Preview(widthDp = 940, heightDp = 480)
@Composable
private fun PreviewRulerTest() {
    EdgeToEdgeTemplate(
        statusBarMode = InsetMode.Visible,
        navMode = NavigationMode.Gesture,
        navigationBarMode = InsetMode.Visible,
        cameraCutoutMode = CameraCutoutMode.Start,
        isInvertedOrientation = false,
        showInsetsBorder = true,
        isNavigationBarContrastEnforced = false
    ) {
        RulerTest()
    }
}

@Composable
private fun RulerTest() {
    val rulers = WindowInsetsRulers.SafeDrawing.current
    Box(
        Modifier.fillMaxSize()
            .fitInside(rulers)
            .background(Color.Green)
    )
    Box(
        Modifier.fillMaxSize()
            .fitOnTopOf(rulers)
            .background(Color.Red)
    )
    Box(
        Modifier.fillMaxSize()
            .fitAtBottomOf(rulers)
            .background(Color.Red)
    )
}

fun Modifier.fitOnTopOf(rulers: RectRulers): Modifier = layout { measurable, constraints ->
    if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        layout(width, height) {
            val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
            val top = rulers.top.current(0f).roundToInt().fastCoerceIn(0, height)
            val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
            val childConstraints = Constraints.fixed(right - left, top)
            val placeable = measurable.measure(childConstraints)
            placeable.place(left, 0)
        }
    } else {
        // Can't use the rulers because we don't know the size
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val height = placeable.height
        layout(width, height) {
            val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
            val top = rulers.top.current(0f).roundToInt().fastCoerceIn(0, height)
            val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
            val bottom =
                rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)
            // center in the available space
            placeable.place((left + right - width) / 2, (top + bottom - height) / 2)
        }
    }
}

fun Modifier.fitAtBottomOf(rulers: RectRulers): Modifier = layout { measurable, constraints ->
    if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        layout(width, height) {
            val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
            val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
            val bottom =
                rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)

            val childConstraints = Constraints.fixed(right - left, height - bottom)
            val placeable = measurable.measure(childConstraints)
            placeable.place(left, bottom)
        }
    } else {
        // Can't use the rulers because we don't know the size
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val height = placeable.height
        layout(width, height) {
            val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
            val top = rulers.top.current(0f).roundToInt().fastCoerceIn(0, height)
            val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
            val bottom =
                rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)
            // center in the available space
            placeable.place((left + right - width) / 2, (top + bottom - height) / 2)
        }
    }
}