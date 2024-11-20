package de.drick.compose.edgetoedgetestlib

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.SemanticsNode
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry

fun createScreenshot(
    screenshotBaseName: String = "screenshot",
    node: SemanticsNode,
    insetBounds: List<Rect>,
) {
    val screenShotRaw = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
    val fileName = "${screenshotBaseName}_node_${node.id}"
    val overlapBitmap = createOverlapScreenShot(
        fileName = fileName,
        screenShotRaw = screenShotRaw,
        insetBounds = insetBounds,
        bounds = node.boundsInWindow
    )
    overlapBitmap.writeToTestStorage(fileName)
    /*if (fileName != null) {
        val screenshotBaseFolder =
            "../../../outputs/connected_android_test_additional_output/debugAndroidTest/connected"
        val screenshotFile = "$screenshotBaseFolder/$device/$fileName.png"
        appendLine("""Screenshot: [$device] $fileName.png""")
    }*/
}
fun createOverlapScreenShot(
    fileName: String,
    screenShotRaw: Bitmap,
    insetBounds: List<Rect>,
    bounds: Rect
): Bitmap {
    val screenShot = screenShotRaw.copy(checkNotNull(screenShotRaw.config), true)
    Canvas(screenShot.asImageBitmap()).apply {
        val paint = Paint().apply {
            color = Color.Red
            style = PaintingStyle.Stroke
            strokeWidth = 6f
        }
        val paintFill = Paint().apply {
            color = Color.Red
            alpha = 0.3f
            style = PaintingStyle.Fill
            strokeWidth = 6f
        }
        drawRect(bounds, paint)
        insetBounds.forEach {
            drawRect(it, paint)
            val intersection = it.intersect(bounds)
            drawRect(intersection, paintFill)
        }
    }
    return screenShot
}
