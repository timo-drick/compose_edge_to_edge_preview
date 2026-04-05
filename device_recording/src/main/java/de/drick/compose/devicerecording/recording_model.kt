package de.drick.compose.devicerecording

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addSvg
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.toSvg
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.RoundedCornerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.drick.compose.edgetoedgetestlib.navigationMode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class RecordedInsets(
    val screenshotFileName: String,
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

@Serializable
data class InsetEntry(
    @field:WindowInsetsCompat.Type.InsetsType
    val type: Int,
    val typeName: String,
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

fun RoundedCornerCompat.toJson() = CornerJson(
    radius = radius,
    centerX = center.x,
    centerY = center.y
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


fun InsetEntry.getTypeString(): String = getNameFromWindowInsetType(type)

fun getNameFromWindowInsetType(
    @WindowInsetsCompat.Type.InsetsType
    type: Int
): String {
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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun recordInsets(
    screenshotFileName: String
): RecordedInsets {
    val insetList = listOf(
        InsetEntry(
            type = WindowInsetsCompat.Type.statusBars(),
            typeName = "statusBars",
            insetIgnoringVisibility = WindowInsets.statusBarsIgnoringVisibility,
            insetVisible = WindowInsets.statusBars,
            isVisible = WindowInsets.areStatusBarsVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.navigationBars(),
            typeName = "navigationBars",
            insetIgnoringVisibility = WindowInsets.navigationBarsIgnoringVisibility,
            insetVisible = WindowInsets.navigationBars,
            isVisible = WindowInsets.areNavigationBarsVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.ime(),
            typeName = "ime",
            insetIgnoringVisibility = WindowInsets.ime,
            insetVisible = WindowInsets.ime,
            isVisible = WindowInsets.isImeVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.displayCutout(),
            typeName = "displayCutout",
            insetIgnoringVisibility = WindowInsets.displayCutout,
            insetVisible = WindowInsets.displayCutout,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.captionBar(),
            typeName = "captionBar",
            insetIgnoringVisibility = WindowInsets.captionBarIgnoringVisibility,
            insetVisible = WindowInsets.captionBar,
            isVisible = WindowInsets.isCaptionBarVisible
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.mandatorySystemGestures(),
            typeName = "mandatorySystemGestures",
            insetIgnoringVisibility = WindowInsets.mandatorySystemGestures,
            insetVisible = WindowInsets.mandatorySystemGestures,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.systemGestures(),
            typeName = "systemGestures",
            insetIgnoringVisibility = WindowInsets.systemGestures,
            insetVisible = WindowInsets.systemGestures,
            isVisible = true
        ),
        InsetEntry(
            type = WindowInsetsCompat.Type.tappableElement(),
            typeName = "tappableElement",
            insetIgnoringVisibility = WindowInsets.tappableElementIgnoringVisibility,
            insetVisible = WindowInsets.tappableElement,
            isVisible = WindowInsets.isTappableElementVisible
        )
    )
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    val apiLevel = Build.VERSION.SDK_INT
    val size = LocalWindowInfo.current.containerSize
    val orientation = LocalConfiguration.current.orientation
    val density = LocalDensity.current.density
    val navigationMode = LocalContext.current.navigationMode()
    val rootInsets = ViewCompat.getRootWindowInsets(LocalView.current)
    val waterfallInsets = rootInsets?.displayCutout?.waterfallInsets?.let {
        WindowInsets(
            left = it.left,
            top = it.top,
            right = it.right,
            bottom = it.bottom
        )
    }
    val cutoutPath = rootInsets?.displayCutout?.cutoutPath
    val displayPath = rootInsets?.displayShape?.path
    val corners  = if (rootInsets != null) {
        val topLeft = rootInsets.getRoundedCorner(RoundedCornerCompat.POSITION_TOP_LEFT)
        val topRight = rootInsets.getRoundedCorner(RoundedCornerCompat.POSITION_TOP_RIGHT)
        val bottomLeft = rootInsets.getRoundedCorner(RoundedCornerCompat.POSITION_BOTTOM_LEFT)
        val bottomRight = rootInsets.getRoundedCorner(RoundedCornerCompat.POSITION_BOTTOM_RIGHT)
        RoundedCornerJson(
            topLeft = topLeft?.toJson(),
            topRight = topRight?.toJson(),
            bottomLeft = bottomLeft?.toJson(),
            bottomRight = bottomRight?.toJson()
        )
    } else null

    return RecordedInsets(
        screenshotFileName = screenshotFileName,
        manufacturer = manufacturer,
        model = model,
        apiLevel = apiLevel,
        windowWidth = size.width,
        windowHeight = size.height,
        density = density,
        orientation = orientation,
        navigationMode = navigationMode,
        insetList = insetList,
        displayCutoutPath = cutoutPath?.asComposePath(),
        displayPath = displayPath?.asComposePath(),
        waterfallInsets = waterfallInsets,
        corners = corners
    )
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