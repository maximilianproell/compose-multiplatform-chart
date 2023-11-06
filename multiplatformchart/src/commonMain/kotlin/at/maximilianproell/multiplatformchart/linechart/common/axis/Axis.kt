package at.maximilianproell.multiplatformchart.linechart.common.axis

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.maximilianproell.multiplatformchart.linechart.common.calculations.xValueOffset

internal fun DrawScope.drawYAxisWithLabels(
    axisConfig: YAxisConfig,
    minValue: Float = 0f,
    maxValue: Float,
    drawingHeight: Float = size.height,
    drawingWidth: Float = size.width,
    xLabelsOffset: Float,
    helperLinesStrokeWidth: Dp = 1.dp,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
) {
    val numberOfLabels = axisConfig.numberOfLabels
    val graphYAxisEndPoint = drawingHeight / (numberOfLabels - 1)
    val labelScaleFactor = (maxValue - minValue) / (numberOfLabels - 1)
    val textSize = textStyle.fontSize.toPx()

    repeat(numberOfLabels) { index ->
        val isLabelTopmost = index == 0
        val yAxisEndPoint = if (isLabelTopmost) textSize else graphYAxisEndPoint * index

        drawIntoCanvas {
            drawText(
                textMeasurer = textMeasurer,
                text = axisConfig.labelsFormatter(
                    minValue + labelScaleFactor * (numberOfLabels - 1 - index)
                ).toString(),
                topLeft = Offset(
                    x = xLabelsOffset,
                    y = yAxisEndPoint - (if (isLabelTopmost) 0f else helperLinesStrokeWidth.toPx())
                ),
                style = textStyle,
            )
        }

        if (axisConfig.showLines && index != 0) { // Don't draw helper line on the top of the chart.
            // Draw helper lines for better visual perception of the data.
            drawLine(
                start = Offset(x = 0f, y = yAxisEndPoint),
                end = Offset(x = drawingWidth, y = yAxisEndPoint),
                color = axisConfig.axisColor,
                pathEffect = null,
                alpha = 0.4F,
                strokeWidth = helperLinesStrokeWidth.toPx()
            )
        }
    }
}

internal fun DrawScope.drawXAxisWithLabels(
    minXLineData: Float,
    maxXLineData: Float,
    xAxisConfig: XAxisConfig,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
) {
    val xAxisLabelsYOffsetPx = xAxisConfig.labelsYOffset.toPx()
    val numberOfXLabels = xAxisConfig.numberOfLabels

    // Draw labels
    if (numberOfXLabels > 1) {
        val jumpSize = (maxXLineData - minXLineData) / (numberOfXLabels - 1)

        repeat(numberOfXLabels) { index ->
            val xValue = minXLineData + jumpSize * index
            val xOffset = xValueOffset(
                xValue = xValue,
                minXLineData = minXLineData,
                maxXLineData = maxXLineData,
                size = size,
            )

            drawXLabel(
                data = xAxisConfig.labelsFormatter(xValue),
                textXPosition = xOffset,
                clipOnBorder = xAxisConfig.allowBorderTextClipping,
                bottomOffset = if (xAxisLabelsYOffsetPx > 0) 0f else xAxisLabelsYOffsetPx,
                textStyle = textStyle,
                textMeasurer = textMeasurer,
            )
        }
    }
}

internal fun DrawScope.drawXLabel(
    data: Any,
    textXPosition: Float,
    clipOnBorder: Boolean,
    bottomOffset: Float,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
) {
    drawIntoCanvas {
        val dataText = data.toString()
        val clippedPosition = if (!clipOnBorder) {
            val textWidth = textMeasurer.measure(text = dataText, style = textStyle).size.width
            val textHalved = textWidth / 2

            if (textXPosition - textHalved < 0) {
                // Text overshoots canvas on the left side.
                textXPosition + textHalved
            } else if (textXPosition + textHalved > size.width) {
                // Text overshoots canvas on the right side.
                textXPosition - textHalved
            } else {
                textXPosition
            }
        } else null

        drawText(
            textMeasurer = textMeasurer,
            text = data.toString(),
            topLeft = Offset(
                x = clippedPosition ?: textXPosition,
                y = size.height + bottomOffset
            ),
            style = textStyle,
        )
    }
}