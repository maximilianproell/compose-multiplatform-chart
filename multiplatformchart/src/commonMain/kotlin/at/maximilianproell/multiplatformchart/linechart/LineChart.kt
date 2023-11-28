package at.maximilianproell.multiplatformchart.linechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import at.maximilianproell.multiplatformchart.common.AxisConfigDefaults
import at.maximilianproell.multiplatformchart.common.LabeledXAxisConfig
import at.maximilianproell.multiplatformchart.common.LabeledYAxisConfig
import at.maximilianproell.multiplatformchart.common.drawXAxisWithLabels
import at.maximilianproell.multiplatformchart.common.drawYAxisWithLabels
import at.maximilianproell.multiplatformchart.linechart.config.LineConfig
import at.maximilianproell.multiplatformchart.linechart.config.LineConfigDefaults
import at.maximilianproell.multiplatformchart.linechart.model.DataPoint
import at.maximilianproell.multiplatformchart.linechart.model.LegendEntry
import at.maximilianproell.multiplatformchart.linechart.model.LineDataSet
import at.maximilianproell.multiplatformchart.linechart.model.downSample
import at.maximilianproell.multiplatformchart.linechart.model.filterInVisibleRange
import at.maximilianproell.multiplatformchart.linechart.model.toLegendEntry
import at.maximilianproell.multiplatformchart.linechart.utils.dataToOffSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    lineDataSets: () -> List<LineDataSet>,
    minVisibleYValue: Float = 0f,
    maxVisibleYValue: Float,
    strokeWidth: Dp = 1.dp,
    circleRadius: Dp = 4.dp,
    xAxisConfig: LabeledXAxisConfig = AxisConfigDefaults.xAxisConfigDefaults(),
    yAxisConfig: LabeledYAxisConfig = AxisConfigDefaults.yAxisConfigDefaults(),
    lineConfig: LineConfig = LineConfigDefaults.lineConfigDefaults()
) {
    val textMeasurer = rememberTextMeasurer()


    // We assume that the list of lineData points is sorted. Therefore, it is sufficient to get
    // the first and last value.
    var minXLineData by remember {
        mutableStateOf(
            lineDataSets().minOfOrNull { it.dataPoints.firstOrNull()?.xValue ?: 0f } ?: 0f
        )
    }
    var maxXLineData by remember {
        mutableStateOf(
            lineDataSets().maxOfOrNull { it.dataPoints.lastOrNull()?.xValue ?: 0f } ?: 0f
        )
    }

    var wholeCanvasSize by remember {
        mutableStateOf(Size(1f, 1f))
    }

    var lineDrawData: LineChartDrawingData by remember {
        mutableStateOf(LineChartDrawingData())
    }

    var pointsInVisibleRange: List<List<DataPoint>> by remember {
        mutableStateOf(emptyList())
    }

    val minMaxFlow = snapshotFlow { minXLineData }.combine(snapshotFlow { maxXLineData }, ::Pair)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            launch {
                minMaxFlow.collect { (minX, maxX) ->
                    pointsInVisibleRange = lineDataSets().map { lineDataSet ->
                        lineDataSet
                            .dataPoints
                            .filterInVisibleRange(minX, maxX)
                            .downSample(100)
                    }
                }
            }

            snapshotFlow { pointsInVisibleRange }.combine(minMaxFlow, ::Pair).collect { (pointsList, minMaxX) ->
                val scaleFactor = wholeCanvasSize.height.div(maxVisibleYValue - minVisibleYValue)

                data class LineDrawData(
                    val path: Path,
                    val offset: List<Offset>
                )

                val lineData = lineDataSets().mapIndexed { index, lineDataSet ->
                    // Create path for every lineDataSet.
                    val path = Path()

                    // Offsets for drawing circles
                    val offsets = mutableListOf<Offset>()

                    pointsList.getOrNull(index)?.forEachIndexed { pointIndex, point ->
                        val centerOffset = dataToOffSet(
                            dataPoint = point,
                            minXLineData = minMaxX.first,
                            maxXLineData = minMaxX.second,
                            size = wholeCanvasSize,
                            yScaleFactor = scaleFactor,
                            minYValue = minVisibleYValue,
                        )

                        offsets.add(centerOffset)

                        if (lineDataSet.dataPoints.size > 1) {
                            when (pointIndex) {
                                0 -> path.moveTo(centerOffset.x, centerOffset.y)
                                else -> path.lineTo(centerOffset.x, centerOffset.y)
                            }
                        }
                    }
                    LineDrawData(
                        path, offsets
                    )
                }
                lineDrawData = lineDrawData.copy(
                    paths = lineData.fastMap { it.path },
                    centerOffsets = lineData.fastMap { it.offset }
                )
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        val originalDifference = maxXLineData - minXLineData
                        val zoomedRangeLength = originalDifference * zoom
                        val difference = originalDifference - zoomedRangeLength

                        val centerWeighting = centroid.x / wholeCanvasSize.width

                        maxXLineData += difference * (1 - centerWeighting)
                        minXLineData -= difference * centerWeighting

                        if (originalDifference != 0f) {
                            val moveDifference = pan.x * (originalDifference / wholeCanvasSize.width)
                            maxXLineData -= moveDifference
                            minXLineData -= moveDifference
                        }
                    }
                }
                .onGloballyPositioned {
                    wholeCanvasSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
                }
        ) {
            val circleRadiusPx = if (lineConfig.showLineDots) circleRadius.toPx() else 0f
            val yAxisLabelsOffsetPx = yAxisConfig.labelsXOffset.toPx()
            val yPaddingOffset = if (yAxisLabelsOffsetPx < 0f) yAxisLabelsOffsetPx * -1 else 0f


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
                lineDataSets().forEachIndexed { index, lineDataSet ->
                    val brush = SolidColor(lineDataSet.lineColor)

                    if (lineDataSet.dataPoints.size > 1) {
                        val pathEffect =
                            if (lineConfig.hasSmoothCurve) PathEffect.cornerPathEffect(strokeWidth.toPx())
                            else null
                        lineDrawData.paths.getOrNull(index)?.let {
                            drawPath(
                                path = it,
                                brush = brush,
                                style = Stroke(
                                    width = strokeWidth.toPx(),
                                    pathEffect = pathEffect,
                                ),
                            )
                        }

                        // Draw circle on every point.
                        if (lineConfig.showLineDots) {
                            lineDrawData.centerOffsets.getOrNull(index)?.let {
                                drawPoints(
                                    points = it,
                                    pointMode = PointMode.Points,
                                    brush = brush,
                                    strokeWidth = circleRadiusPx * 2,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

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

                            // TODO: fix
                            /*val backgroundPath = path
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
                            )*/
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

/**
 * Stores the data needed to draw multiple line data sets.
 */
private data class LineChartDrawingData(
    val paths: List<Path> = emptyList(),
    val centerOffsets: List<List<Offset>> = emptyList()
)

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