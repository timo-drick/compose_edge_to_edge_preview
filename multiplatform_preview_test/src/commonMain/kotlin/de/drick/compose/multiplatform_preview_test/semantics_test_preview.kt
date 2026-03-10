package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
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
import de.drick.compose.edgetoedgepreviewlib.NavigationMode


@Preview(widthDp = 411, heightDp = 891)
@Composable
private fun SemanticsUnsafePreview() {
    SemanticsTest {
        TestComposable(WindowInsets.navigationBars)
    }
}
@Preview(widthDp = 411, heightDp = 891)
@Composable
private fun SemanticsSafeDrawingPreview() {
    SemanticsTest {
        TestComposable(WindowInsets.safeDrawing)
    }
}
@Preview(widthDp = 411, heightDp = 891)
@Composable
private fun SemanticsSafeContentPreview() {
    SemanticsTest {
        TestComposable(WindowInsets.safeContent)
    }
}


@Composable
private fun SemanticsTest(content: @Composable () -> Unit) {
    MaterialTheme {
        EdgeToEdgeTemplate(
            navMode = NavigationMode.Gesture
        ) {
            TestWindowInsets {
                onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
                    .checkOverlap(WindowInsets.safeDrawing)
                onAllNodes(hasClickAction())
                    .checkOverlap(WindowInsets.safeContent)
            }
            content()
        }
    }
}


@Composable
fun TestComposable(
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.windowInsetsPadding(windowInsets)
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
