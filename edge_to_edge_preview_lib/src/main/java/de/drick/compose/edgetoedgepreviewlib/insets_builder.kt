package de.drick.compose.edgetoedgepreviewlib

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