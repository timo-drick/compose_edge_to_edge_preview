package de.drick.compose.edgetoedgepreview

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import de.drick.compose.edgetoedgetestlib.SemanticsWindowInsetsAnchor
import org.junit.Rule
import org.junit.Test

class EdgeToEdgeTestPaparazzi {

    private val navigationMode = NavigationMode.ThreeButton
    private val testInvertedOrientation = TestInvertedOrientation.NORMAL

    @get:Rule val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_3A
    )

    @Test
    fun testWindowInsets() {
        paparazzi.snapshot {
            EdgeToEdgeTemplate(
                modifier = Modifier
                    .testTag("edge_to_edge"),
                navMode = navigationMode,
                cameraCutoutMode = CameraCutoutMode.End,
                isInvertedOrientation = testInvertedOrientation == TestInvertedOrientation.INVERTED,
            ) {
                SemanticsWindowInsetsAnchor()
                ComposeLibrariesTheme {
                    InsetsTest()
                }
            }
        }
    }
}
