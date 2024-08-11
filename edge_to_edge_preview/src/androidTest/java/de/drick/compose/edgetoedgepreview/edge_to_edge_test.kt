package de.drick.compose.edgetoedgepreview

import android.app.UiAutomation
import android.os.Build
import android.os.SystemClock
import android.view.WindowInsets
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.printToLog
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.telekom.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.telekom.edgetoedgetestlib.assertAllWindowInsets
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
        val activity = composeTestRule.activity
        composeTestRule.setContent {
            SemanticsWindowInsetsAnchor()
            SideEffect {
                if (Build.VERSION.SDK_INT >= 29) {
                    activity.window.isNavigationBarContrastEnforced = false
                }
            }
            ComposeLibrariesTheme {
                InsetsTest()
            }
        }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.setRotation(rotation)
        instrumentation.waitForIdleSync()

        SystemClock.sleep(1000)
        composeTestRule.waitForIdle()
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                root = composeTestRule.onRoot()
            )*/
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */
        val node = composeTestRule.onNodeWithTag("Test").fetchSemanticsNode()
        node.parent

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("ROOT_NODE")
    }
}

fun SemanticsNodeInteraction.performScrollToBottom(): SemanticsNodeInteraction {
    val node = fetchSemanticsNode()
    val maxValue = node.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.maxValue?.invoke()
    checkNotNull(maxValue)
    performSemanticsAction(SemanticsActions.ScrollBy) {
        it.invoke(0f, maxValue)
    }
    return this
}