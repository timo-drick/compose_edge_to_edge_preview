package de.drick.compose.edgetoedgepreviewlib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(name = "Ime keyboard", widthDp = 400)
@Composable
private fun PreviewImeKeyboard() {
    ImeKeyboard(
        size = 250.dp,
        isDarkMode = true
    )
}

@Preview(name = "Ime keyboard light", widthDp = 400)
@Composable
private fun PreviewImeKeyboardLight() {
    ImeKeyboard(
        size = 250.dp,
        isDarkMode = false
    )
}

private val keyRow1 = "qwertyuiop".map { it.toString() }
private val keyRow1Hints = "1234567890".map { it.toString() }
private val keyRow2 = "asdfghjkl".map { it.toString() }
private val keyRow3 = "zxcvbnm".map { it.toString() }

/**
 * Placeholder that simulates an open on-screen keyboard (IME).
 */
@Composable
fun ImeKeyboard(
    size: Dp,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = true,
) {
    val backgroundColor = if (isDarkMode) Color(0xFF22262A) else Color(0xFFECEEF1)
    val keyColor = if (isDarkMode) Color(0xFF3C4046) else Color(0xFFFBFCFE)
    val functionKeyColor = if (isDarkMode) Color(0xFF51555B) else Color(0xFFD4D8DE)
    val contentColor = if (isDarkMode) Color(0xFFE8EAED) else Color(0xFF1F2328)
    val colors = ImeKeyboardColors(
        key = keyColor,
        functionKey = functionKeyColor,
        content = contentColor
    )
    Column(
        modifier = modifier
            .height(size)
            .fillMaxWidth()
            .background(backgroundColor)
            // like a real IME keep the keys out of the navigation bar area
            // while the keyboard background is drawn behind the navigation bar
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KeyRow {
            keyRow1.forEachIndexed { index, letter ->
                Key(letter, colors, hint = keyRow1Hints[index])
            }
        }
        KeyRow {
            Spacer(Modifier.weight(0.5f))
            keyRow2.forEach { letter ->
                Key(letter, colors)
            }
            Spacer(Modifier.weight(0.5f))
        }
        KeyRow {
            Key("", colors, weight = 1.5f, isFunctionKey = true) // shift
            keyRow3.forEach { letter ->
                Key(letter, colors)
            }
            Key("", colors, weight = 1.5f, isFunctionKey = true) // backspace
        }
        KeyRow {
            Key("?123", colors, weight = 1.5f, isFunctionKey = true)
            Key(",", colors)
            Key("", colors) // emoji
            Key("", colors, weight = 4f) // space
            Key(".", colors)
            Key("", colors, weight = 1.5f, isFunctionKey = true) // search
        }
    }
}

private class ImeKeyboardColors(
    val key: Color,
    val functionKey: Color,
    val content: Color
)

@Composable
private fun ColumnScope.KeyRow(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        content = content
    )
}

@Composable
private fun RowScope.Key(
    label: String,
    colors: ImeKeyboardColors,
    weight: Float = 1f,
    hint: String? = null,
    isFunctionKey: Boolean = false
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .background(
                color = if (isFunctionKey) colors.functionKey else colors.key,
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        BasicText(
            text = label,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(fontSize = 18.sp),
            color = { colors.content }
        )
        if (hint != null) {
            BasicText(
                text = hint,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 2.dp, end = 5.dp),
                style = TextStyle(fontSize = 10.sp),
                color = { colors.content }
            )
        }
    }
}
