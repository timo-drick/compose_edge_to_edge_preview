package de.drick.compose.edgetoedgepreview

import android.os.Build
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
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.drick.compose.edgetoedgetestlib.TestRotation
import de.drick.compose.edgetoedgetestlib.assertAllWindowInsets
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EdgeToEdgeTest(
    rotation: TestRotation,
    navigationMode: NavigationMode
): EdgeToEdgeTestNoActivity(rotation, navigationMode) {

    override val composeTestRule = createAndroidComposeRule<TestActivity>()

    //@get:Rule val composeTestRule = createEmptyComposeRule()

    //private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.setRotation(rotation.rotation)
        instrumentation.waitForIdleSync()
    }

    @Test
    fun testWindowInsets() {
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
        composeTestRule.waitForIdle()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                baseName = "screenshot_$rotation"
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                baseName = "screenshot_${rotation}_scrolled"
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