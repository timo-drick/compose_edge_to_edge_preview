package de.drick.compose.edgetoedgepreview

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.core.view.WindowInsetsCompat
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.drick.compose.edgetoedgetestlib.assertWindowInsets
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog
import java.io.File


enum class TestInvertedOrientation { NORMAL, INVERTED }
private val testQualifiersList = listOf(
    "w360dp-h640dp-xxhdpi",
    "w640dp-h360dp-xxhdpi"
)

@Config(sdk = [34])
@RunWith(ParameterizedRobolectricTestRunner::class)
class EdgeToEdgeTestRobolectric(
    private val testQualifiers: String,
    private val testInvertedOrientation: TestInvertedOrientation,
    private val navigationMode: NavigationMode
) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0},{1},{2}")
        fun data() = buildList {
            testQualifiersList.forEach { qualifiers ->
                TestInvertedOrientation.entries.forEach { inverted ->
                    add(arrayOf(qualifiers, inverted, NavigationMode.ThreeButton))
                    add(arrayOf(qualifiers, inverted, NavigationMode.Gesture))
                }
            }
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out // Redirect Logcat to console
        RuntimeEnvironment.setQualifiers(testQualifiers)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun testWindowInsets() {
        composeTestRule.setContent {
            EdgeToEdgeTemplate(
                modifier = Modifier
                    .testTag("edge_to_edge"),
                navMode = navigationMode,
                cameraCutoutMode = CameraCutoutMode.End,
                isInvertedOrientation = testInvertedOrientation == TestInvertedOrientation.INVERTED,
                useHiddenApiHack = true, //TODO make it work without this hack.
            ) {
                SemanticsWindowInsetsAnchor()
                ComposeLibrariesTheme {
                    InsetsTest()
                }
            }
        }
        val base = "screenshot_${testQualifiers}_$testInvertedOrientation"
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                screenshotBaseName = base,
                isRobolectricTest = true
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                screenshotBaseName = "${base}_scrolled",
                isRobolectricTest = true
            )
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */
        System.setProperty("robolectric.screenshot.hwrdr.native", "true")
        val bitmap = composeTestRule
            .onNodeWithTag("edge_to_edge")
            .captureToImage()
            .asAndroidBitmap()
        //val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        //.writeToTestStorage("testImage1")

        File("${base}_screenshot.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        //composeTestRule.onRoot(useUnmergedTree = true).printToLog("ROOT_NODE")
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