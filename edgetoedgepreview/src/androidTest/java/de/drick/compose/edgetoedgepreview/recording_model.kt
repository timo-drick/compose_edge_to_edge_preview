package de.drick.compose.edgetoedgepreview

import android.os.Build
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areNavigationBarsVisible
import androidx.compose.foundation.layout.areStatusBarsVisible
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.captionBarIgnoringVisibility
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isCaptionBarVisible
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.isTappableElementVisible
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowInsetsCompat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class InsetEntry(
    @WindowInsetsCompat.Type.InsetsType
    val type: Int,
    @Serializable(with = WindowInsetsSerializer::class)
    val insetIgnoringVisibility: WindowInsets,
    @Serializable(with = WindowInsetsSerializer::class)
    val insetVisible: WindowInsets,
    val isVisible: Boolean
)


@Serializable
data class WindowInsetsJson(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

private val density = Density(1f)
private val ld = LayoutDirection.Ltr

object WindowInsetsSerializer : KSerializer<WindowInsets> {
    override val descriptor: SerialDescriptor = WindowInsetsJson.serializer().descriptor

    override fun serialize(encoder: Encoder, value: WindowInsets) {
        val json = WindowInsetsJson(
            left = value.getLeft(density, ld),
            top = value.getTop(density),
            right = value.getRight(density, ld),
            bottom = value.getBottom(density)
        )
        encoder.encodeSerializableValue(WindowInsetsJson.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): WindowInsets {
        val jsonInsets = decoder.decodeSerializableValue(WindowInsetsJson.serializer())
        return WindowInsets(jsonInsets.left, jsonInsets.top, jsonInsets.right, jsonInsets.bottom)
    }
}

fun InsetEntry.getTypeString(): String {
    val typeName = when (type) {
        WindowInsetsCompat.Type.statusBars() -> "statusBars()"
        WindowInsetsCompat.Type.navigationBars() -> "navigationBars()"
        WindowInsetsCompat.Type.ime() -> "ime()"
        WindowInsetsCompat.Type.displayCutout() -> "displayCutout()"
        WindowInsetsCompat.Type.captionBar() -> "captionBar()"
        WindowInsetsCompat.Type.mandatorySystemGestures() -> "mandatorySystemGestures()"
        WindowInsetsCompat.Type.systemGestures() -> "systemGestures()"
        WindowInsetsCompat.Type.tappableElement() -> "tappableElement()"
        else -> throw IllegalArgumentException("Type: $type is unknown!")
    }
    return "WindowInsetsCompat.Type.$typeName"
}

@Serializable
data class RecordedInsets(
    val manufacturer: String,
    val model: String,
    val apiLevel: Int,
    val windowWidth: Int,
    val windowHeight: Int,
    val density: Float,
    val orientation: Int,
    val insetList: List<InsetEntry>
)


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun recordInsets(): RecordedInsets {
    //TODO optimize to not update on every recomposition


    // format the string AppsLauncher/Android/<Country>/<Manufacturer>/<Model>/<OS_Version>/<AppsLauncher_Version>/-/-/-/-/-/-

    val insetList = listOf(
        InsetEntry(
            type = WindowInsetsCompat.Type.statusBars(),
            insetIgnoringVisibility = WindowInsets.statusBarsIgnoringVisibility,
            insetVisible = WindowInsets.statusBars,
            isVisible = WindowInsets.areStatusBarsVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.navigationBars(),
            insetIgnoringVisibility = WindowInsets.navigationBarsIgnoringVisibility,
            insetVisible = WindowInsets.navigationBars,
            isVisible = WindowInsets.areNavigationBarsVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.ime(),
            insetIgnoringVisibility = WindowInsets.ime,
            insetVisible = WindowInsets.ime,
            isVisible = WindowInsets.isImeVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.displayCutout(),
            insetIgnoringVisibility = WindowInsets.displayCutout,
            insetVisible = WindowInsets.displayCutout,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.captionBar(),
            insetIgnoringVisibility = WindowInsets.captionBarIgnoringVisibility,
            insetVisible = WindowInsets.captionBar,
            isVisible = WindowInsets.isCaptionBarVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.mandatorySystemGestures(),
            insetIgnoringVisibility = WindowInsets.mandatorySystemGestures,
            insetVisible = WindowInsets.mandatorySystemGestures,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.systemGestures(),
            insetIgnoringVisibility = WindowInsets.systemGestures,
            insetVisible = WindowInsets.systemGestures,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.tappableElement(),
            insetIgnoringVisibility = WindowInsets.tappableElementIgnoringVisibility,
            insetVisible = WindowInsets.tappableElement,
            isVisible = WindowInsets.isTappableElementVisible
        )
    )
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val apiLevel = Build.VERSION.SDK_INT
    val size = currentWindowSize()
    val orientation = LocalConfiguration.current.orientation
    val density = LocalDensity.current.density
    return RecordedInsets(
        manufacturer = manufacturer,
        model = model,
        apiLevel = apiLevel,
        windowWidth = size.width,
        windowHeight = size.height,
        density = density,
        orientation = orientation,
        insetList = insetList
    )
}
