package at.maximilianproell.multiplatformchart.linechart.common.calculations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import at.maximilianproell.multiplatformchart.linechart.model.DataPoint

internal fun dataToOffSet(
    dataPoint: DataPoint,
    minXLineData: Float,
    maxXLineData: Float,
    size: Size,
    yScaleFactor: Float,
    minYValue: Float,
): Offset {
    val xOnCanvas = xValueOffset(
        xValue = dataPoint.xValue,
        minXLineData = minXLineData,
        maxXLineData = maxXLineData,
        size = size
    )

    val y = size.height - (dataPoint.yValue - minYValue) * yScaleFactor
    return Offset(xOnCanvas, y)
}

internal fun xValueOffset(
    xValue: Float,
    minXLineData: Float,
    maxXLineData: Float,
    size: Size,
): Float {
    val length = if (maxXLineData == minXLineData) 1f else maxXLineData - minXLineData
    val normalizedX = (xValue - minXLineData) / length
    return normalizedX * size.width
}