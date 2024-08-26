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
import de.telekom.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import de.telekom.edgetoedgetestlib.assertAllWindowInsets
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog
import java.io.File

enum class TestOrientation { PORTRAIT, LANDSCAPE }
enum class TestInvertedOrientation { NORMAL, INVERTED }

@RunWith(ParameterizedRobolectricTestRunner::class)
class EdgeToEdgeTestRobolectric(
    private val testOrientation: TestOrientation,
    private val testInvertedOrientation: TestInvertedOrientation,
    private val navigationMode: NavigationMode
) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0},{1},{2}")
        fun data() = buildList {
            TestOrientation.entries.forEach { orientation ->
                TestInvertedOrientation.entries.forEach { inverted ->
                    add(arrayOf(orientation, inverted, NavigationMode.ThreeButton))
                    add(arrayOf(orientation, inverted, NavigationMode.Gesture))
                }
            }
        }
    }

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out // Redirect Logcat to console
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun testWindowInsets() {
        when (testOrientation) {
            TestOrientation.PORTRAIT -> RuntimeEnvironment.setQualifiers("w360dp-h640dp-xxhdpi")
            TestOrientation.LANDSCAPE -> RuntimeEnvironment.setQualifiers("w640dp-h360dp-xxhdpi")
        }

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
        val base = "screenshot_${testOrientation}_$testInvertedOrientation"
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                baseName = base,
                isRobolectricTest = true
            )
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performScrollToBottom()
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAllWindowInsets(
                insetType = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
                baseName = "${base}_scrolled",
                isRobolectricTest = true
            )
        /*composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
            .assertAll(SemanticsMatcher.windowInsets())
        */
        val bitmap = composeTestRule.onNodeWithTag("edge_to_edge")
            .captureToImage()
            .asAndroidBitmap()
        //.writeToTestStorage("testImage1")
        File("screenshot.png").outputStream().use {
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