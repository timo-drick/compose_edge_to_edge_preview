package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Status bar")
@Composable
private fun PreviewStatusBar() {
    StatusBar(Modifier.height(24.dp))
}

@Composable
fun StatusBar(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val contentColor = if (isDarkMode) Color.White else Color.Black

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = "11:52",
            style = TextStyle.Default.copy(color = contentColor)
        )
        Spacer(Modifier.weight(1f))
        Image(
            imageVector = Icons.Default.Wifi,
            contentDescription = "Wifi icon",
            colorFilter = ColorFilter.tint(contentColor)
        )
        Image(
            imageVector = Icons.Default.BatteryChargingFull,
            contentDescription = "Wifi icon",
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}
