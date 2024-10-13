package de.drick.compose.edgetoedgetestlib

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

val WindowInsetsKey = SemanticsPropertyKey<TestWindowInsets>("WindowInsets")
var SemanticsPropertyReceiver.windowInsets by WindowInsetsKey

/**
 * Place this in your composable that
 * you want to test.
 */
@SuppressLint("ComposeModifierMissing")
@Composable
fun SemanticsWindowInsetsAnchor() {
    val insets = getTestWindowInsets()
    Spacer(modifier = Modifier.semantics {
        windowInsets = insets
    })
}

/**
 * Searches for the window insets in semantic tree.
 */
fun SemanticsNode.findWindowInsets(): TestWindowInsets? =
    root?.semanticsOwner?.rootSemanticsNode?.searchChildren()
private fun SemanticsNode.searchChildren(): TestWindowInsets? {
    children.forEach {
        return it.config.getOrElseNullable(WindowInsetsKey, defaultValue = { null })
            ?: it.searchChildren() //Recurse search
    }
    return null //Anchor not found!
}

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

fun checkOverlap(
    @InsetsType type: Int,
    recordedInsets: TestWindowInsets,
    bounds: Rect,
    sides: WindowInsetsSides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
): List<TestInsetEntry> {
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

@SuppressLint("UnsafeOptInUsageError")
private fun createOverlapScreenShot(
    fileName: String,
    screenShotRaw: Bitmap,
    insetBounds: List<Rect>,
    bounds: Rect,
    isRobolectricTest: Boolean
) {
    val screenShot = screenShotRaw.copy(checkNotNull(screenShotRaw.config), true)
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
    if (isRobolectricTest) {
        File("$fileName.png").outputStream().use {
            screenShot.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    } else {
        screenShot.writeToTestStorage(fileName)
    }
}

fun SemanticsNodeInteraction.assertWindowInsets(
    @InsetsType insetType: Int,
    isRobolectricTest: Boolean = false,
    messagePrefixOnError: (() -> String)? = null
): SemanticsNodeInteraction {
    val insetName = getNameFromWindowInsetType(insetType)
    var errorMessageOnFail = "Failed to assertWindowInsets: [$insetName])"
    if (messagePrefixOnError != null) {
        errorMessageOnFail = messagePrefixOnError() + "\n" + errorMessageOnFail
    }
    val node = fetchSemanticsNode(errorMessageOnFail)
    val recordedInsets = node.findWindowInsets()
    checkNotNull(recordedInsets) { "SemanticsWindowInsetsAnchor not found in semantics hierarchy!" }
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
        createOverlapScreenShot(
            fileName = fileName,
            screenShotRaw = screenShotRaw,
            insetBounds = overlappingBounds,
            bounds = node.boundsInWindow,
            isRobolectricTest = isRobolectricTest
        )
        val message = buildString {
            append(buildGeneralErrorMessage(errorMessageOnFail, this@assertWindowInsets, node))
            appendLine()
            val overlappingInsetTypes = overlapInsets.joinToString { getNameFromWindowInsetType(it.type) }
            appendLine("[$overlappingInsetTypes] overlap with node!")
        }
        throw AssertionError(message)
    }
    return this
}

fun SemanticsNodeInteractionCollection.assertAllWindowInsets(
    baseName: String = "screenshot",
    @InsetsType insetType: Int,
    isRobolectricTest: Boolean = false,
): SemanticsNodeInteractionCollection {
    val insetName = getNameFromWindowInsetType(insetType)
    val errorOnFail = "Failed to assertAllWindowInsets($insetName)"
    val nodes = fetchSemanticsNodes(errorMessageOnFail = errorOnFail)
    val windowInsets = nodes.first().findWindowInsets()
    checkNotNull(windowInsets) { """
        |SemanticsWindowInsetsAnchor not found in semantics hierarchy!
        |Please make sure you added SemanticsWindowInsetsAnchor in your composable.
        |Example:
        |> composeTestRule.setContent {
        |>     enableEdgeToEdge()
        |>  -> SemanticsWindowInsetsAnchor() <--
        |>     AppTheme {
        |>         ComposableToTest()
        |>     }
        |> }
        """.trimMargin()
    }
    check(windowInsets.isNotEmpty()) { """
        |Detected window insets are empty!
        |Maybe you forgot to enable edge to edge?
        |//TODO different example when in robolectric mode!
        |Example:
        |> composeTestRule.setContent {
        |> --> enableEdgeToEdge() <--
        |>     SemanticsWindowInsetsAnchor()
        |>     AppTheme {
        |>         ComposableToTest()
        |>     }
        |> }
    """.trimMargin()

    }
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val message = buildString {
        nodes.forEach { node ->
            //Search for traversal true parent
            val traversalGroupNode = findTraversalGroupNode(node)
            var sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
            var scrollPosition = 0f
            //TODO add horizontal traversal group
            traversalGroupNode?.let {
                it.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.let { verticalRange ->
                    //appendLine("Traversal node: ${traversalGroupNode.id} $verticalRange")
                    sides = WindowInsetsSides.Horizontal
                    val vPos = verticalRange.value()
                    scrollPosition = vPos
                    if (vPos <= 0f) sides += WindowInsetsSides.Top
                    if (vPos >= verticalRange.maxValue())
                        sides += WindowInsetsSides.Bottom
                }
            }
            val overlapInsets = checkOverlap(insetType, windowInsets, node.boundsInWindow, sides)
            if (overlapInsets.isNotEmpty()) {
                val overlappingBounds = overlapInsets.flatMap {
                    it.insetVisible.toBounds(
                        Size(
                            windowInsets.windowWidth.toFloat(),
                            windowInsets.windowHeight.toFloat()
                        ),
                        sides
                    )
                }
                val fileName = "${baseName}_node_${node.id}"
                //val screenShotRaw = root.captureToImage().asAndroidBitmap()
                val screenShotRaw = instrumentation.uiAutomation.takeScreenshot()
                createOverlapScreenShot(
                    fileName = fileName,
                    screenShotRaw = screenShotRaw,
                    insetBounds = overlappingBounds,
                    bounds = node.boundsInWindow,
                    isRobolectricTest = isRobolectricTest
                )
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
                val device = "${Build.MODEL} - ${Build.VERSION.RELEASE}"
                appendLine("Device: $device")
                val screenshotBaseFolder = "../../../outputs/connected_android_test_additional_output/debugAndroidTest/connected"
                val screenshotFile = "$screenshotBaseFolder/$device/$fileName.png"
                appendLine("""Screenshot: [$device] $fileName.png""")
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

private val density = Density(1f)
private val ld = LayoutDirection.Ltr

fun TestWindowInsets.isNotEmpty(): Boolean =
    windowWidth > 0 && windowHeight > 0 &&
            insetList.any {
                it.insetIgnoringVisibility.getTop(density) > 0 ||
                it.insetIgnoringVisibility.getRight(density, ld) > 0 ||
                it.insetIgnoringVisibility.getBottom(density) > 0 ||
                it.insetIgnoringVisibility.getLeft(density, ld) > 0
            }

fun WindowInsets.toBounds(
    windowSize: Size,
    sides: WindowInsetsSides
): List<Rect> = buildList {
    val leftSize = getLeft(density, ld).toFloat()
    if (leftSize > 0 && sides.intersect(WindowInsetsSides.Left)) {
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
    if (topSize > 0 && sides.intersect(WindowInsetsSides.Top)) {
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
    if (rightSize > 0 && sides.intersect(WindowInsetsSides.Right)) {
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
    if (bottomSize > 0 && sides.intersect(WindowInsetsSides.Bottom)) {
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