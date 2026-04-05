package de.drick.compose.edgetoedgepreviewlib

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.RectRulers
import androidx.compose.ui.layout.RulerScope
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.layout.layout
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

/**
 * Simulates [WindowInsetsRulers] values inside a Compose Preview by providing ruler values
 * via the [layout] modifier's ruler placement block using [androidx.compose.ui.layout.RulerScope.provides].
 */
fun Modifier.provideWindowInsetsRulers(
    insets: WindowInsetsCompat,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val width = placeable.width
    val height = placeable.height
    layout(
        width = width,
        height = height,
        rulers = {
            buildRules(insets) {
                WindowInsetsRulers.StatusBars provides InsetType.STATUS_BARS
                WindowInsetsRulers.NavigationBars provides InsetType.NAVIGATION_BARS
                WindowInsetsRulers.CaptionBar provides InsetType.CAPTION_BAR
                WindowInsetsRulers.Ime provides InsetType.IME
                WindowInsetsRulers.SystemGestures provides InsetType.SYSTEM_GESTURES
                WindowInsetsRulers.MandatorySystemGestures provides InsetType.MANDATORY_SYSTEM_GESTURES
                WindowInsetsRulers.TappableElement provides InsetType.TAPPABLE_ELEMENT
                WindowInsetsRulers.DisplayCutout provides InsetType.DISPLAY_CUTOUT
                val waterfallInsets = insets.displayCutout?.waterfallInsets ?: Insets.of(0, 0, 0, 0)
                WindowInsetsRulers.Waterfall.current provides waterfallInsets
                WindowInsetsRulers.Waterfall.maximum provides waterfallInsets
            }
        }
    ) {
        placeable.place(0, 0)
    }
}

interface RulerBuilderDsl {
    infix fun WindowInsetsRulers.provides(type: InsetType)
    infix fun RectRulers.provides(insets: Insets)
}

fun RulerScope.buildRules(insets: WindowInsetsCompat, block: RulerBuilderDsl.() -> Unit) {
    val impl = object : RulerBuilderDsl {
        override fun WindowInsetsRulers.provides(type: InsetType) {
            current provides insets.getInsets(type.getTypeMask())
            if (type != InsetType.IME) {
                maximum provides insets.getInsetsIgnoringVisibility(type.getTypeMask())
            }
        }
        override fun RectRulers.provides(insets: Insets) {
            if (Build.VERSION.SDK_INT < 30) return
            val (w, h) = coordinates.size
            top provides insets.top.toFloat()
            left provides insets.left.toFloat()
            bottom provides (h - insets.bottom.toFloat())
            right provides (w - insets.right.toFloat())
        }
    }
    block(impl)
}
