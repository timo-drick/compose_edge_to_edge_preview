package de.drick.compose.devicerecording

import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import de.drick.compose.devicerecording.ui.theme.ComposeEdgeToEdgePreviewLibraryTheme
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.DeviceConfigurationUtils
import de.drick.compose.edgetoedgetestlib.TestRotation
import de.drick.compose.edgetoedgetestlib.initializeActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
class RecordWindowInsets(
    private val rotation: TestRotation,
    private val navigationMode: NavigationMode,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val config = DeviceConfigurationUtils()

    companion object {
        @JvmStatic
        @Parameters(name = "{0},{1}")
        fun data(): Collection<Array<Any>> = buildList {
            TestRotation.entries.forEach { rotation ->
                add(arrayOf(rotation, NavigationMode.ThreeButton))
                add(arrayOf(rotation, NavigationMode.Gesture))
            }
        }
    }

    @Before
    fun setup() {
        config.prepare {
            turnScreenOn()
            rotateScreen(rotation)
            setNavigationMode(navigationMode == NavigationMode.ThreeButton)
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
        val screenshotFileName = "uiAutomatorScreenshot_${rotation}_${navigationMode}"
        composeTestRule.setContent {
            initializeActivity {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
            recordedInsets = recordInsets("$screenshotFileName.png")
            ComposeEdgeToEdgePreviewLibraryTheme {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                )
            }
        }
        composeTestRule.waitForIdle()
        instrumentation.uiAutomation
            .takeScreenshot()
            .writeToTestStorage(screenshotFileName)
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
