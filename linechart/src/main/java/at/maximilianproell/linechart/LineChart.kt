package at.maximilianproell.linechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.maximilianproell.linechart.common.axis.AxisConfigDefaults
import at.maximilianproell.linechart.common.axis.XAxisConfig
import at.maximilianproell.linechart.common.axis.YAxisConfig
import at.maximilianproell.linechart.common.axis.drawXAxisWithLabels
import at.maximilianproell.linechart.common.axis.drawYAxisWithLabels
import at.maximilianproell.linechart.common.calculations.dataToOffSet
import at.maximilianproell.linechart.common.utils.getTypefaceOfTextStyle
import at.maximilianproell.linechart.config.LineConfig
import at.maximilianproell.linechart.config.LineConfigDefaults
import at.maximilianproell.linechart.model.DataPoint
import at.maximilianproell.linechart.model.LegendEntry
import at.maximilianproell.linechart.model.LineDataSet
import at.maximilianproell.linechart.model.toLegendEntry
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    lineDataSets: () -> List<LineDataSet>,
    minVisibleYValue: Float = 0f,
    maxVisibleYValue: Float,
    strokeWidth: Dp = 1.dp,
    circleRadius: Dp = 8.dp,
    xAxisConfig: XAxisConfig = AxisConfigDefaults.xAxisConfigDefaults(),
    yAxisConfig: YAxisConfig = AxisConfigDefaults.yAxisConfigDefaults(),
    lineConfig: LineConfig = LineConfigDefaults.lineConfigDefaults()
) {
    val fallbackTextColor = LocalContentColor.current
    val xLabelTypeFace = getTypefaceOfTextStyle(textStyle = xAxisConfig.labelTextStyle)
    val yLabelTypeFace = getTypefaceOfTextStyle(textStyle = yAxisConfig.labelTextStyle)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
        ) {
            val circleRadiusPx = if (lineConfig.showLineDots) circleRadius.toPx() else 0f
            val yAxisLabelsOffsetPx = yAxisConfig.labelsXOffset.toPx()
            val yPaddingOffset = if (yAxisLabelsOffsetPx < 0f) yAxisLabelsOffsetPx * -1 else 0f

            // We assume that the list of lineData points is sorted. Therefore, it is sufficient to get
            // the first and last value.
            val minXLineData = lineDataSets().minOfOrNull { it.dataPoints.firstOrNull()?.xValue ?: 0f } ?: 0f
            val maxXLineData = lineDataSets().maxOfOrNull { it.dataPoints.lastOrNull()?.xValue ?: 0f } ?: 0f

            val xAxisLabelsYOffsetPx = xAxisConfig.labelsYOffset.toPx()
            val bottomLabelPadding = if (xAxisLabelsYOffsetPx > 0) {
                // Positive label padding, therefore bottom padding necessary.
                xAxisLabelsYOffsetPx
            } else 0f
            inset(
                left = circleRadiusPx + yPaddingOffset,
                right = circleRadiusPx,
                top = circleRadiusPx,
                bottom = circleRadiusPx + bottomLabelPadding
            ) {
                val scaleFactor = size.height.div(maxVisibleYValue - minVisibleYValue)

                lineDataSets().forEach { lineDataSet ->
                    val path = Path()
                    val brush = SolidColor(lineDataSet.lineColor)

                    lineDataSet.dataPoints.forEachIndexed { index, data ->
                        val centerOffset = dataToOffSet(
                            dataPoint = data,
                            minXLineData = minXLineData,
                            maxXLineData = maxXLineData,
                            size = size,
                            yScaleFactor = scaleFactor,
                            minYValue = minVisibleYValue,
                        )

                        if (lineDataSet.dataPoints.size > 1) {
                            when (index) {
                                0 -> path.moveTo(centerOffset.x, centerOffset.y)
                                else -> path.lineTo(centerOffset.x, centerOffset.y)
                            }
                        }

                        // Draw circle on every point.
                        if (lineConfig.showLineDots) {
                            drawCircle(
                                center = centerOffset,
                                radius = circleRadiusPx,
                                brush = brush
                            )
                        }
                    }
                    if (lineDataSet.dataPoints.size > 1) {
                        val pathEffect =
                            if (lineConfig.hasSmoothCurve) PathEffect.cornerPathEffect(strokeWidth.toPx())
                            else null
                        drawPath(
                            path = path,
                            brush = brush,
                            style = Stroke(width = strokeWidth.toPx(), pathEffect = pathEffect),
                        )

                        if (lineConfig.fillAreaUnderLine) {
                            val lastCenterOffset = dataToOffSet(
                                dataPoint = lineDataSet.dataPoints.last(),
                                minXLineData = minXLineData,
                                maxXLineData = maxXLineData,
                                size = size,
                                yScaleFactor = scaleFactor,
                                minYValue = minVisibleYValue,
                            )
                            val firstCenterOffset = dataToOffSet(
                                dataPoint = lineDataSet.dataPoints.first(),
                                minXLineData = minXLineData,
                                maxXLineData = maxXLineData,
                                size = size,
                                yScaleFactor = scaleFactor,
                                minYValue = minVisibleYValue,
                            )

                            val backgroundPath = path
                                .apply {
                                    lineTo(lastCenterOffset.x, size.height)
                                    lineTo(firstCenterOffset.x, size.height)
                                    close()
                                }
                            drawPath(
                                path = backgroundPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(lineDataSet.lineColor, Color.Transparent),
                                    endY = size.height
                                ),
                            )
                        }
                    }
                }

                if (yAxisConfig.showLines) {
                    drawYAxisWithLabels(
                        yAxisConfig,
                        minValue = minVisibleYValue,
                        maxValue = maxVisibleYValue,
                        drawingHeight = size.height - yAxisConfig.labelsXOffset.toPx(),
                        textColor = yAxisConfig.labelTextStyle.color.takeOrElse { fallbackTextColor },
                        typeface = yLabelTypeFace,
                        textSize = yAxisConfig.labelTextStyle.fontSize.toPx(),
                        xLabelsOffset = yAxisConfig.labelsXOffset.toPx()
                    )
                }
            }

            val labelColor = xAxisConfig.labelTextStyle.color.takeOrElse { fallbackTextColor }

            // Draw x labels
            drawXAxisWithLabels(
                minXLineData = minXLineData,
                maxXLineData = maxXLineData,
                xAxisConfig = xAxisConfig,
                labelColor = labelColor,
                labelTypeFace = xLabelTypeFace
            )
        }

        val legendEntries = remember {
            derivedStateOf { lineDataSets().map { it.toLegendEntry() } }
        }

        if (legendEntries.value.isNotEmpty()) {
            ChartLegend(
                modifier = Modifier.padding(horizontal = yAxisConfig.labelsXOffset.coerceAtLeast(0.dp)),
                legendEntries = legendEntries.value
            )
        }
    }
}

