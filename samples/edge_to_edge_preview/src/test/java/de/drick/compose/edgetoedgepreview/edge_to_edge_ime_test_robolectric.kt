package de.drick.compose.edgetoedgepreview

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.InsetsConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog
import java.io.File

@Config(sdk = [36])
@RunWith(RobolectricTestRunner::class)
class EdgeToEdgeImeTestRobolectric {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        ShadowLog.stream = System.out // Redirect Logcat to console
        RuntimeEnvironment.setQualifiers("w360dp-h640dp-xxhdpi")
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun imeModeVisibleInjectsImeInsetsAtBottom() {
        val imeSize = 250.dp
        var isImeVisible = false
        var imeBottomPx = -1
        var navigationBarBottomPx = -1
        var expectedImePx = -1
        composeTestRule.setContent {
            EdgeToEdgeTemplate(
                modifier = Modifier.testTag("edge_to_edge"),
                cfg = InsetsConfig(
                    imeMode = InsetMode.Visible,
                    imeSize = imeSize
                )
            ) {
                val density = LocalDensity.current
                expectedImePx = with(density) { imeSize.roundToPx() }
                isImeVisible = WindowInsets.isImeVisible
                imeBottomPx = WindowInsets.ime.getBottom(density)
                navigationBarBottomPx = WindowInsets.navigationBars.getBottom(density)
                Box(Modifier.fillMaxSize().background(Color.White)) {
                    // marker that should sit directly on top of the keyboard placeholder
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .imePadding()
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color.Red)
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        assertTrue("WindowInsets.isImeVisible should be true", isImeVisible)
        assertEquals("IME bottom inset", expectedImePx, imeBottomPx)
        assertTrue(
            "IME inset should cover the navigation bar (ime >= nav bar)",
            imeBottomPx >= navigationBarBottomPx
        )
        System.setProperty("robolectric.screenshot.hwrdr.native", "true")
        val bitmap = composeTestRule
            .onNodeWithTag("edge_to_edge")
            .captureToImage()
            .asAndroidBitmap()
        File("ime_visible_screenshot.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Test
    fun defaultConfigHasNoImeInsets() {
        var isImeVisible = true
        var imeBottomPx = -1
        composeTestRule.setContent {
            EdgeToEdgeTemplate(
                cfg = InsetsConfig()
            ) {
                isImeVisible = WindowInsets.isImeVisible
                imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
            }
        }
        composeTestRule.waitForIdle()
        assertFalse("WindowInsets.isImeVisible should be false by default", isImeVisible)
        assertEquals("IME bottom inset should be 0 by default", 0, imeBottomPx)
    }
}
