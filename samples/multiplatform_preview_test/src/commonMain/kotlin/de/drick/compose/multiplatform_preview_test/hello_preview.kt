package de.drick.compose.multiplatform_preview_test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate


@Composable
@Preview
fun PreviewHelloCommon() {
    EdgeToEdgeTemplate() {
        HelloCommon()
    }
}

@Composable
fun HelloCommon() {
    Scaffold(
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues).padding(16.dp)) {
                Text("Hello World")
            }
        }
    )

}