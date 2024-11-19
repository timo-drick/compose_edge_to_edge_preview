package de.drick.compose.edgetoedge.test

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

enum class InsetSides {
    Left, Top, Right, Bottom
}

fun SemanticsNode.searchChildren(
    matcher: SemanticsMatcher
) = buildList {
    Alignment.Top
    searchChildren(this, matcher)
}

fun SemanticsNode.searchChildren(
    list: MutableList<SemanticsNode>,
    matcher: SemanticsMatcher
) {
    children.forEach { node ->
        if (matcher.matches(node)) {
            list.add(node)
        }
        node.searchChildren(list, matcher)
    }
}

fun findVerticalScrollAxisRange(node: SemanticsNode): ScrollAxisRange? = node.config
    .getOrNull(SemanticsProperties.VerticalScrollAxisRange)
    ?: node.parent?.let {
        findVerticalScrollAxisRange(it)
    }

fun findHorizontalScrollAxisRange(node: SemanticsNode): ScrollAxisRange? = node.config
    .getOrNull(SemanticsProperties.HorizontalScrollAxisRange)
    ?: node.parent?.let {
        findHorizontalScrollAxisRange(it)
    }

fun checkOverlap(
    node: SemanticsNode,
    insets: WindowInsets,
    windowSize: Size,
    excludeVerticalScrollSides: Boolean = true,
    excludeHorizontalScrollSides: Boolean = true,
    overlap: (inset: Rect, node: Rect) -> Unit
) {
    val sides = InsetSides.entries.toMutableSet()
    if (excludeVerticalScrollSides) {
        findVerticalScrollAxisRange(node)?.let { verticalRange ->
            val vPos = verticalRange.value()
            if (vPos > 0f)
                sides.remove(InsetSides.Top)
            if (vPos < verticalRange.maxValue())
                sides.remove(InsetSides.Bottom)
        }
    }
    if (excludeHorizontalScrollSides) {
        findHorizontalScrollAxisRange(node)?.let { horizontalRange ->
            val hPos = horizontalRange.value()
            if (hPos > 0f)
                sides.remove(InsetSides.Left)
            if (hPos < horizontalRange.maxValue())
                sides.remove(InsetSides.Right)
        }
    }
    val nodeRect = node.boundsInWindow
    insets.toBounds(windowSize, sides).forEach { insetRect ->
        if (nodeRect.overlaps(insetRect)) {
            overlap(insetRect, nodeRect)
        }
    }
}

private val density = Density(1f)
private val ld = LayoutDirection.Ltr

fun WindowInsets.toBounds(
    windowSize: Size,
    sides: Set<InsetSides>
): List<Rect> = buildList {
    val leftSize = getLeft(density, ld).toFloat()
    if (leftSize > 0 && sides.contains(InsetSides.Left)) {
        add(
            Rect(
                left = 0f,
                top = 0f,
                right = leftSize,
                bottom = windowSize.height
            )
        )
    }
    val topSize = getTop(density).toFloat()
    if (topSize > 0 && sides.contains(InsetSides.Top)) {
        add(
            Rect(
                left = 0f,
                top = 0f,
                right = windowSize.width,
                bottom = topSize
            )
        )
    }
    val rightSize = getRight(density, ld).toFloat()
    if (rightSize > 0 && sides.contains(InsetSides.Right)) {
        add(
            Rect(
                left = windowSize.width - rightSize,
                top = 0f,
                right = windowSize.width,
                bottom = windowSize.height
            )
        )
    }
    val bottomSize = getBottom(density).toFloat()
    if (bottomSize > 0 && sides.contains(InsetSides.Bottom)) {
        add(
            Rect(
                left = 0f,
                top = windowSize.height - bottomSize,
                right = windowSize.width,
                bottom = windowSize.height
            )
        )
    }
}
