package de.drick.compose.edgetoedgepreview

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.printToLog
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.drick.compose.edgetoedgetestlib.TestRotation
import de.drick.compose.edgetoedgetestlib.assertWindowInsets
import de.drick.compose.edgetoedgetestlib.createScreenshot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EdgeToEdgeTest(
    rotation: TestRotation,
    navigationMode: NavigationMode
): EdgeToEdgeTestBase(rotation, navigationMode) {

    @get:Rule
    override val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.setRotation(rotation.rotation)
        instrumentation.waitForIdleSync()
    }

    @Test
    fun testWindowInsets() {
        composeTestRule.setContent {
            SemanticsWindowInsetsAnchor()
            (LocalContext.current as ComponentActivity).apply {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= 29) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
            ComposeLibrariesTheme {
                InsetsTest()
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                onOverlap = { node, insetsRect ->
                    createScreenshot(
                        screenshotBaseName = "screenshot_$rotation",
                        node = node,
                        insetBounds = insetsRect
                    )
                }
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                onOverlap = { node, insetsRect ->
                    createScreenshot(
                        screenshotBaseName = "screenshot_${rotation}_scrolled",
                        node = node,
                        insetBounds = insetsRect
                    )
                }
            )
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */

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