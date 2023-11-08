package at.maximilianproell.multiplatformchart.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText

/**
 * Draws a text label into the canvas.
 * @param data The data to draw. Will be converted to a String.
 * @param centerTextXPosition The center x position of the text label.
 * @param clipOnBorder If `true`, the text will be cut off at the edges of the canvas. Else, the text will always reside
 * within the canvas bounds.
 * @param textStyle The [TextStyle] to draw the text with.
 * @param textMeasurer The [TextMeasurer] used to measure the text.
 */
internal fun DrawScope.drawXLabel(
    data: Any,
    centerTextXPosition: Float,
    clipOnBorder: Boolean,
    bottomOffset: Float,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
) {
    val dataText = data.toString()
    val textWidth = textMeasurer.measure(text = dataText, style = textStyle).size.width
    val textHalved = textWidth / 2
    val leftXPos = centerTextXPosition - textHalved
    val clippedLeftXPosition = if (!clipOnBorder) {
        if (leftXPos < 0) {
            // Text overshoots canvas on the left side.
            0f
        } else if (leftXPos + textWidth > size.width) {
            // Text overshoots canvas on the right side.
            size.width - textWidth
        } else {
            leftXPos
        }
    } else null

    drawText(
        textMeasurer = textMeasurer,
        text = dataText,
        topLeft = Offset(
            x = clippedLeftXPosition ?: leftXPos,
            y = size.height + bottomOffset
        ),
        style = textStyle,
    )

}