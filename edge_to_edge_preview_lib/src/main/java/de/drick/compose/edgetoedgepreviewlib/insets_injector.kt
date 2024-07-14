@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.layout.WindowInsetsHolder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.view.WindowInsetsCompat

interface WindowInsetsState {
    fun update(windowInsets: WindowInsetsCompat)
}


/**
 * This is a hack and may fail in future compose versions!
 */
private class WindowInsetsStateImpl(
    private val holder: WindowInsetsHolder
): WindowInsetsState {
    override fun update(windowInsets: WindowInsetsCompat) {
        holder.update(windowInsets, 0)
    }
}

@Composable
fun rememberWindowInsetsState(): WindowInsetsState {
    val insetsHolder = WindowInsetsHolder.current()
    return remember { WindowInsetsStateImpl(insetsHolder) }
}
