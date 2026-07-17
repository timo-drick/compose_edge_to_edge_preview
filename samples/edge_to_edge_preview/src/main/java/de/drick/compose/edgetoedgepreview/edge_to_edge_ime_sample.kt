package de.drick.compose.edgetoedgepreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.drick.compose.edgetoedgepreview.ui.theme.ComposeLibrariesTheme
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.InsetsConfig

@GridScreenPreview
@Composable
private fun PreviewEdgeToEdgeImeThreeButtonNav() {
    EdgeToEdgeTemplate(
        cfg = InsetsConfig(imeMode = InsetMode.Visible)
    ) {
        ComposeLibrariesTheme {
            ImeSampleContent()
        }
    }
}

@GridScreenPreview
@Composable
private fun PreviewEdgeToEdgeImeGestureNav() {
    EdgeToEdgeTemplate(
        cfg = InsetsConfig.GestureNav.copy(imeMode = InsetMode.Visible)
    ) {
        ComposeLibrariesTheme {
            ImeSampleContent()
        }
    }
}

@Composable
private fun ImeSampleContent() {
    var text by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        Text(
            text = "Messages",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Type a message") }
        )
    }
}