@Composable
private fun ChartLegend(
    modifier: Modifier = Modifier,
    legendEntries: List<LegendEntry>
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        mainAxisSpacing = 12.dp,
    ) {
        legendEntries.forEach { label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(color = label.legendColor)
                )
                Text(
                    text = label.legendName,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
@Preview
fun LineChartPreview() {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    lineDataSets = {
                        listOf(
                            LineDataSet(
                                name = "Red Line",
                                dataPoints = listOf(
                                    DataPoint(10F, 20F),
                                    DataPoint(20F, 15F),
                                    DataPoint(30F, 5F),
                                    DataPoint(60F, 13F),
                                    DataPoint(80F, 0F),
                                    DataPoint(100F, 10F),
                                ),
                                lineColor = Color.Red
                            ),
                            LineDataSet(
                                name = "Magenta Line",
                                dataPoints = listOf(
                                    DataPoint(100F, 10F),
                                    DataPoint(120F, 20F),
                                    DataPoint(130F, 5F),
                                    DataPoint(160F, 7.5F),
                                    DataPoint(200F, 30F),
                                ),
                                lineColor = Color.Magenta,
                            ),
                        )
                    },
                    maxVisibleYValue = 30f,
                    xAxisConfig = AxisConfigDefaults.xAxisConfigDefaults().copy(
                        labelsYOffset = 8.dp,
                        axisColor = Color.Black,
                        allowBorderTextClipping = true
                    ),
                    yAxisConfig = AxisConfigDefaults.yAxisConfigDefaults().copy(
                        numberOfLabels = 6,
                        labelsXOffset = 8.dp
                    ),
                    lineConfig = LineConfigDefaults.lineConfigDefaults().copy(showLineDots = false)
                )
            }
        }
    }
}