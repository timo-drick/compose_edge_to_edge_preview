package de.drick.compose.multiplatform_preview_test.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun TestComponentPreview() {

}

@Composable
fun TestComponent(
    title: String,
    modifier: Modifier = Modifier,
    middleModifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    rotatedText: Boolean = false
) {
    TestComponent(
        modifier = modifier
            .background(bgStripedGreen)
            .then(middleModifier)
            .background(bgStripedGrey)
            .then(innerModifier),
        title = title,
        style = style,
        rotatedText = rotatedText
    ) {
        //Text("${size.width}x${size.height} (${sizeInsets.width}x${sizeInsets.height})")
    }
}