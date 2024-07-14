package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview(name = "Navigation bar",
    device = "id:pixel_8"
)
@Composable
private fun PreviewNavigationBar() {
    NavigationBar(
        size = 50.dp,
        navMode = NavigationMode.ThreeButton
    )
}

@Composable
fun NavigationBar(
    size: Dp,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
    isDarkMode: Boolean = true,
    navMode: NavigationMode = NavigationMode.ThreeButton,
    alpha: Float = 0.5f,
) {
    val contentColor = if (isDarkMode) Color.LightGray else Color.DarkGray
    val backgroundColor = if (isDarkMode)
        Color.Black.copy(alpha = alpha)
    else
        Color.White.copy(alpha = alpha)
    val iconSize = 32.dp
    when {
        navMode == NavigationMode.Gesture -> {
            Row(
                modifier = modifier.height(size).fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Spacer(
                    Modifier
                        .background(color = contentColor, shape = RoundedCornerShape(4.dp))
                        .width(100.dp)
                        .height(6.dp)
                )
                Spacer(Modifier.weight(1f))
            }
        }
        isVertical -> {
            Column(
                modifier = modifier
                    .width(size)
                    .fillMaxHeight()
                    .background(backgroundColor)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(size),
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Home",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Rectangle,
                    contentDescription = "History",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
            }
        }
        else -> {
            Row(
                modifier = modifier
                    .height(size)
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Rectangle,
                    contentDescription = "History",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Home",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
                Image(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(contentColor)
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
