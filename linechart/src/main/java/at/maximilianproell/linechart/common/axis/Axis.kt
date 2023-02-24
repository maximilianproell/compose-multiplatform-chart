package at.maximilianproell.linechart.common.axis

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.maximilianproell.linechart.common.calculations.xValueOffset

internal fun DrawScope.drawYAxisWithLabels(
    axisConfig: YAxisConfig,
    minValue: Float = 0f,
    maxValue: Float,
    drawingHeight: Float = size.height,
    drawingWidth: Float = size.width,
    xLabelsOffset: Float,
    helperLinesStrokeWidth: Dp = 1.dp,
    textColor: Color = Color.Black,
    typeface: Typeface,
    textSize: Float = 16f
) {
    val numberOfLabels = axisConfig.numberOfLabels
    val graphYAxisEndPoint = drawingHeight / (numberOfLabels - 1)
    val labelScaleFactor = (maxValue - minValue) / (numberOfLabels - 1)

    repeat(numberOfLabels) { index ->
        val isLabelTopmost = index == 0
        val yAxisEndPoint = if (isLabelTopmost) textSize else graphYAxisEndPoint * index

        drawIntoCanvas {
            it.nativeCanvas.apply {
                drawText(
                    axisConfig.labelsFormatter(
                        minValue + labelScaleFactor * (numberOfLabels - 1 - index)
                    ).toString(),
                    xLabelsOffset,
                    yAxisEndPoint - (if (isLabelTopmost) 0f else helperLinesStrokeWidth.toPx()),
                    Paint().apply {
                        color = textColor.toArgb()
                        setTypeface(typeface)
                        setTextSize(textSize)
                        textAlign = Paint.Align.LEFT
                    }
                )
            }
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
    labelColor: Color,
    labelTypeFace: Typeface
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
                clipOnBorder = xAxisConfig.borderTextClippingEnabled,
                textColor = labelColor,
                typeface = labelTypeFace,
                textSize = xAxisConfig.labelTextStyle.fontSize.toPx(),
                bottomOffset = if (xAxisLabelsYOffsetPx > 0) 0f else xAxisLabelsYOffsetPx
            )
        }
    }
}

internal fun DrawScope.drawXLabel(
    data: Any,
    textXPosition: Float,
    clipOnBorder: Boolean,
    bottomOffset: Float,
    textColor: Color,
    typeface: Typeface,
    textSize: Float = 16f
) {
    drawIntoCanvas {
        it.nativeCanvas.apply {
            val textPaint = Paint().apply {
                color = textColor.toArgb()
                setTypeface(typeface)
                setTextSize(textSize)
                textAlign = Paint.Align.CENTER
            }
            val dataText = data.toString()

            val clippedPosition = if (!clipOnBorder) {
                val textWidth = textPaint.measureText(dataText)
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
                data.toString(),
                clippedPosition ?: textXPosition,
                size.height + bottomOffset,
                Paint().apply {
                    color = textColor.toArgb()
                    setTypeface(typeface)
                    setTextSize(textSize)
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}