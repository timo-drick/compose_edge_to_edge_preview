package de.drick.compose.edgetoedgepreviewlib

import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat

enum class InsetPos {
    LEFT, RIGHT, TOP, BOTTOM
}

interface InsetsDsl {
    fun setInset(
        left: Int = 0,
        top: Int = 0,
        right: Int = 0,
        bottom: Int = 0,
        @WindowInsetsCompat.Type.InsetsType type: Int,
        isVisible: Boolean
    ): Insets
    fun setInset(
        pos: InsetPos,
        @WindowInsetsCompat.Type.InsetsType type: Int,
        size: Int,
        isVisible: Boolean
    ): Insets
}

fun buildInsets(block: InsetsDsl.() -> Unit): WindowInsetsCompat {
    val dsl = object : InsetsDsl {
        val builder = WindowInsetsCompat.Builder()
        override fun setInset(
            pos: InsetPos,
            @WindowInsetsCompat.Type.InsetsType type: Int,
            size: Int,
            isVisible:
            Boolean
        ) = when (pos) {
            InsetPos.LEFT -> setInset(left = size, type = type, isVisible = isVisible)
            InsetPos.RIGHT -> setInset(right = size, type = type, isVisible = isVisible)
            InsetPos.TOP -> setInset(top = size, type = type, isVisible = isVisible)
            InsetPos.BOTTOM -> setInset(bottom = size, type = type, isVisible = isVisible)
        }


        override fun setInset(
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            type: Int,
            isVisible: Boolean
        ): Insets {
            val insets = Insets.of(left, top, right, bottom)
            if (isVisible) {
                builder.setInsets(type, insets)
            }
            builder.setInsetsIgnoringVisibility(type, insets)
            builder.setVisible(type, isVisible)
            return insets
        }
    }
    block(dsl)
    return dsl.builder.build()
}

/**
 * Copied and modified from the Android Jetpack Compose sources of the
 * DeviceConfigurationOverride.WindowInsets. I just reimplemented it to avoid having to include
 * the dependency of the androidx.compose.ui:ui-test library.
 */
@Composable
fun WindowInsetsInjector(
    windowInsets: WindowInsetsCompat,
    content: @Composable () -> Unit
) {
    val currentContent by rememberUpdatedState(content)
    val currentWindowInsets by rememberUpdatedState(windowInsets)
    AndroidView(
        factory = { context ->
            object : AbstractComposeView(context) {
                @Composable
                override fun Content() {
                    currentContent()
                }

                override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
                    for (i in 0 until childCount) {
                        getChildAt(i).dispatchApplyWindowInsets(
                            WindowInsets(currentWindowInsets.toWindowInsets())
                        )
                    }
                    return WindowInsetsCompat.CONSUMED.toWindowInsets()!!
                }

                /**
                 * Deprecated, but intercept the `requestApplyInsets` call via the deprecated
                 * method.
                 */
                @Deprecated("Deprecated in Java")
                override fun requestFitSystemWindows() {
                    dispatchApplyWindowInsets(WindowInsets(currentWindowInsets.toWindowInsets()!!))
                }
            }
        },
        update = { with(currentWindowInsets) { it.requestApplyInsets() } }
    )
}

