package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.local.compose.icons.Icons_Filled_Lens

enum class CameraCutoutMode {
    None, Middle, Start, End
}

@Preview(name = "Portrait")
@Composable
private fun PreviewCameraCutoutVertical() {
    val cutoutSize = 80.dp
    CameraCutout(
        cutoutSize = cutoutSize,
        cutoutMode = CameraCutoutMode.Middle,
        isVertical = true
    )
}
@Preview(name = "Portrait")
@Composable
private fun PreviewCameraCutoutVerticalHorizontal() {
    val cutoutSize = 80.dp
    CameraCutout(
        cutoutSize = cutoutSize,
        cutoutMode = CameraCutoutMode.Middle,
        isVertical = false
    )
}
@Composable
fun CameraCutout(
    modifier: Modifier = Modifier,
    cutoutMode: CameraCutoutMode = CameraCutoutMode.Middle,
    isVertical: Boolean = false,
    cutoutSize: Dp = 24.dp
) {
    if (cutoutMode == CameraCutoutMode.None) return
    if (isVertical) {
        val alignment = when (cutoutMode) {
            CameraCutoutMode.None -> Arrangement.Center
            CameraCutoutMode.Middle -> Arrangement.Center
            CameraCutoutMode.Start -> Arrangement.Top
            CameraCutoutMode.End -> Arrangement.Bottom
        }
        Column(
            modifier = modifier
                .width(cutoutSize)
                .padding(8.dp)
                .fillMaxHeight(),
            verticalArrangement = alignment,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                imageVector = Icons_Filled_Lens,
                contentDescription = "Camera lens",
                contentScale = ContentScale.FillWidth
            )
        }
    } else {
        val alignment = when (cutoutMode) {
            CameraCutoutMode.None -> Arrangement.Center
            CameraCutoutMode.Middle -> Arrangement.Center
            CameraCutoutMode.Start -> Arrangement.Start
            CameraCutoutMode.End -> Arrangement.End
        }
        Row(
            modifier = modifier
                .height(cutoutSize)
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = alignment,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.fillMaxHeight(),
                imageVector = Icons_Filled_Lens,
                contentDescription = "Camera lens",
                contentScale = ContentScale.FillHeight
            )
        }
    }
}
