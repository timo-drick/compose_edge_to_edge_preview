package de.drick.compose.edgetoedgetestlib

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toSize
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType

val WindowInsetsKey = SemanticsPropertyKey<TestWindowInsets>("WindowInsets")
var SemanticsPropertyReceiver.windowInsets by WindowInsetsKey

fun SemanticsNodeInteraction.assertWindowInsets(
    @InsetsType insetType: Int,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeVerticalScrollSides: Boolean = true,
    /**
     * Callback is executed when there is an overlap with the specified
     * inset type. insets: List<Rect> contains all insets that overlap.
     */
    onOverlap: (node: SemanticsNode, insets: List<Rect>) -> Unit = { _, _ -> },
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
        excludeVerticalScrollSides = excludeVerticalScrollSides,
        onOverlap = onOverlap
    )

    return this
}

fun SemanticsNodeInteractionCollection.assertWindowInsets(
    @InsetsType insetType: Int,
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
     * Callback is executed for every node that overlaps with the specified
     * inset type. insets: List<Rect> contains all insets that overlap.
     */
    onOverlap: (node: SemanticsNode, insets: List<Rect>) -> Unit = { _, _ -> },
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
        excludeVerticalScrollSides = excludeVerticalScrollSides,
        complainAboutNoWindowInsets = complainAboutNoWindowInsets,
        onOverlap = onOverlap
    )
    return this
}

data class OverlapResultItem(
    @InsetsType val insetType: Int,
    val node: SemanticsNode,
    val nodeBounds: Rect,
    val insetBounds: List<Rect>,
)

fun testWindowInsets(
    nodes: List<SemanticsNode>,
    generalErrorMessage: (SemanticsNode) -> String,
    @InsetsType insetType: Int,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeVerticalScrollSides: Boolean = true,
    /**
     * When a node is inside a vertical scrollable area only the horizontal sides are checked.
     * But if the content is fully scrolled down the top side is also checked.
     * And if the content is fully scrolled up the bottom side is also checked.
     * Horizontal scroll areas not supported yet!
     */
    excludeHorizontalScrollSides: Boolean = true,
    /**
     * When there are no window insets at all. There will be
     * an error message to turn on edge-to-edge design.
     */
    complainAboutNoWindowInsets: Boolean = true,
    /**
     * Take a screenshot when there is an overlap. Currently this is only created in the outputs
     * folder of the build directory. So i did not found a way to display it in the test report.
     */
    onOverlap: (node: SemanticsNode, insets: List<Rect>) -> Unit = { _, _ -> },
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
    val message = buildString {
        nodes.forEach { node ->
            val insetsList = filterInsets(insetType, windowInsets)
            val overlapResultList = mutableListOf<OverlapResultItem>()
            insetsList.forEach { insets ->
                de.drick.compose.edgetoedge.test.checkOverlap(
                    node = node,
                    insets = insets,
                    windowSize = windowInsets.windowSize.toSize(),
                    excludeVerticalScrollSides = excludeVerticalScrollSides,
                    excludeHorizontalScrollSides = excludeHorizontalScrollSides,
                ) { inset, nodeRect ->
                    //Overlap detected !
                    overlapResultList.add(OverlapResultItem(
                        insetType = insetType,
                        node = node,
                        nodeBounds = nodeRect,
                        insetBounds = listOf(inset)
                    ))
                }
            }
            if (overlapResultList.isNotEmpty()) {
                val insetsRect = overlapResultList.flatMap { it.insetBounds }
                onOverlap(node, insetsRect)
                append(generalErrorMessage(node))
                appendLine()
                val overlappingInsetTypes =
                    overlapResultList.joinToString { getNameFromWindowInsetType(it.insetType) }
                appendLine("[$overlappingInsetTypes] overlap with node!")
                val device = "${Build.MODEL} - ${Build.VERSION.RELEASE}"
                appendLine("Device: $device")
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

fun filterInsets(
    @InsetsType type: Int,
    recordedInsets: TestWindowInsets,
) = recordedInsets.insetList
    .filter { it.isVisible }
    .filter { type and it.type > 0 }
    .map { it.insetVisible }

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
    windowSize.width > 0 && windowSize.height > 0 &&
            insetList.any {
                it.insetIgnoringVisibility.getTop(density) > 0 ||
                it.insetIgnoringVisibility.getRight(density, ld) > 0 ||
                it.insetIgnoringVisibility.getBottom(density) > 0 ||
                it.insetIgnoringVisibility.getLeft(density, ld) > 0
            }
