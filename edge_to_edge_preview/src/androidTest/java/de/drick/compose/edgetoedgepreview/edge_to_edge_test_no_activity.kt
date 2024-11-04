package de.drick.compose.edgetoedgepreview

import android.os.Build
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.DeviceConfigurationUtils
import de.drick.compose.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.drick.compose.edgetoedgetestlib.TestRotation
import de.drick.compose.edgetoedgetestlib.assertWindowInsets
import de.drick.compose.edgetoedgetestlib.initializeActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters



@RunWith(Parameterized::class)
class TestClass(
    rotation: TestRotation,
    navigationMode: NavigationMode
): EdgeToEdgeTestBase(rotation, navigationMode) {

    override val composeTestRule = createComposeRule()

    @get:Rule
    val disableAnimationsRule = DisableAnimationsRule()

    @Test
    fun testWindowInsets() {
        composeTestRule.setContent {
            initializeActivity {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= 29) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
            SemanticsWindowInsetsAnchor()
            ComposeLibrariesTheme {
                InsetsTest()
            }
        }
        val visibleInsets = WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.displayCutout() or
                WindowInsetsCompat.Type.ime()
        val interactiveInsets = WindowInsetsCompat.Type.mandatorySystemGestures() or
                WindowInsetsCompat.Type.systemGestures() or
                WindowInsetsCompat.Type.tappableElement()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = visibleInsets,
                excludeVerticalScrollSides = true,
                screenshotBaseName = "screenshot_text_${rotation}_${navigationMode}",
            )
        composeTestRule
            .onAllNodes(hasClickAction())
            .assertWindowInsets(
                insetType = interactiveInsets,
                excludeVerticalScrollSides = true,
                screenshotBaseName = "screenshot_button_${rotation}_${navigationMode}",
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = visibleInsets,
                screenshotBaseName = "screenshot_scrolled_${rotation}_${navigationMode}",
            )
        composeTestRule
            .onAllNodes(hasClickAction())
            .assertWindowInsets(
                insetType = interactiveInsets,
                excludeVerticalScrollSides = true,
                screenshotBaseName = "screenshot_button_${rotation}_${navigationMode}",
            )
        val screenShotRaw = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        screenShotRaw.writeToTestStorage("screenshot_${rotation}_${navigationMode}")
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */
        val node = composeTestRule.onNodeWithTag("Test").fetchSemanticsNode()
        node.parent

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("ROOT_NODE")
    }
}

abstract class EdgeToEdgeTestBase(
    val rotation: TestRotation,
    val navigationMode: NavigationMode,
) {

    @get:Rule
    abstract val composeTestRule: ComposeTestRule

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
    fun prepare() {
        config.prepare {
            turnScreenOn()
            rotateScreen(rotation)
            setNavigationMode(navigationMode == NavigationMode.ThreeButton)
            fontSize(1.5f)
            displaySize(1.3f)
            sleep(500)
            demoStatusBar("clock -e hhmm 1200")
            demoStatusBar("battery -e level 69 -e plugged true -e powersave false")
            demoStatusBar("network -e fully true")
            demoStatusBar("network -e wifi show -e level 3 -e fully true")
            demoStatusBar("network -e mobile show -e datatype 5g -e level 2 -e fully true")
            demoStatusBar("notifications -e visible false")
            demoStatusBar("status -e bluetooth connected -e alarm show -e mute show -e sync show")
            sleep(500)
        }
    }

    @After
    fun resetDevice() {
        config.restore()
    }
}