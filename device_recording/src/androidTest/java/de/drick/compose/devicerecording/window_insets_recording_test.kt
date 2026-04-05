package de.drick.compose.devicerecording

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.graphics.set
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import de.drick.compose.edgetoedgetestlib.DeviceConfigurationUtils
import de.drick.compose.edgetoedgetestlib.TestRotation
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

enum class NavigationMode {
    ThreeButton, Gesture
}

enum class UiMode {
    Dark, Light
}

@RunWith(Parameterized::class)
class RecordWindowInsets(
    private val rotation: TestRotation,
    private val navigationMode: NavigationMode,
    private val uiMode: UiMode
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RecordActivity>()

    private val config = DeviceConfigurationUtils()

    companion object {
        @JvmStatic
        @Parameters(name = "{0},{1},{2}")
        fun data(): Collection<Array<Any>> = buildList {
            TestRotation.entries.forEach { rotation ->
                UiMode.entries.forEach { uiMode ->
                    add(arrayOf(rotation, NavigationMode.ThreeButton, uiMode))
                    add(arrayOf(rotation, NavigationMode.Gesture, uiMode))
                }
            }
            //add(arrayOf(TestRotation.Rotated90, NavigationMode.Gesture, UiMode.Dark))
        }
    }

    @Before
    fun setup() {
        config.prepare {
            turnScreenOn()
            rotateScreen(rotation)
            setNavigationMode(navigationMode == NavigationMode.ThreeButton)
            setDarkMode(uiMode == UiMode.Dark)
            sleep(1000)
        }
    }

    @After
    fun resetDevice() {
        config.restore()
    }

    @Test
    fun recordScreen() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val platformTestStorage = PlatformTestStorageRegistry.getInstance()

        var recordedInsets: RecordedInsets? = null
        val fileName = "display_${rotation}_${navigationMode}_$uiMode"

        val backgroundColor = if (uiMode == UiMode.Dark) {
            Color.Black.copy(green = 0.001f)
        } else {
            Color.White.copy(green = 0.999f)
        }
        composeTestRule.setContent {
            recordedInsets = recordInsets("$fileName.png")
            Box(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }
        composeTestRule.waitForIdle()

        SystemClock.sleep(1000)
        if (recordedInsets == null) throw IllegalStateException("Unable to record window insets!")
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        screenshot.writeToTestStorage(fileName)

        /*val width = screenshot.width
        val height = screenshot.height
        val transparentBitmap = Bitmap.createBitmap(width, height, screenshot.config!!)
        val transparentBackground = backgroundColor.copy(alpha = 0f).toArgb()
        val backgroundArgb = backgroundColor.toArgb()
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = screenshot.getColor(x, y).toArgb()
                if (pixelColor == backgroundArgb) {
                    // Set alpha to 0
                    transparentBitmap[x, y] = transparentBackground
                } else {
                    transparentBitmap[x, y] = pixelColor
                }
            }
        }
        val fillPaint = Paint()
        fillPaint.color = Color.Black
        fillPaint.style = PaintingStyle.Fill
        val strokePaint = Paint()
        strokePaint.color = Color.White
        strokePaint.style = PaintingStyle.Stroke
        strokePaint.strokeWidth = 4f
        Canvas(transparentBitmap.asImageBitmap()).apply {
            recordedInsets.displayCutoutPath?.let {
                drawPath(it, fillPaint)
                drawPath(it, strokePaint)
            }
            /*displayPath?.let {
                withClipOut(it) {
                    drawColor(android.graphics.Color.BLACK)
                    drawPath(it, strokePaint)
                }
            }*/
            recordedInsets.corners?.let { corners ->
                val rect = RoundRect(
                    left = 0f,
                    top = 0f,
                    right = width.toFloat(),
                    bottom = height.toFloat(),
                    topLeftCornerRadius = corners.topLeft.toCornerRadius(),
                    topRightCornerRadius = corners.topRight.toCornerRadius(),
                    bottomLeftCornerRadius = corners.bottomLeft.toCornerRadius(),
                    bottomRightCornerRadius = corners.bottomRight.toCornerRadius()
                )
                val path = Path().apply {
                    addRoundRect(
                        roundRect = rect,
                        direction = Path.Direction.Clockwise,
                    )
                }
                withClipOut(path) {
                    drawPath(path, fillPaint)
                    drawPath(path, strokePaint)
                }
            }
            /*cornerPath?.let {
                withClipOut(it) {
                    drawColor(android.graphics.Color.BLACK)
                    drawPath(it, strokePaint)
                }
            }*/
        }
        transparentBitmap.writeToTestStorage("${fileName}_alpha")
        */
        val json = Json {
            prettyPrint = true
        }
        platformTestStorage.openOutputFile("$fileName.json").use { outputStream ->
            val string = json.encodeToString(recordedInsets)
            outputStream.write(string.toByteArray())
        }

    }
}

inline fun Canvas.withClipOut(clipPath: Path, block: Canvas.() -> Unit) {
    save()
    clipPath(
        path = clipPath,
        clipOp = ClipOp.Difference
    )
    try {
        block()
    } finally {
        restore()
    }
}
