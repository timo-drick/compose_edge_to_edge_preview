package de.drick.compose.edgetoedgepreview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.DoNotInline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.test.filters.SdkSuppress
import androidx.test.platform.graphics.HardwareRendererCompat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@Composable
@SdkSuppress(minSdkVersion = 26)
fun captureScreenshot(): ImageBitmap {
    // TODO MultiWindowScreenshot

    val view = LocalView.current

    val dialogWindow: Window? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        findDialogWindowProviderInParent(view)?.window
    } else {
        null
    }


    val windowToUse = dialogWindow ?: view.context.getActivityWindow()

    val bounds = Rect(0, 0, view.width, view.height)
    return windowToUse.captureRegionToImage(bounds)
}

internal fun findDialogWindowProviderInParent(view: View): DialogWindowProvider? {
    if (view is DialogWindowProvider) {
        return view
    }
    val parent = view.parent ?: return null
    if (parent is View) {
        return findDialogWindowProviderInParent(parent)
    }
    return null
}

private fun Context.getActivityWindow(): Window {
    fun Context.getActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> throw IllegalStateException(
                "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
                        "An Activity context is required to get a Window instance"
            )
        }
    }
    return getActivity().window
}

@SdkSuppress(minSdkVersion = 26)
private fun Window.captureRegionToImage(
    boundsInWindow: Rect,
): ImageBitmap {
    // Turn on hardware rendering, if necessary
    return withDrawingEnabled {
        // Then we generate the bitmap
        generateBitmap(boundsInWindow).asImageBitmap()
    }
}

private fun <R> withDrawingEnabled(block: () -> R): R {
    val wasDrawingEnabled = HardwareRendererCompat.isDrawingEnabled()
    try {
        if (!wasDrawingEnabled) {
            HardwareRendererCompat.setDrawingEnabled(true)
        }
        return block.invoke()
    } finally {
        if (!wasDrawingEnabled) {
            HardwareRendererCompat.setDrawingEnabled(false)
        }
    }
}

@SdkSuppress(minSdkVersion = 26)
private fun Window.generateBitmap(boundsInWindow: Rect): Bitmap {
    val destBitmap =
        Bitmap.createBitmap(
            boundsInWindow.width(),
            boundsInWindow.height(),
            Bitmap.Config.ARGB_8888
        )
    generateBitmapFromPixelCopy(boundsInWindow, destBitmap)
    return destBitmap
}

@SdkSuppress(minSdkVersion = 26)
private object PixelCopyHelper {
    @DoNotInline
    fun request(
        source: Window,
        srcRect: Rect?,
        dest: Bitmap,
        listener: PixelCopy.OnPixelCopyFinishedListener,
        listenerThread: Handler
    ) {
        PixelCopy.request(source, srcRect, dest, listener, listenerThread)
    }
}

@SdkSuppress(minSdkVersion = 26)
private fun Window.generateBitmapFromPixelCopy(boundsInWindow: Rect, destBitmap: Bitmap) {
    val latch = CountDownLatch(1)
    var copyResult = 0
    val onCopyFinished = PixelCopy.OnPixelCopyFinishedListener { result ->
        copyResult = result
        latch.countDown()
    }
    PixelCopyHelper.request(
        this,
        boundsInWindow,
        destBitmap,
        onCopyFinished,
        Handler(Looper.getMainLooper())
    )

    if (!latch.await(1, TimeUnit.SECONDS)) {
        throw AssertionError("Failed waiting for PixelCopy!")
    }
    if (copyResult != PixelCopy.SUCCESS) {
        throw AssertionError("PixelCopy failed!")
    }
}