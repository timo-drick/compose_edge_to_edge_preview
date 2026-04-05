package de.drick.compose.edgetoedgepreviewlib

import android.annotation.SuppressLint
import android.os.Build
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

/**
 *
 *  static final int FIRST = 1;
 *  static final int STATUS_BARS = FIRST;
 *  static final int NAVIGATION_BARS = 1 << 1;
 *  static final int CAPTION_BAR = 1 << 2;
 *
 *  static final int IME = 1 << 3;
 *
 *  static final int SYSTEM_GESTURES = 1 << 4;
 *  static final int MANDATORY_SYSTEM_GESTURES = 1 << 5;
 *  static final int TAPPABLE_ELEMENT = 1 << 6;
 *
 *  static final int DISPLAY_CUTOUT = 1 << 7;
 *
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
    //SYSTEM_OVERLAYS(WindowInsets.Type.systemOverlays()),
    //ALL(WindowInsets.Type.)
}

@WindowInsetsCompat.Type.InsetsType
internal fun InsetType.getTypeMask(): Int = when(this) {
    InsetType.STATUS_BARS -> WindowInsetsCompat.Type.statusBars()
    InsetType.NAVIGATION_BARS -> WindowInsetsCompat.Type.navigationBars()
    InsetType.CAPTION_BAR -> WindowInsetsCompat.Type.captionBar()
    InsetType.IME -> WindowInsetsCompat.Type.ime()
    InsetType.SYSTEM_GESTURES -> WindowInsetsCompat.Type.systemGestures()
    InsetType.MANDATORY_SYSTEM_GESTURES -> WindowInsetsCompat.Type.mandatorySystemGestures()
    InsetType.TAPPABLE_ELEMENT -> WindowInsetsCompat.Type.tappableElement()
    InsetType.DISPLAY_CUTOUT -> WindowInsetsCompat.Type.displayCutout()
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

fun buildInsets(block: InsetsDsl.() -> Unit): WindowInsetsCompat  {
    val dsl = object : InsetsDsl {
        val builder = WindowInsetsCompat.Builder()
        override fun setInset(
            pos: InsetPos,
            type: InsetType,
            size: Int,
            isVisible: Boolean
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
                builder.setInsets(type.getTypeMask(), insets)
            }
            builder.setInsetsIgnoringVisibility(type.getTypeMask(), insets)
            builder.setVisible(type.getTypeMask(), isVisible)
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

                @SuppressLint("NewApi")
                override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
                    for (i in 0 until childCount) {
                        getChildAt(i).dispatchApplyWindowInsets(
                            currentWindowInsets.toWindowInsets()
                        )
                    }
                    return WindowInsets.CONSUMED
                }

                /**
                 * Deprecated, but intercept the `requestApplyInsets` call via the deprecated
                 * method.
                 */
                @Deprecated("Deprecated in Java")
                override fun requestFitSystemWindows() {
                    currentWindowInsets.toWindowInsets()?.let {
                        dispatchApplyWindowInsets(it)
                    }
                }
            }
        },
        update = {
            if (Build.VERSION.SDK_INT >= 20) {
                it.requestApplyInsets()
            }
        }
    )
}

