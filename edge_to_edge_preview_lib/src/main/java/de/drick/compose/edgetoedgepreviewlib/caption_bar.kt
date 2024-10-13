package de.drick.compose.edgetoedgepreviewlib

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.local.compose.icons.Icons_AutoMirrored_Filled_ArrowBack
import androidx.local.compose.icons.Icons_Filled_Close
import androidx.local.compose.icons.Icons_Filled_Menu
import androidx.local.compose.icons.Icons_Filled_Minimize

@Preview(name = "Caption bar",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(name = "Caption bar",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PreviewCaptionBar() {
    val backgroundColor = if (isSystemInDarkTheme())
        Color.Black
    else
        Color.White
    Box(
        modifier = Modifier.background(backgroundColor)
    ) {
        CaptionBar(Modifier.height(24.dp))
    }
}

@Composable
fun CaptionBar(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val contentColor = if (isDarkMode) Color.White else Color.Black

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons_AutoMirrored_Filled_ArrowBack,
            contentDescription = "Back navigation",
            colorFilter = ColorFilter.tint(contentColor)
        )
        Spacer(Modifier.weight(1f))
        Image(
            imageVector = Icons_Filled_Minimize,
            contentDescription = "Minimize window",
            colorFilter = ColorFilter.tint(contentColor)
        )
        Image(
            imageVector = Icons_Filled_Menu,
            contentDescription = "Fullscreen",
            colorFilter = ColorFilter.tint(contentColor)
        )
        Image(
            imageVector = Icons_Filled_Close,
            contentDescription = "Close",
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}
