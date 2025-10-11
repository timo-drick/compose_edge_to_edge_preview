package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsCompat

@Composable
fun HighlightInsets(
    windowInsets: WindowInsetsCompat,
    modifier: Modifier = Modifier,
    fill: Boolean = false
) {
    val insets = windowInsets.getInsets(-0x1)
    Spacer(
        modifier.fillMaxSize().drawWithCache {
            val leftSize = insets.left.toFloat()
            val topSize = insets.top.toFloat()
            val rightSize = insets.right.toFloat()
            val bottomSize = insets.bottom.toFloat()
            val color = Color.Red
            val alpha = .6f
            val rectStyle = Fill
            val lineStyle = Stroke(width = 4f)
            onDrawWithContent {
                drawContent()
                if (fill) {
                    drawRect(color, Offset.Zero, Size(leftSize, size.height), alpha, rectStyle)
                    drawRect(color, Offset.Zero, size = Size(size.width, topSize), alpha, rectStyle)
                    drawRect(color, Offset(size.width - rightSize, 0f), size = Size(rightSize, size.height), alpha, rectStyle)
                    drawRect(color, Offset(0f, size.height - bottomSize), size = Size(size.width, bottomSize), alpha, rectStyle)
                } else {
                    if (leftSize > 0)
                        drawLine(color, Offset(leftSize, 0f), Offset(leftSize, size.height), 5f)
                    if (rightSize > 0)
                        drawLine(color, Offset(size.width - rightSize, 0f), Offset(size.width - rightSize, size.height), 5f)
                    if (topSize > 0)
                        drawLine(color, Offset(0f, topSize), Offset(size.width, topSize), 5f)
                    if (bottomSize > 0)
                        drawLine(color, Offset(0f, size.height - bottomSize), Offset(size.width, size.height - bottomSize), 5f)
                }
            }
        }
    )
}

private val mockInsetsPixel7aGesture = buildInsets {
    setInset(
        top = 118,
        type = WindowInsetsCompat.Type.statusBars(),
        isVisible = true
    )
    setInset(
        bottom = 63,
        type = WindowInsetsCompat.Type.navigationBars(),
        isVisible = true
    )
    setInset(
        top = 118,
        type = WindowInsetsCompat.Type.displayCutout(),
        isVisible = true
    )
    setInset(
        top = 150,
        bottom = 84,
        type = WindowInsetsCompat.Type.mandatorySystemGestures(),
        isVisible = true
    )
    setInset(
        left = 78,
        top = 150,
        right = 78,
        bottom = 84,
        type = WindowInsetsCompat.Type.systemGestures(),
        isVisible = true
    )
    setInset(
        top = 118,
        type = WindowInsetsCompat.Type.tappableElement(),
        isVisible = true
    )

}
@Preview
@Composable
private fun PreviewHighlightInsets() {
    HighlightInsets(mockInsetsPixel7aGesture, Modifier.fillMaxSize())
}