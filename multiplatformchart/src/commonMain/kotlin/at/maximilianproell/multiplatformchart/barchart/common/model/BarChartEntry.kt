package at.maximilianproell.multiplatformchart.barchart.common.model

/**
 * Represents an entry (i.e., a bar) in a bar chart.
 * @param xValue Basically the label of this bar chart entry, displayed on the x-axis.
 * @param yValue The height of this bar chart entry.
 */
data class BarChartEntry(
    val xValue: String,
    val yValue: Float,
)