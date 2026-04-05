package de.drick.compose.edgetoedgepreviewlib.record

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addSvg
import androidx.compose.ui.graphics.toSvg
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class RecordedInsets(
    val manufacturer: String,
    val model: String,
    val apiLevel: Int,
    val windowWidth: Int, // screen width in pixel
    val windowHeight: Int,// screen height in pixel
    val density: Float,   // screen density
    val orientation: Int,
    val navigationMode: String,
    val insetList: List<InsetEntry>,
    @Serializable(ComposePathSerializer::class)
    val displayCutoutPath: Path?,
    @Serializable(ComposePathSerializer::class)
    val displayPath: Path?,
    @Serializable(with = WindowInsetsSerializer::class)
    val waterfallInsets: WindowInsets?,
    val corners: RoundedCornerJson?
)

enum class InsetsType {
    STATUS_BARS, NAVIGATION_BARS, CAPTION_BAR, IME, SYSTEM_GESTURES, MANDATORY_SYSTEM_GESTURES,
    TAPPABLE_ELEMENT, DISPLAY_CUTOUT, WATERFALL
}

@Serializable
data class InsetEntry(
    val type: InsetsType,
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

@Serializable
data class RoundedCornerJson(
    val topLeft: CornerJson?,
    val topRight: CornerJson?,
    val bottomLeft: CornerJson?,
    val bottomRight: CornerJson?
)

@Serializable
data class CornerJson(
    val radius: Int,
    val centerX: Int,
    val centerY: Int
)

fun CornerJson?.toCornerRadius() = this?.let {
    CornerRadius(it.radius.toFloat())
} ?: CornerRadius.Zero


fun WindowInsets.toJson(): WindowInsetsJson {
    val density = Density(1f)
    val ld = LayoutDirection.Ltr
    return WindowInsetsJson(
        left = getLeft(density, ld),
        top = getTop(density),
        right = getRight(density, ld),
        bottom = getBottom(density)
    )
}

object WindowInsetsSerializer : KSerializer<WindowInsets> {

    override val descriptor: SerialDescriptor = WindowInsetsJson.serializer().descriptor

    override fun serialize(encoder: Encoder, value: WindowInsets) {
        val json = value.toJson()
        encoder.encodeSerializableValue(WindowInsetsJson.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): WindowInsets {
        val jsonInsets = decoder.decodeSerializableValue(WindowInsetsJson.serializer())
        return WindowInsets(jsonInsets.left, jsonInsets.top, jsonInsets.right, jsonInsets.bottom)
    }
}


object ComposePathSerializer : KSerializer<Path> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ComposePath", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.toSvg(asDocument = false))
    }

    override fun deserialize(decoder: Decoder): Path {
        val svgData = decoder.decodeString()
        return Path().apply {
            addSvg(svgData)
        }
    }
}