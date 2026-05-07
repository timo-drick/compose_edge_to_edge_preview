package de.drick.compose.multiplatform_preview_test.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestComponentPreview() {
    TestComponent(
        title = "Test component",
        modifier = Modifier.fillMaxSize(),
        middleModifier = Modifier.padding(24.dp),
        innerModifier = Modifier.padding(16.dp)
    )
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