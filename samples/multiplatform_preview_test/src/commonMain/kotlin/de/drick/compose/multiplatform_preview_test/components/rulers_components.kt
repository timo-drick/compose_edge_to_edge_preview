package de.drick.compose.multiplatform_preview_test.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.RectRulers
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.roundToInt




fun Modifier.insetsAsPaddingValues(
    paddingValues: MutablePaddingValues,
    rulers: RectRulers
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val width = placeable.width
    val height = placeable.height
    layout(width, height) {
        val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
        val top = rulers.top.current(0f).roundToInt().fastCoerceIn(0, height)
        val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
        val bottom =
            rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)
        paddingValues.left = left.toDp()
        paddingValues.top = top.toDp()
        paddingValues.right = (width - right).toDp()
        paddingValues.bottom = (height - bottom).toDp()

        placeable.place(0, 0)
    }
}

fun Modifier.fitInsideHorizontal(rulers: RectRulers): Modifier = layout { measurable, constraints ->
    if (constraints.hasBoundedWidth) {
        val width = constraints.maxWidth
        val childConstraints = Constraints.fixedWidth(width)
        val placeable = measurable.measure(childConstraints)
        val height = placeable.height
        layout(width, height) {
            val left = rulers.left.current(0f).roundToInt().fastCoerceIn(0, width)
            val right = rulers.right.current(width.toFloat()).roundToInt().fastCoerceIn(0, width)
            val bottom =
                rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)
            val childConstraints = Constraints.fixed(right - left, height)
            val placeable = measurable.measure(childConstraints)
            placeable.place(left, height - bottom)
        }
    } else {
        throw IllegalStateException("Need to have bounded width constraints!")
    }
}

fun Modifier.fitInsideVertical(rulers: RectRulers): Modifier = layout { measurable, constraints ->
    if (constraints.hasBoundedHeight) {
        val height = constraints.maxHeight
        val childConstraints = Constraints.fixedHeight(height)
        val placeable = measurable.measure(childConstraints)
        val width = placeable.width
        layout(width, height) {
            val top = rulers.top.current(0f).roundToInt().fastCoerceIn(0, height)
            val bottom =
                rulers.bottom.current(height.toFloat()).roundToInt().fastCoerceIn(0, height)
            val childConstraints = Constraints.fixed(width, bottom - top)
            val placeable = measurable.measure(childConstraints)
            placeable.place(0, top)
        }
    } else {
        throw IllegalStateException("Need to have bounded height constraints!")
    }
}

class MutablePaddingValues: PaddingValues {
    var left by mutableStateOf(0.dp)
    var top by mutableStateOf(0.dp)
    var right by mutableStateOf(0.dp)
    var bottom by mutableStateOf(0.dp)

    override fun calculateBottomPadding() = bottom
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = left
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = right
    override fun calculateTopPadding() = top

    override fun toString() = "Padding left: $left top: $top right: $right bottom: $bottom"
}
