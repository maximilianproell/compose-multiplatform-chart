package at.maximilianproell.multiplatformchart.linechart.model

/**
 * Represents a data point in the line chart.
 */
data class DataPoint(val xValue: Float, val yValue: Float)

// todo delete maybe
fun List<DataPoint>.maxYValue() = maxOfOrNull {
    it.yValue
} ?: 0f