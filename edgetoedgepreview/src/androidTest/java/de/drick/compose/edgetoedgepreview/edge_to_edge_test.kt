package de.drick.compose.edgetoedgepreview

import android.app.UiAutomation
import android.os.Build
import android.os.SystemClock
import android.view.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EdgeToEdgeTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<TestActivity>()

    //@get:Rule val composeTestRule = createEmptyComposeRule()

    //private lateinit var scenario: ActivityScenario<MainActivity>

    private lateinit var windowInsets: WindowInsets

    @Before
    fun setup() {
        //scenario = ActivityScenario.launch(MainActivity::class.java)
        /*scenario.onActivity {
            val view = it.findViewById<View>(android.R.id.content)
            windowInsets = WindowInsets.Builder().build()
            view.setOnApplyWindowInsetsListener { v, insets ->
                windowInsets = insets
                insets
            }
        }*/
    }

    @Test
    fun edgeToEdge0() {
        testWindowInsets(UiAutomation.ROTATION_FREEZE_0)
    }
    @Test
    fun edgeToEdge90() {
        testWindowInsets(UiAutomation.ROTATION_FREEZE_90)
    }
    @Test
    fun edgeToEdge180() {
        testWindowInsets(UiAutomation.ROTATION_FREEZE_180)
    }
    @Test
    fun edgeToEdge270() {
        testWindowInsets(UiAutomation.ROTATION_FREEZE_270)
    }

    private fun testWindowInsets(rotation: Int) {
        var recordedInsets: RecordedInsets? = null
        val activity = composeTestRule.activity
        composeTestRule.setContent {
            SideEffect {
                if (Build.VERSION.SDK_INT >= 29) {
                    activity.window.isNavigationBarContrastEnforced = false
                }
            }
            ComposeLibrariesTheme {
                recordedInsets = recordInsets()
                InsetsTest(Modifier.semantics {
                    recordedInsets?.let {
                        windowInsets = it
                    }
                })
            }
        }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val platformTestStorage = PlatformTestStorageRegistry.getInstance()
        instrumentation.uiAutomation.setRotation(rotation)
        instrumentation.waitForIdleSync()

        SystemClock.sleep(1000)
        composeTestRule.waitForIdle()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                root = composeTestRule.onRoot()
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                root = composeTestRule.onRoot()
            )
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */
        val node = composeTestRule.onNodeWithTag("Test").fetchSemanticsNode()
        node.parent
        val position = node.positionInWindow
        val bounds = node.boundsInWindow

        platformTestStorage.openOutputFile("test_$rotation.json").use { outputStream ->
            val json = Json {
                prettyPrint = true
            }
            //val statusBars = windowInsets.getInsets(WindowInsets.Type.statusBars())
            val string = recordedInsets?.let { json.encodeToString(it) } ?: "NA"
            outputStream.write(string.toByteArray())
        }
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("ROOT_NODE")
    }
}

private val density = Density(1f)
private val ld = LayoutDirection.Ltr

fun androidx.compose.foundation.layout.WindowInsets.toBounds(
    windowSize: Size,
    sides: WindowInsetsSides
): List<Rect> = buildList {
    val leftSize = getLeft(density, ld).toFloat()
    if (leftSize > 0 && sides.intersect(WindowInsetsSides.Left)) {
        add(
            Rect(
                left = 0f,
                top = 0f,
                right = leftSize,
                bottom = windowSize.height
            )
        )
    }
    val topSize = getTop(density).toFloat()
    if (topSize > 0 && sides.intersect(WindowInsetsSides.Top)) {
        add(
            Rect(
                left = 0f,
                top = 0f,
                right = windowSize.width,
                bottom = topSize
            )
        )
    }
    val rightSize = getRight(density, ld).toFloat()
    if (rightSize > 0 && sides.intersect(WindowInsetsSides.Right)) {
        add(
            Rect(
                left = windowSize.width - rightSize,
                top = 0f,
                right = windowSize.width,
                bottom = windowSize.height
            )
        )
    }
    val bottomSize = getBottom(density).toFloat()
    if (bottomSize > 0 && sides.intersect(WindowInsetsSides.Bottom)) {
        add(
            Rect(
                left = 0f,
                top = windowSize.height - bottomSize,
                right = windowSize.width,
                bottom = windowSize.height
            )
        )
    }
}
