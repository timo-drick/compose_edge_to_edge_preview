package de.drick.compose.edgetoedgepreviewlib

import android.annotation.SuppressLint
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.Insets

enum class InsetPos {
    LEFT, RIGHT, TOP, BOTTOM
}


/**
 *  static final int FIRST = 1;
 *  static final int STATUS_BARS = FIRST;
 *  static final int NAVIGATION_BARS = 1 << 1;
 *  static final int CAPTION_BAR = 1
 *  static final int IME = 1
 *  static final int SYSTEM_GESTURES = 1 << 4;
 *  static final int MANDATORY_SYSTEM_GESTURES = 1 << 5;
 *  static final int TAPPABLE_ELEMENT = 1
 *  static final int DISPLAY_CUTOUT = 1
 *  static final int WINDOW_DECOR = 1 << 8;
 *  static final int SYSTEM_OVERLAYS = 1 << 9;
 *  static final int LAST = SYSTEM_OVERLAYS;
 *  static final int SIZE = 10;
 *
 */
enum class InsetType {
    STATUS_BARS,
    NAVIGATION_BARS,
    CAPTION_BAR,
    IME,
    SYSTEM_GESTURES,
    MANDATORY_SYSTEM_GESTURES,
    TAPPABLE_ELEMENT,
    DISPLAY_CUTOUT,
    //TEM_OVERLAYS(WindowInsets.Type.systemOverlays()),
    //ALL(WindowInsets.Type.)
}

@RequiresApi(30)
internal fun InsetType.getTypeMask30(): Int = when(this) {
    InsetType.STATUS_BARS -> WindowInsets.Type.statusBars()
    InsetType.NAVIGATION_BARS -> WindowInsets.Type.navigationBars()
    InsetType.CAPTION_BAR -> WindowInsets.Type.captionBar()
    InsetType.IME -> WindowInsets.Type.ime()
    InsetType.SYSTEM_GESTURES -> WindowInsets.Type.systemGestures()
    InsetType.MANDATORY_SYSTEM_GESTURES -> WindowInsets.Type.mandatorySystemGestures()
    InsetType.TAPPABLE_ELEMENT -> WindowInsets.Type.tappableElement()
    InsetType.DISPLAY_CUTOUT -> WindowInsets.Type.displayCutout()
}

interface InsetsDsl {
    fun setInset(
        left: Int = 0,
        top: Int = 0,
        right: Int = 0,
        bottom: Int = 0,
        type: InsetType,
        isVisible: Boolean
    ): Insets
    fun setInset(
        pos: InsetPos,
        type: InsetType,
        size: Int,
        isVisible: Boolean
    ): Insets
}

@SuppressLint("NewApi")
fun buildInsets(block: InsetsDsl.() -> Unit): WindowInsets = buildInsets30(block)/*if (Build.VERSION.SDK_INT >= 30) {
    buildInsets30(block)
} else {
    println("SDK version: ${Build.VERSION.SDK_INT}")
    if (Build.VERSION.SDK_INT == 0) { //Maybe we are in a screenshot test??
        buildInsets30(block)
    } else {
        TODO("VERSION.SDK_INT < 30")
    }
}*/

@RequiresApi(30)
fun buildInsets30(block: InsetsDsl.() -> Unit): WindowInsets {
    val dsl = object : InsetsDsl {
        val builder = WindowInsets.Builder()
        override fun setInset(
            pos: InsetPos,
            type: InsetType,
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
            type: InsetType,
            isVisible: Boolean
        ): Insets {
            val insets = Insets.of(left, top, right, bottom)
            if (isVisible) {
                builder.setInsets(type.getTypeMask30(), insets.toAndroidInsets())
            }
            builder.setInsetsIgnoringVisibility(type.getTypeMask30(), insets.toAndroidInsets())
            builder.setVisible(type.getTypeMask30(), isVisible)
            return insets
        }
    }
    block(dsl)
    return dsl.builder.build()
}

@RequiresApi(29)
fun Insets.toAndroidInsets() : android.graphics.Insets =
    android.graphics.Insets.of(left, top, right, bottom)

/**
 * Copied and modified from the Android Jetpack Compose sources of the
 * DeviceConfigurationOverride.WindowInsets. I just reimplemented it to avoid having to include
 * the dependency of the androidx.compose.ui:ui-test library.
 */
@Composable
fun WindowInsetsInjector(
    windowInsets: WindowInsets,
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

                @SuppressLint("NewApi")
                override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
                    for (i in 0 until childCount) {
                        getChildAt(i).dispatchApplyWindowInsets(
                            WindowInsets(currentWindowInsets)
                        )
                    }
                    return WindowInsets.CONSUMED
                    /*val sdk = Build.VERSION.SDK_INT
                    return when {
                        sdk >= 30 -> WindowInsets.CONSUMED
                        sdk == 0 -> WindowInsets.CONSUMED   // Maybe we are inside of a screenshot test
                        else -> WindowInsetsCompat.CONSUMED.toWindowInsets()!!
                    }*/
                }

                /**
                 * Deprecated, but intercept the `requestApplyInsets` call via the deprecated
                 * method.
                 */
                @Deprecated("Deprecated in Java")
                override fun requestFitSystemWindows() {
                    dispatchApplyWindowInsets(WindowInsets(currentWindowInsets))
                }
            }
        },
        update = { with(currentWindowInsets) { it.requestApplyInsets() } }
    )
}

