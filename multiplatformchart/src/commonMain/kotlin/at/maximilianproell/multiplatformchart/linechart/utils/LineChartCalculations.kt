package at.maximilianproell.multiplatformchart.linechart.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import at.maximilianproell.multiplatformchart.common.xValueOffset
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
        minXData = minXLineData,
        maxXData = maxXLineData,
        size = size
    )

    val y = size.height - (dataPoint.yValue - minYValue) * yScaleFactor
    return Offset(xOnCanvas, y)
}

