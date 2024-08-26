package de.drick.compose.edgetoedgepreview

import android.app.UiAutomation
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.telekom.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.telekom.edgetoedgetestlib.assertAllWindowInsets
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@Composable
fun enableEdgeToEdge(
): ComponentActivity? {
    val ctx = LocalContext.current
    return remember {
        (ctx as? ComponentActivity)?.apply { enableEdgeToEdge() }
    }
}

@Composable
fun rememberActivity(
    block: (ComponentActivity) -> Unit
): ComponentActivity? {
    val ctx = LocalContext.current
    return remember {
        (ctx as? ComponentActivity)?.apply { block(this) }
    }
}

fun rotateScreen(rotation: Int) {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val uiAutomation = instrumentation.uiAutomation
    uiAutomation.setRotation(rotation)
    /*uiAutomation.executeAndWaitForEvent(
        { uiAutomation.setRotation(rotation) },
        {
            it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        },
        1000
    )*/
    SystemClock.sleep(500)
    instrumentation.waitForIdleSync()
}

@RunWith(Parameterized::class)
class TestClass(rotation: TestRotation): EdgeToEdgeTestNoActivity(rotation) {

    override val composeTestRule = createComposeRule()

    @Test
    fun testWindowInsets() {
        composeTestRule.setContent {
            enableEdgeToEdge()
            SemanticsWindowInsetsAnchor()
            ComposeLibrariesTheme {
                InsetsTest()
            }
        }
        //composeTestRule.waitForIdle()
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
                baseName = "screenshot_$rotation",
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

enum class TestRotation(val rotation: Int) {
    Normal(UiAutomation.ROTATION_FREEZE_0),
    Rotated90(UiAutomation.ROTATION_FREEZE_90),
    Rotated180(UiAutomation.ROTATION_FREEZE_180),
    Rotated270(UiAutomation.ROTATION_FREEZE_270)
}

abstract class EdgeToEdgeTestNoActivity(val rotation: TestRotation) {

    @get:Rule
    abstract val composeTestRule: ComposeTestRule

    private var displayRotationBeforeTest = 0

    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> = buildList {
            TestRotation.entries.forEach { rotation ->
                add(arrayOf(rotation))
            }
        }
    }

    @Before
    fun prepare() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        displayRotationBeforeTest = device.displayRotation
        rotateScreen(rotation.rotation)
    }

    @After
    fun resetDevice() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val device = UiDevice.getInstance(instrumentation)
        instrumentation.uiAutomation.setRotation(displayRotationBeforeTest)
        //rotateScreen(UiAutomation.ROTATION_FREEZE_0)
        //rotateScreen(UiAutomation.ROTATION_UNFREEZE)
    }
}