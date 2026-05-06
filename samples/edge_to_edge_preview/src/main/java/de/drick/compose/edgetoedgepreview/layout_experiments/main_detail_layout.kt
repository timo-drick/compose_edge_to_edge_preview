package de.drick.compose.edgetoedgepreview.layout_experiments

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.lerp
import kotlin.math.max
import kotlin.math.min

@Composable
fun ListDetailLayout(
    tripList: @Composable () -> Unit,
    detail: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    detailFullscreen: Boolean = false
) {
    val density = LocalDensity.current
    //val screenLayout = LocalScreenLayout.current
    val detailVisible by animateFloatAsState(
        targetValue = if (detail != null) 1f else 0f,
        label = "detail visible",
        animationSpec = tween(1000)
    )
    val detailFullscreenMixer by animateFloatAsState(
        targetValue = if (detailFullscreen) 1f else 0f,
        label = "detail fullscreen",
        animationSpec = tween(1000)
    )
    val consumedInsetsFirst = remember { MutablePaddingValues() }
    val consumedInsetsSecond = remember { MutablePaddingValues() }
    Layout(
        modifier = modifier.clipToBounds(),
        content = {
            SmartInsetsConsumer(
                Modifier
                    .layoutId("first")
                //.consumeNonOverlappingInsets2()
                //.consumeWindowInsets(consumedInsetsFirst)
            ) {
                tripList()
            }
            SmartInsetsConsumer(
                Modifier
                    .layoutId("second")
                //.consumeNonOverlappingInsets2()
                //.consumeWindowInsets(consumedInsetsSecond)
            ) {
                if (detail!=null) detail()
            }
        }
    ) { measurable, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            //log("coordinates: ${coordinates?.localToWindow(Offset.Zero)}")
            val firstMeasurable = measurable.first { it.layoutId == "first" }
            val secondMeasurable = measurable.firstOrNull() { it.layoutId == "second" }
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val landscape = width > height

            if (landscape) {
                /*val foldX: Int? = screenLayout.foldingFeature?.let {
                    if (it.orientation == FoldingFeature.Orientation.VERTICAL && it.isSeparating) {
                        coordinates?.windowToLocal(Offset(it.bounds.left.toFloat(), 0f))?.x?.toInt()
                    } else {
                        null
                    }
                }*/
                val foldX: Int? = null
                val midPointAbsolute = if (foldX != null && foldX < width && foldX > 0) foldX else width / 2
                val midPointRelative = lerp(midPointAbsolute, 0, detailFullscreenMixer)
                val detailWidth = ((width - midPointRelative) * detailVisible).toInt()
                val listWidth = width - detailWidth
                val detailTargetSize = width - midPointRelative
                with(density) {
                    consumedInsetsFirst.end = detailWidth.toDp()
                    consumedInsetsSecond.start = listWidth.toDp()
                }
                if (listWidth > 0) {
                    val maxSize = max(midPointAbsolute, listWidth)
                    val firstPlaceable =
                        firstMeasurable.measure(constraints.copy(maxWidth = maxSize))
                    firstPlaceable.placeRelative(0, 0)
                }
                if (secondMeasurable != null) {
                    val secondPlaceable =
                        secondMeasurable.measure(constraints.copy(maxWidth = detailTargetSize))
                    secondPlaceable.placeRelative(listWidth, 0)
                }
            } else { //Portrait
                /*val foldY: Int? = screenLayout.foldingFeature?.let {
                    if (it.orientation == FoldingFeature.Orientation.HORIZONTAL && it.isSeparating) {
                        coordinates?.windowToLocal(Offset(0f, it.bounds.top.toFloat()))?.y?.toInt()
                    } else {
                        null
                    }
                }*/
                val foldY: Int? = null
                val midPointAbsolute = if (foldY != null && foldY < height && foldY > 0) foldY else height / 2
                val midPointRelative = lerp(midPointAbsolute, height, detailFullscreenMixer)
                val detailHeight = (midPointRelative * detailVisible).toInt()
                val listHeight = height - detailHeight
                //log("listHeight: $listHeight midpoint: $midPointRelative height: $height foldY:$foldY")

                consumedInsetsFirst.top = with(density) { detailHeight.toDp() }
                consumedInsetsSecond.bottom = with(density) { listHeight.toDp() }
                if (listHeight > 0) {
                    val maxSize = max(midPointAbsolute, listHeight)
                    val firstPlaceable =
                        firstMeasurable.measure(constraints.copy(maxHeight = maxSize))
                    firstPlaceable.place(0, min(detailHeight, midPointAbsolute))
                }
                if (secondMeasurable != null) {
                    val secondPlaceable =
                        secondMeasurable.measure(constraints.copy(maxHeight = midPointRelative))
                    secondPlaceable.place(0, detailHeight - midPointRelative)
                }
            }
        }
    }
}