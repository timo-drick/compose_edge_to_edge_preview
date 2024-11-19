package de.drick.compose.edgetoedgepreviewchecklib

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import de.drick.compose.edgetoedge.test.checkOverlap
import de.drick.compose.edgetoedge.test.searchChildren

private data class OverlapCheckData(
    val matcher: SemanticsMatcher,
    val insets: WindowInsets,
    val excludeVerticalScrollSides: Boolean,
    val excludeHorizontalScrollSides: Boolean,
)

@SuppressLint("ComposeModifierMissing")
@Composable
fun TestWindowInsets(
    overlapDsl: @Composable CheckOverlapDsl.() -> Unit
) {
    val semanticsOwner = rememberSemanticsOwner()
    val testList: List<OverlapCheckData> = buildList {
        val dsl = object : CheckOverlapDsl {
            override fun onAllNodes(matcher: SemanticsMatcher) = object : NodeInteractionDsl {
                override fun checkOverlap(
                    insets: WindowInsets,
                    excludeVerticalScrollSides: Boolean,
                    excludeHorizontalScrollSides: Boolean,
                ) {
                    add(
                        OverlapCheckData(
                            matcher = matcher,
                            insets = insets,
                            excludeVerticalScrollSides = excludeVerticalScrollSides,
                            excludeHorizontalScrollSides = excludeHorizontalScrollSides
                        )
                    )
                }
            }
        }
        overlapDsl(dsl)
    }
    var globalPos: LayoutCoordinates? by remember { mutableStateOf(null) }
    val windowSizeInt = currentWindowSize()
    Spacer(Modifier.safeDrawingPadding().fillMaxSize().onGloballyPositioned {
        globalPos = it
    }.zIndex(1000f).drawWithCache {
        val windowSize = windowSizeInt.toSize()
        val rootNode = checkNotNull(semanticsOwner?.rootSemanticsNode)
        val offset = globalPos?.windowToLocal(Offset.Zero) ?: Offset.Zero
        val colorInset = Color.Red.copy(alpha = 0.3f)
        val color = Color.Red
        val alphaColor = Color.Red.copy(alpha = 0.2f)
        onDrawWithContent {
            drawContent()
            testList.forEach { data ->
                rootNode.searchChildren(data.matcher).forEach { node ->
                    checkOverlap(
                        node = node,
                        insets = data.insets,
                        windowSize = windowSize,
                        excludeVerticalScrollSides = data.excludeVerticalScrollSides,
                        excludeHorizontalScrollSides = data.excludeHorizontalScrollSides,
                    ) { inset, content ->
                        val intersect = inset.intersect(content)
                        drawBox(colorInset, inset.translate(offset), style = Fill)
                        drawBox(color, content.translate(offset))
                        //drawBox(alphaColor, intersect.translate(offset), style = Fill)
                    }
                }
            }
        }
    })
}

fun ContentDrawScope.drawBox(
    color: Color,
    rect: Rect,
    style: DrawStyle = Stroke(width = 4f)
) {
    drawRect(
        color = color,
        topLeft = Offset(rect.left, rect.top),
        size = rect.size,
        style = style
    )
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@Composable
fun rememberSemanticsOwner(): SemanticsOwner? {
    val view = LocalView.current
    return remember {
        (view as? androidx.compose.ui.platform.AndroidComposeView)
            ?.semanticsOwner
    }
}

interface CheckOverlapDsl {
    fun onAllNodes(matcher: SemanticsMatcher): NodeInteractionDsl
}

interface NodeInteractionDsl {
    fun checkOverlap(
        insets: WindowInsets,
        excludeVerticalScrollSides: Boolean = true,
        excludeHorizontalScrollSides: Boolean = true,
    )
}
