package de.drick.compose.edgetoedgepreviewchecklib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsOwner

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@Composable
actual fun rememberSemanticsOwner(): SemanticsOwner? {
    val view = LocalView.current
    return remember {
        (view as? androidx.compose.ui.platform.AndroidComposeView)
            ?.semanticsOwner
    }
}