package de.drick.compose.edgetoedgepreview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.tooling.preview.Preview
import de.drick.compose.edgetoedgepreviewchecklib.TestWindowInsets
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate


@Preview(
    device = "spec:width=411dp,height=891dp,orientation=landscape",
    fontScale = 1.0f
)
@Composable
private fun SemanticsPreview() {
    MaterialTheme {
        EdgeToEdgeTemplate {
            TestWindowInsets {
                onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
                    .checkOverlap(WindowInsets.safeDrawing)
                /*onAllNodes(hasClickAction())
                    .checkOverlap(WindowInsets.safeContent)
                 */
            }
            TestComposable()
        }
    }
}

@Preview(
    device = "spec:width=411dp,height=891dp,cutout=punch_hole,navigation=buttons",
    showSystemUi = true
)
@Composable
private fun SemanticsPreviewLadybug() {
    MaterialTheme {
        TestWindowInsets {
            onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
                .checkOverlap(WindowInsets.safeDrawing)
            onAllNodes(hasClickAction())
                .checkOverlap(WindowInsets.safeContent)
        }
        TestComposable()
    }
}

@Composable
fun TestComposable(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Text("Header", Modifier)
        Button(
            modifier = Modifier,
            onClick = {}
        ) {
            Icon(Icons.Outlined.Archive, contentDescription = "Archive icon")
            Text("Test button")
        }
    }
}
