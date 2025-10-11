package de.drick.compose.multiplatform_preview_test

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview


// Currently IntelliJ and Android Studio will not show any previews for jvm target!
// So here we will not see anything
@Preview
@Composable
fun Test() {
    Text("Hello Desktop")
}