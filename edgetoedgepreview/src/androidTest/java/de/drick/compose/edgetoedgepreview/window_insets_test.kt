package de.drick.compose.edgetoedgepreview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.performSemanticsAction
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.io.PlatformTestStorageRegistry

val WindowInsetsKey = SemanticsPropertyKey<RecordedInsets>("WindowInsets")
var SemanticsPropertyReceiver.windowInsets by WindowInsetsKey

@SuppressLint("ComposeModifierMissing")
@Composable
fun SemanticsWindowInsetsAnchor() {
    val recordedInsets = recordInsets()
    Spacer(modifier = Modifier.semantics {
        windowInsets = recordedInsets
    })
}

fun SemanticsNodeInteraction.performScrollToBottom(): SemanticsNodeInteraction {
    val node = fetchSemanticsNode()
    val maxValue = node.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.maxValue?.invoke()
    checkNotNull(maxValue)
    performSemanticsAction(SemanticsActions.ScrollBy) {
        it.invoke(0f, maxValue)
    }
    return this
}

fun checkOverlap(
    @InsetsType type: Int,
    recordedInsets: RecordedInsets,
    bounds: Rect,
    sides: WindowInsetsSides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
): List<InsetEntry> {
    val windowSize = Size(recordedInsets.windowWidth.toFloat(), recordedInsets.windowHeight.toFloat())
    return recordedInsets.insetList
        .filter { it.isVisible }
        .filter { type and it.type > 0 }
        .filter { insetEntry ->
            insetEntry.insetVisible.toBounds(windowSize, sides)
                .forEach {
                    if (it.overlaps(bounds)) {
                        return@filter true
                    }
                }
            false
        }
}

fun SemanticsNode.findWindowInsets(): RecordedInsets? {
    children.forEach {
        return it.config.getOrElseNullable(WindowInsetsKey, defaultValue = { null })
            ?: it.findWindowInsets()
    }
    return null
}

/*fun SemanticsMatcher.Companion.windowInsets(): SemanticsMatcher = SemanticsMatcher("Windows inset matcher") {
    val recordedInsets = it.root?.semanticsOwner?.rootSemanticsNode?.findWindowInsets()
    checkNotNull(recordedInsets) { "SemanticsWindowInsetsAnchor not found in semantics hierarchy" }
    checkOverlap(recordedInsets, it.boundsInWindow)
}*/

private fun createOverlapScreenShot(
    fileName: String,
    screenShotRaw: Bitmap,
    insetBounds: List<Rect>,
    bounds: Rect
): Uri {
    val screenShot = screenShotRaw.copy(screenShotRaw.config, true)
    Canvas(screenShot.asImageBitmap()).apply {
        val paint = Paint().apply {
            color = Color.Red
            style = PaintingStyle.Stroke
            strokeWidth = 6f
        }
        val paintFill = Paint().apply {
            color = Color.Red
            alpha = 0.3f
            style = PaintingStyle.Fill
            strokeWidth = 6f
        }
        drawRect(bounds, paint)
        insetBounds.forEach {
            drawRect(it, paint)
            val intersection = it.intersect(bounds)
            drawRect(intersection, paintFill)
        }
    }
    val platformTestStorage = PlatformTestStorageRegistry.getInstance()
    screenShot.writeToTestStorage(fileName)
    return platformTestStorage.getOutputFileUri(fileName)
}

fun SemanticsNodeInteraction.assertWindowInsets(
    @InsetsType insetType: Int,
    messagePrefixOnError: (() -> String)? = null
): SemanticsNodeInteraction {
    val insetName = getNameFromWindowInsetType(insetType)
    var errorMessageOnFail = "Failed to assertWindowInsets: [$insetName])"
    if (messagePrefixOnError != null) {
        errorMessageOnFail = messagePrefixOnError() + "\n" + errorMessageOnFail
    }
    val node = fetchSemanticsNode(errorMessageOnFail)
    val recordedInsets = node.root?.semanticsOwner?.rootSemanticsNode?.findWindowInsets()
    //TODO report this error
    checkNotNull(recordedInsets) { "SemanticsWindowInsetsAnchor not found in semantics hierarchy" }
    val instrumentation = InstrumentationRegistry.getInstrumentation()

    val overlapInsets = checkOverlap(insetType, recordedInsets, node.boundsInWindow)
    if (overlapInsets.isNotEmpty()) {
        val overlappingBounds = overlapInsets.flatMap {
            it.insetVisible.toBounds(
                windowSize = Size(recordedInsets.windowWidth.toFloat(), recordedInsets.windowHeight.toFloat()),
                sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
            )
        }
        val fileName = "screenshot_overlap_${node.id}"
        val screenShotRaw = instrumentation.uiAutomation.takeScreenshot()
        val uri = createOverlapScreenShot(fileName, screenShotRaw, overlappingBounds, node.boundsInWindow)
        val message = buildString {
            append(buildGeneralErrorMessage(errorMessageOnFail, this@assertWindowInsets, node))
            appendLine()
            val overlappingInsetTypes = overlapInsets.joinToString { getNameFromWindowInsetType(it.type) }
            appendLine("[$overlappingInsetTypes] overlap with node!")
            appendLine("screenshot: $uri")
        }
        throw AssertionError(message)
    }
    /*if (!matcher.matches(node)) {
        throw AssertionError(buildGeneralErrorMessage(errorMessageOnFail, selector, node))
    }*/
    return this
}

