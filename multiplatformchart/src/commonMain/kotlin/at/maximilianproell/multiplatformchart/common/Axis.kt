package at.maximilianproell.multiplatformchart.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun DrawScope.drawYAxisWithLabels(
    axisConfig: LabeledYAxisConfig,
    minValue: Float = 0f,
    maxValue: Float,
    drawingHeight: Float = size.height,
    drawingWidth: Float = size.width,
    helperLinesStrokeWidth: Dp = 1.dp,
    textMeasurer: TextMeasurer,
) {
    val numberOfLabels = axisConfig.numberOfLabels
    val graphYAxisEndPoint = drawingHeight / (numberOfLabels - 1)
    val labelScaleFactor = (maxValue - minValue) / (numberOfLabels - 1)
    val textSize = axisConfig.labelTextStyle.fontSize.toPx()

    repeat(numberOfLabels) { index ->
        val isLabelTopmost = index == 0
        val yAxisEndPoint = if (isLabelTopmost) textSize else graphYAxisEndPoint * index

        drawText(
            textMeasurer = textMeasurer,
            text = axisConfig.labelsFormatter(
                minValue + labelScaleFactor * (numberOfLabels - 1 - index)
            ).toString(),
            topLeft = Offset(
                x = axisConfig.labelsXOffset.toPx(),
                y = yAxisEndPoint - (if (isLabelTopmost) 0f else helperLinesStrokeWidth.toPx())
            ),
            style = axisConfig.labelTextStyle,
        )

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
    xAxisConfig: LabeledXAxisConfig,
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
                minXData = minXLineData,
                maxXData = maxXLineData,
                size = size,
            )

            drawXLabel(
                data = xAxisConfig.labelsFormatter(xValue),
                centerTextXPosition = xOffset,
                clipOnBorder = xAxisConfig.allowBorderTextClipping,
                bottomOffset = if (xAxisLabelsYOffsetPx > 0) 0f else xAxisLabelsYOffsetPx,
                textStyle = xAxisConfig.labelTextStyle,
                textMeasurer = textMeasurer,
            )
        }
    }
}

