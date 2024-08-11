package de.drick.compose.edgetoedgepreview

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    @get:Rule val composeTestRule = createComposeRule()

    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "w360dp-h640dp-xxhdpi")
    @Test
    fun addition_isCorrect() {
        composeTestRule.setContent {
            EdgeToEdgeTemplate(
                modifier = Modifier.fillMaxSize().testTag("edge_to_edge"),
                navMode = NavigationMode.ThreeButton,
                cameraCutoutMode = CameraCutoutMode.End,
                showInsetsBorder = false,
            ) {
                PreviewContentAppBar()
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("edge_to_edge").assertIsDisplayed()
        val bitmap = composeTestRule.onNodeWithTag("edge_to_edge")
            .captureToImage()
            .asAndroidBitmap()
            //.writeToTestStorage("testImage1")
        File("screenshot.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}


@Composable
private fun PreviewContentAppBar() {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        SampleLandscapeContentAppBar()
    } else {
        SamplePortraitContentAppBar()
    }
}


@Composable
private fun SamplePortraitContentAppBar() {
    Column(
        Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        TestComponentWindowInsets(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            title = "Main Content",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal +
                        WindowInsetsSides.Top
            )
        )
        TestComponentWindowInsets(
            modifier = Modifier.fillMaxWidth(),
            innerModifier = Modifier.height(80.dp),
            title = "Navigation Bar",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal +
                        WindowInsetsSides.Bottom
            )
        )
    }
}

@Composable
private fun SampleLandscapeContentAppBar() {
    Row(
        Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        TestComponentWindowInsets(
            modifier = Modifier
                .fillMaxHeight()
                .width(140.dp),
            //innerModifier = Modifier.height(80.dp),
            title = "Navigation Bar",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Start +
                        WindowInsetsSides.Vertical),
            rotatedText = true
        )
        TestComponentWindowInsets(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            title = "Main Content",
            windowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.End +
                        WindowInsetsSides.Vertical
            )
        )
    }
}