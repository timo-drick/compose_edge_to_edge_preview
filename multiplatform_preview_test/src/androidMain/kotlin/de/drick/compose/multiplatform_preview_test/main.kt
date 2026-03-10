package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate


@Preview
@Composable
fun Test() {
    EdgeToEdgeTemplate {
        Text(
            text = "Hello Desktop",
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        )
    }
}