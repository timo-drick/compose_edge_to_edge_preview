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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.local.compose.icons.Icons_Filled_Circle
import androidx.local.compose.icons.Icons_Filled_Rectangle
import androidx.local.compose.icons.Icons_Navigation_Back

@Preview(name = "Navigation bar", device = "id:pixel_8")
@Composable
private fun PreviewNavigationBar() {
    NavigationBar(
        size = 50.dp,
        navMode = NavigationMode.ThreeButton,
    )
}
@Preview(name = "Navigation bar", device = "id:pixel_8")
@Composable
private fun PreviewNavigationBarRtl() {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        NavigationBar(
            size = 50.dp,
            navMode = NavigationMode.ThreeButton,
        )
    }
}
@Preview
@Composable
private fun PreviewNavigationBarLandscape() {
    NavigationBar(
        size = 50.dp,
        navMode = NavigationMode.ThreeButton,
        isVertical = true
    )
}

@Composable
fun NavigationBar(
    size: Dp,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
    isDarkMode: Boolean = true,
    navMode: NavigationMode = NavigationMode.ThreeButton,
    backgroundAlpha: Float = 0.5f,
) {
    val contentColor = if (isDarkMode) Color.LightGray else Color.DarkGray
    val backgroundColor = if (isDarkMode)
        Color.Black.copy(alpha = backgroundAlpha)
    else
        Color.White.copy(alpha = backgroundAlpha)
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
                IconNavigateBack(iconSize, contentColor)
                Spacer(Modifier.weight(1f))
                IconNavigateHome(iconSize, contentColor)
                Spacer(Modifier.weight(1f))
                IconNavigateHistory(iconSize, contentColor)
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
                IconNavigateBack(iconSize, contentColor)
                Spacer(Modifier.weight(1f))
                IconNavigateHome(iconSize, contentColor)
                Spacer(Modifier.weight(1f))
                IconNavigateHistory(iconSize, contentColor)
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun IconNavigateBack(
    iconSize: Dp,
    contentColor: Color
) {
    val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
    Image(
        modifier = Modifier
            .size(iconSize)
            .scale(if (ltr) -1f else 1f),
        imageVector = Icons_Navigation_Back,
        contentDescription = "Back",
        colorFilter = ColorFilter.tint(contentColor)
    )
}

@Composable
private fun IconNavigateHome(
    iconSize: Dp,
    contentColor: Color
) {
    Image(
        modifier = Modifier.size(iconSize - 8.dp),
        imageVector = Icons_Filled_Circle,
        contentDescription = "Home",
        colorFilter = ColorFilter.tint(contentColor)
    )
}

@Composable
private fun IconNavigateHistory(
    iconSize: Dp,
    contentColor: Color
) {
    Image(
        modifier = Modifier.size(iconSize - 4.dp),
        imageVector = Icons_Filled_Rectangle,
        contentDescription = "History",
        colorFilter = ColorFilter.tint(contentColor)
    )
}