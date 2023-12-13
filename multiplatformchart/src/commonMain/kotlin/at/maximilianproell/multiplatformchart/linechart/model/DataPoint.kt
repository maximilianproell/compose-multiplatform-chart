package at.maximilianproell.multiplatformchart.linechart.model

/**
 * Represents a data point in the line chart.
 */
data class DataPoint(val xValue: Float, val yValue: Float)

/**
 * Returns a subset of this [List] of [DataPoint]s given the range parameters.
 */
fun List<DataPoint>.filterInVisibleRange(minXData: Float, maxXData: Float): List<DataPoint> {
    return filter { it.xValue in minXData..maxXData }
}