fun SemanticsNodeInteractionCollection.assertAllWindowInsets(
    @InsetsType insetType: Int,
    root: SemanticsNodeInteraction,
): SemanticsNodeInteractionCollection {
    val insetName = getNameFromWindowInsetType(insetType)
    val errorOnFail = "Failed to assertAllWindowInsets($insetName)"
    val nodes = fetchSemanticsNodes(errorMessageOnFail = errorOnFail)
    val recordedInsets = nodes.first().root?.semanticsOwner?.rootSemanticsNode?.findWindowInsets()
    //TODO report this error
    checkNotNull(recordedInsets) { "SemanticsWindowInsetsAnchor not found in semantics hierarchy" }
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val message = buildString {
        nodes.forEach { node ->
            //Search for traversal true parent
            fun findTraversalGroupNode(node: SemanticsNode): SemanticsNode? {
                val isTraversalGroup = node.config.getOrElse(SemanticsProperties.IsTraversalGroup, defaultValue = { false })
                return if (isTraversalGroup) {
                    node
                } else {
                    node.parent?.let {
                        findTraversalGroupNode(it)
                    }
                }
            }
            val traversalGroupNode = findTraversalGroupNode(node)
            var sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
            var scrollPosition = 0f
            traversalGroupNode?.let {
                it.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.let { verticalRange ->
                    //appendLine("Traversal node: ${traversalGroupNode.id} $verticalRange")
                    sides = WindowInsetsSides.Horizontal
                    val vPos = verticalRange.value()
                    scrollPosition = vPos
                    if ( vPos <= 0f) sides += WindowInsetsSides.Top
                    if (vPos >= verticalRange.maxValue())
                        sides += WindowInsetsSides.Bottom
                }
            }
            val overlapInsets = checkOverlap(insetType, recordedInsets, node.boundsInWindow, sides)
            if (overlapInsets.isNotEmpty()) {
                val overlappingBounds = overlapInsets.flatMap {
                    it.insetVisible.toBounds(
                        Size(
                            recordedInsets.windowWidth.toFloat(),
                            recordedInsets.windowHeight.toFloat()
                        ),
                        sides
                    )
                }
                val fileName = "screenshot_node_${node.id}"
                val screenShotRaw = root.captureToImage().asAndroidBitmap()//instrumentation.uiAutomation.takeScreenshot()
                createOverlapScreenShot(fileName, screenShotRaw, overlappingBounds, node.boundsInWindow)
                append(
                    buildGeneralErrorMessage(
                        "",
                        this@assertAllWindowInsets,
                        node
                    )
                )
                appendLine()
                val overlappingInsetTypes =
                    overlapInsets.joinToString { getNameFromWindowInsetType(it.type) }
                appendLine("[$overlappingInsetTypes] overlap with node!")
                appendLine("scroll position: $scrollPosition")
                appendLine()
            }
        }
    }
    if (message.isNotEmpty()) throw AssertionError(message)
    return this
}


fun getNameFromWindowInsetType(
    @InsetsType
    type: Int
): String {
    val insetTypes = buildList {
        if (type and WindowInsetsCompat.Type.statusBars() > 0) {
            add("statusBars")
        }
        if (type and WindowInsetsCompat.Type.navigationBars() > 0) {
            add("navigationBars")
        }
        if (type and WindowInsetsCompat.Type.captionBar() > 0) {
            add("captionBar")
        }
        if (type and WindowInsetsCompat.Type.ime() > 0) {
            add("ime")
        }
        if (type and WindowInsetsCompat.Type.displayCutout() > 0) {
            add("displayCutout")
        }
        if (type and WindowInsetsCompat.Type.mandatorySystemGestures() > 0) {
            add("mandatorySystemGestures")
        }
        if (type and WindowInsetsCompat.Type.systemGestures() > 0) {
            add("systemGestures")
        }
        if (type and WindowInsetsCompat.Type.tappableElement() > 0) {
            add("tappableElement")
        }
    }
    if (insetTypes.isEmpty()) throw IllegalArgumentException("Type: $type is unknown!")
    return insetTypes.joinToString(",")
}