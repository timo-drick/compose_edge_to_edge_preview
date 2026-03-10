package de.drick.compose.devicerecording

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.SystemClock
import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
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
        val screenshotFileName = "uiAutomatorScreenshot_${rotation}_${navigationMode}_$uiMode"

        val backgroundColor = if (uiMode == UiMode.Dark) {
            Color.Black.copy(green = 0.001f)
        } else {
            Color.White.copy(green = 0.999f)
        }
        var cutoutPath: Path? = null
        var displayPath: Path? = null
        var cornerPath: Path? = null
        composeTestRule.setContent {
            /*initializeActivity { // Only necessary when using ComponentActivity
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= 29) {
                    window.isNavigationBarContrastEnforced = false
                }
            }*/
            recordedInsets = recordInsets("$screenshotFileName.png")
            val width = recordedInsets.windowWidth
            val height = recordedInsets.windowHeight
            if (Build.VERSION.SDK_INT >= 31) {
                LocalView.current.rootWindowInsets.apply {
                    cutoutPath = displayCutout?.cutoutPath
                    if (Build.VERSION.SDK_INT >= 34) {
                        displayPath = displayShape?.path
                    }
                    val topLeft = getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0
                    val topRight = getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius ?: 0
                    val bottomLeft = getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: 0
                    val bottomRight = getRoundedCorner(RoundedCorner.POSITION_BOTTOM_RIGHT)?.radius ?: 0
                    val radiusArray = arrayOf(
                        topLeft, topLeft,
                        topRight, topRight,
                        bottomRight, bottomRight,
                        bottomLeft, bottomLeft
                    ).map { it.toFloat() }.toFloatArray()
                    cornerPath = Path().apply {
                        addRoundRect(
                            0f,
                            0f,
                            width.toFloat(),
                            height.toFloat(),
                            radiusArray,
                            Path.Direction.CW
                        )
                    }

                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }
        composeTestRule.waitForIdle()

        SystemClock.sleep(1000)
        
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        screenshot.writeToTestStorage("${screenshotFileName}_original")
        val width = screenshot.width
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
        fillPaint.color = android.graphics.Color.BLACK
        fillPaint.style = Paint.Style.FILL
        val strokePaint = Paint()
        strokePaint.color = android.graphics.Color.WHITE
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 4f
        Canvas(transparentBitmap).apply {
            cutoutPath?.let {
                drawPath(it, fillPaint)
                drawPath(it, strokePaint)
            }
            displayPath?.let {
                withClipOut(it) {
                    drawColor(android.graphics.Color.BLACK)
                    drawPath(it, strokePaint)
                }
            }
            cornerPath?.let {
                withClipOut(it) {
                    drawColor(android.graphics.Color.BLACK)
                    drawPath(it, strokePaint)
                }
            }
        }



        transparentBitmap.writeToTestStorage(screenshotFileName)
        recordedInsets?.let { insets ->
            val json = Json {
                prettyPrint = true
            }
            platformTestStorage.openOutputFile("test_${rotation}_${navigationMode}.json").use { outputStream ->
                val string = json.encodeToString(insets)
                outputStream.write(string.toByteArray())
            }
        }
    }
}

inline fun Canvas.withClipOut(clipPath: Path, block: Canvas.() -> Unit) {
    val checkpoint = save()
    clipOutPath(clipPath)
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}
