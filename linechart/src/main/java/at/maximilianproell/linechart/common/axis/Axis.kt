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

internal fun DrawScope.drawYAxisWithLabels(
    axisConfig: AxisConfig,
    minValue: Float = 0f,
    maxValue: Float,
    drawingHeight: Float = size.height,
    drawingWidth: Float = size.width,
    xLabelsOffset: Float,
    numberOfLabels: Int = 3,
    helperLinesStrokeWidth: Dp = 1.dp,
    textColor: Color = Color.Black,
    typeface: Typeface,
    textSize: Float = 16f
) {
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

        // TODO: maybe add option so this can be set externally.
        if (index != 0) { // Don't draw helper line on the top of the chart.
            // Draw helper lines for better visual perception of the data.
            drawLine(
                start = Offset(x = 0f, y = yAxisEndPoint),
                end = Offset(x = drawingWidth, y = yAxisEndPoint),
                color = axisConfig.axisColor,
                pathEffect = null,
                alpha = 0.1F,
                strokeWidth = helperLinesStrokeWidth.toPx()
            )
        }
    }
}

internal fun DrawScope.drawXLabel(
    data: Any,
    xValue: Float,
    yOffset: Float,
    textColor: Color,
    typeface: Typeface,
    textSize: Float = 16f
) {
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(
                data.toString(),
                xValue,
                size.height + yOffset,
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