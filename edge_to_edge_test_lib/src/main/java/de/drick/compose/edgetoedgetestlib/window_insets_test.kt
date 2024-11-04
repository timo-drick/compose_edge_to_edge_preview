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
import androidx.compose.ui.semantics.ScrollAxisRange
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

fun findVerticalScrollAxisRange(node: SemanticsNode): ScrollAxisRange? = node.config
    .getOrNull(SemanticsProperties.VerticalScrollAxisRange)
    ?: node.parent?.let {
        findVerticalScrollAxisRange(it)
    }

fun findHorizontalScrollAxisRange(node: SemanticsNode): ScrollAxisRange? = node.config
    .getOrNull(SemanticsProperties.HorizontalScrollAxisRange)
    ?: node.parent?.let {
        findVerticalScrollAxisRange(it)
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
    /**
     * In robolectric tests the window insets are not working with the view injection
     * method. So here we use a hack to inject the window insets. Looks like it is working.
     */
    isRobolectricTest: Boolean = false,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeVerticalScrollSides: Boolean = true,
    /**
     * Take a screenshot when there is an overlap. Currently this is only created in the outputs
     * folder of the build directory. So i did not found a way to display it in the test report.
     */
    screenshotBaseName: String? = null,
): SemanticsNodeInteraction {
    val insetName = getNameFromWindowInsetType(insetType)
    val errorMessageOnFail = "Failed to assertWindowInsets: [$insetName])"

    val node = fetchSemanticsNode(errorMessageOnFail)
    testWindowInsets(
        nodes = listOf(node),
        generalErrorMessage = {
            buildGeneralErrorMessage(
                "",
                this,
                node
            )
        },
        insetType = insetType,
        isRobolectricTest = isRobolectricTest,
        excludeVerticalScrollSides = excludeVerticalScrollSides,
        screenshotBaseName = screenshotBaseName
    )

    return this
}

fun SemanticsNodeInteractionCollection.assertWindowInsets(
    @InsetsType insetType: Int,
    /**
     * In robolectric tests the window insets are not working with the view injection
     * method. So here we use a hack to inject the window insets. Looks like it is working.
     */
    isRobolectricTest: Boolean = false,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeVerticalScrollSides: Boolean = true,
    /**
     * When there are no window insets at all. There will be
     * an error message to turn on edge-to-edge design.
     */
    complainAboutNoWindowInsets: Boolean = true,
    /**
     * Take a screenshot when there is an overlap. Currently this is only created in the outputs
     * folder of the build directory. So i did not found a way to display it in the test report.
     */
    screenshotBaseName: String? = null,
    ): SemanticsNodeInteractionCollection {
    val insetName = getNameFromWindowInsetType(insetType)
    val errorOnFail = "Failed to assertWindowInsets($insetName)"
    val nodes = fetchSemanticsNodes(errorMessageOnFail = errorOnFail)
    testWindowInsets(
        nodes = nodes,
        generalErrorMessage = { node ->
            buildGeneralErrorMessage(
                "",
                this,
                node
            )
        },
        insetType = insetType,
        isRobolectricTest = isRobolectricTest,
        excludeVerticalScrollSides = excludeVerticalScrollSides,
        complainAboutNoWindowInsets = complainAboutNoWindowInsets,
        screenshotBaseName = screenshotBaseName
    )
    return this
}

fun testWindowInsets(
    nodes: List<SemanticsNode>,
    generalErrorMessage: (SemanticsNode) -> String,
    @InsetsType insetType: Int,
    /**
     * In robolectric tests the window insets are not working with the view injection
     * method. So here we use a hack to inject the window insets. Looks like it is working.
     */
    isRobolectricTest: Boolean = false,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeVerticalScrollSides: Boolean = true,
    /**
     * When there are no window insets at all. There will be
     * an error message to turn on edge-to-edge design.
     */
    complainAboutNoWindowInsets: Boolean = true,
    /**
     * Take a screenshot when there is an overlap. Currently this is only created in the outputs
     * folder of the build directory. So i did not found a way to display it in the test report.
     */
    screenshotBaseName: String? = null,
) {
    val windowInsets = checkWindowInsetsAnchor(nodes.first())
    if (complainAboutNoWindowInsets) {
        check(windowInsets.isNotEmpty()) {
            """
        |Detected window insets are empty!
        |Maybe you forgot to enable edge to edge?
        |//TODO different example when in robolectric mode!
        |Example:
        |> composeTestRule.setContent {
        |> --> (LocalContext.current as ComponentActivity).enableEdgeToEdge()  <--
        |>     SemanticsWindowInsetsAnchor()
        |>     AppTheme {
        |>         ScreenToTest()
        |>     }
        |> }
    """.trimMargin()
        }
    }
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val message = buildString {
        nodes.forEach { node ->
            var sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical
            var verticalScrollPosition: Float? = null
            if (excludeVerticalScrollSides) {
                //TODO add horizontal traversal group
                findVerticalScrollAxisRange(node)?.let { verticalRange ->
                    sides = WindowInsetsSides.Horizontal
                    val vPos = verticalRange.value()
                    verticalScrollPosition = vPos
                    if (vPos <= 0f) sides += WindowInsetsSides.Top
                    if (vPos >= verticalRange.maxValue())
                        sides += WindowInsetsSides.Bottom
                }
            }
            val overlapInsets = checkOverlap(insetType, windowInsets, node.boundsInWindow, sides)
            if (overlapInsets.isNotEmpty()) {
                val windowSize = Size(
                    windowInsets.windowWidth.toFloat(),
                    windowInsets.windowHeight.toFloat()
                )
                val overlappingBounds = overlapInsets
                    .flatMap {
                        it.insetVisible
                            .toBounds(windowSize, sides)
                            .filter { bounds -> bounds.overlaps(node.boundsInWindow) }
                }
                val fileName = screenshotBaseName?.let { "${it}_node_${node.id}" }
                if (fileName != null) {
                    //val screenShotRaw = root.captureToImage().asAndroidBitmap()
                    val screenShotRaw = instrumentation.uiAutomation.takeScreenshot()
                    createOverlapScreenShot(
                        fileName = fileName,
                        screenShotRaw = screenShotRaw,
                        insetBounds = overlappingBounds,
                        bounds = node.boundsInWindow,
                        isRobolectricTest = isRobolectricTest
                    )
                }
                append(generalErrorMessage(node))
                appendLine()
                val overlappingInsetTypes =
                    overlapInsets.joinToString { getNameFromWindowInsetType(it.type) }
                appendLine("[$overlappingInsetTypes] overlap with node!")
                if (verticalScrollPosition != null) {
                    appendLine("vertical scroll position: $verticalScrollPosition")
                }
                val device = "${Build.MODEL} - ${Build.VERSION.RELEASE}"
                appendLine("Device: $device")
                if (fileName != null) {
                    val screenshotBaseFolder =
                        "../../../outputs/connected_android_test_additional_output/debugAndroidTest/connected"
                    val screenshotFile = "$screenshotBaseFolder/$device/$fileName.png"
                    appendLine("""Screenshot: [$device] $fileName.png""")
                }
                appendLine()
            }
        }
    }
    if (message.isNotEmpty()) throw AssertionError(message)
}

private fun checkWindowInsetsAnchor(node: SemanticsNode): TestWindowInsets {
    val windowInsets = node.findWindowInsets()
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
    return windowInsets
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