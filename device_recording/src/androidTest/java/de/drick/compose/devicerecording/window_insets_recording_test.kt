package de.drick.compose.devicerecording

import android.app.UiAutomation
import android.os.Build
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import androidx.test.uiautomator.UiDevice
import de.drick.compose.devicerecording.ui.theme.ComposeEdgeToEdgePreviewLibraryTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test


class RecordWindowInsets {

    @get:Rule val composeTestRule = createAndroidComposeRule<RecordActivity>()

    //@get:Rule
    //val composeTestRule = createEmptyComposeRule()

    //private lateinit var scenario: ActivityScenario<MainActivity>

    /*@Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }*/

    @Test
    fun recordEdgeToEdge0() {
        recordScreen(UiAutomation.ROTATION_FREEZE_0)
    }
    @Test
    fun recordEdgeToEdge90() {
        recordScreen(UiAutomation.ROTATION_FREEZE_90)
    }
    @Test
    fun recordEdgeToEdge180() {
        recordScreen(UiAutomation.ROTATION_FREEZE_180)
    }
    @Test
    fun recordEdgeToEdge270() {
        recordScreen(UiAutomation.ROTATION_FREEZE_270)
    }

    private fun recordScreen(rotation: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val platformTestStorage = PlatformTestStorageRegistry.getInstance()
        val device = UiDevice.getInstance(instrumentation)
        instrumentation.uiAutomation.setRotation(rotation)
        instrumentation.waitForIdleSync()
        SystemClock.sleep(1000)

        val activity = composeTestRule.activity
        var recordedInsets: RecordedInsets? = null
        composeTestRule.setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SideEffect {
                    activity.window.isNavigationBarContrastEnforced = false
                }
            }
            ComposeEdgeToEdgePreviewLibraryTheme {
                recordedInsets = recordInsets()
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot()
            .captureToImage()
            .asAndroidBitmap()
            .writeToTestStorage("testImage1_$rotation")
        instrumentation.getUiAutomation()
            .takeScreenshot()
            .writeToTestStorage("uiAutomatorScreenshot_$rotation")
        recordedInsets?.let { insets ->
            val json = Json {
                prettyPrint = true
            }
            platformTestStorage.openOutputFile("test_$rotation.json").use { outputStream ->
                val string = json.encodeToString(insets)
                outputStream.write(string.toByteArray())
            }
        }


    }
}
