package de.drick.compose.edgetoedgepreviewlib

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

enum class InsetPos {
    LEFT, RIGHT, TOP, BOTTOM
}

interface InsetsDsl {
    fun setInset(pos: InsetPos, @WindowInsetsCompat.Type.InsetsType type: Int, size: Int, isVisible: Boolean)
}

fun buildInsets(block: InsetsDsl.() -> Unit): WindowInsetsCompat {
    val dsl = object : InsetsDsl {
        val builder = WindowInsetsCompat.Builder()
        override fun setInset(pos: InsetPos, @WindowInsetsCompat.Type.InsetsType type: Int, size: Int, isVisible: Boolean) {
            val insets = when (pos) {
                InsetPos.LEFT -> Insets.of(size, 0,0,0)
                InsetPos.RIGHT -> Insets.of(0, 0,size,0)
                InsetPos.TOP -> Insets.of(0, size,0,0)
                InsetPos.BOTTOM -> Insets.of(0, 0,0,size)
            }
            if (isVisible) {
                builder.setInsets(type, insets)
            }
            builder.setInsetsIgnoringVisibility(type, insets)
            builder.setVisible(type, isVisible)
        }
    }
    block(dsl)
    return dsl.builder.build()
}

@Composable
fun ViewInsetInjector(
    windowInsets: WindowInsetsCompat,
    useHiddenApiHack: Boolean = false,
    content: @Composable () -> Unit
) {
    if (useHiddenApiHack) {
        val windowInsetsState = rememberWindowInsetsState()
        LaunchedEffect(Unit) {
            windowInsetsState.update(windowInsets)
        }
        content()
    } else {
        AndroidView(
            factory = { ctx ->
                val view = ComposeView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setOnApplyWindowInsetsListener { _, _ ->
                        checkNotNull(windowInsets.toWindowInsets())
                    }
                    setContent(content)
                }
                view
            }
        )
    }
}
