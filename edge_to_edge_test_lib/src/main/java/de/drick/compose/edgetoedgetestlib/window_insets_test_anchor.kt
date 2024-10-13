package de.drick.compose.edgetoedgetestlib

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areNavigationBarsVisible
import androidx.compose.foundation.layout.areStatusBarsVisible
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarIgnoringVisibility
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isCaptionBarVisible
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.isTappableElementVisible
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsCompat


data class TestInsetEntry(
    val type: Int,
    val insetIgnoringVisibility: WindowInsets,
    val insetVisible: WindowInsets,
    val isVisible: Boolean
)

data class TestWindowInsets(
    val windowWidth: Int,
    val windowHeight: Int,
    val insetList: List<TestInsetEntry>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun getTestWindowInsets(): TestWindowInsets {
    //TODO optimize to not update on every recomposition
    val insetList = listOf(
        TestInsetEntry(
            type = WindowInsetsCompat.Type.statusBars(),
            insetIgnoringVisibility = WindowInsets.statusBarsIgnoringVisibility,
            insetVisible = WindowInsets.statusBars,
            isVisible = WindowInsets.areStatusBarsVisible
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.navigationBars(),
            insetIgnoringVisibility = WindowInsets.navigationBarsIgnoringVisibility,
            insetVisible = WindowInsets.navigationBars,
            isVisible = WindowInsets.areNavigationBarsVisible
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.ime(),
            insetIgnoringVisibility = WindowInsets.ime,
            insetVisible = WindowInsets.ime,
            isVisible = WindowInsets.isImeVisible
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.displayCutout(),
            insetIgnoringVisibility = WindowInsets.displayCutout,
            insetVisible = WindowInsets.displayCutout,
            isVisible = true
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.captionBar(),
            insetIgnoringVisibility = WindowInsets.captionBarIgnoringVisibility,
            insetVisible = WindowInsets.captionBar,
            isVisible = WindowInsets.isCaptionBarVisible
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.mandatorySystemGestures(),
            insetIgnoringVisibility = WindowInsets.mandatorySystemGestures,
            insetVisible = WindowInsets.mandatorySystemGestures,
            isVisible = true
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.systemGestures(),
            insetIgnoringVisibility = WindowInsets.systemGestures,
            insetVisible = WindowInsets.systemGestures,
            isVisible = true
        ),
        TestInsetEntry(
            type = WindowInsetsCompat.Type.tappableElement(),
            insetIgnoringVisibility = WindowInsets.tappableElementIgnoringVisibility,
            insetVisible = WindowInsets.tappableElement,
            isVisible = WindowInsets.isTappableElementVisible
        )
    )
    val size = currentWindowSize()
    return TestWindowInsets(
        windowWidth = size.width,
        windowHeight = size.height,
        insetList = insetList
    )
}
