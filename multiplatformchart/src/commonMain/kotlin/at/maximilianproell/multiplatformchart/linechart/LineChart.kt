package at.maximilianproell.multiplatformchart.linechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.maximilianproell.multiplatformchart.linechart.common.axis.AxisConfigDefaults
import at.maximilianproell.multiplatformchart.linechart.common.axis.XAxisConfig
import at.maximilianproell.multiplatformchart.linechart.common.axis.YAxisConfig
import at.maximilianproell.multiplatformchart.linechart.common.axis.drawXAxisWithLabels
import at.maximilianproell.multiplatformchart.linechart.common.axis.drawYAxisWithLabels
import at.maximilianproell.multiplatformchart.linechart.common.calculations.dataToOffSet
import at.maximilianproell.multiplatformchart.linechart.config.LineConfig
import at.maximilianproell.multiplatformchart.linechart.config.LineConfigDefaults
import at.maximilianproell.multiplatformchart.linechart.model.LegendEntry
import at.maximilianproell.multiplatformchart.linechart.model.LineDataSet
import at.maximilianproell.multiplatformchart.linechart.model.toLegendEntry

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    lineDataSets: () -> List<LineDataSet>,
    minVisibleYValue: Float = 0f,
    maxVisibleYValue: Float,
    strokeWidth: Dp = 1.dp,
    circleRadius: Dp = 4.dp,
    xAxisConfig: XAxisConfig = AxisConfigDefaults.xAxisConfigDefaults(),
    yAxisConfig: YAxisConfig = AxisConfigDefaults.yAxisConfigDefaults(),
    lineConfig: LineConfig = LineConfigDefaults.lineConfigDefaults()
) {
    val textMeasurer = rememberTextMeasurer()

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

                if (yAxisConfig.numberOfLabels > 1) {
                    drawYAxisWithLabels(
                        yAxisConfig,
                        minValue = minVisibleYValue,
                        maxValue = maxVisibleYValue,
                        drawingHeight = size.height,
                        textMeasurer = textMeasurer,
                    )
                }
            }

            // Draw x labels
            inset(
                left = yPaddingOffset,
                right = 0f,
                bottom = 0f,
                top = 0f
            ) {
                drawXAxisWithLabels(
                    minXLineData = minXLineData,
                    maxXLineData = maxXLineData,
                    xAxisConfig = xAxisConfig,
                    textMeasurer = textMeasurer,
                )
            }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChartLegend(
    modifier: Modifier = Modifier,
    legendEntries: List<LegendEntry>
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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