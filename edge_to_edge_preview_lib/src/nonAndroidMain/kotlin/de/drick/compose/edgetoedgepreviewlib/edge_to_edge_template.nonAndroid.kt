package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun EdgeToEdgeTemplate(
    cfg: InsetsConfig,
    modifier: Modifier,
    isDarkMode: Boolean,
    isLandscape: Boolean,
    content: @Composable () -> Unit
) {
    BasicText(
        text = "Edge-to-edge injection not supported for this platform!",
        modifier = Modifier.background(Color.White).padding(16.dp),
        color = { Color.Black }
    )
}

@Composable
@ReadOnlyComposable
actual fun isLandscape(): Boolean = true