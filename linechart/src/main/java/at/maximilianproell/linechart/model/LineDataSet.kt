package at.maximilianproell.linechart.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a dataset containing the line data to draw.
 * @param name The name of this dataset.
 * @param dataPoints A list containing all the data points to draw.
 * @param lineColor The color of this line.
 */
data class LineDataSet(
    val name: String,
    val dataPoints: List<DataPoint>,
    val lineColor: Color,
)

internal fun LineDataSet.toLegendEntry() = LegendEntry(
    legendName = name,
    legendColor = lineColor
)

/**
 * Represents a legend entry on the chart.
 * @param legendColor The color of the legend. Should ideally match the color of the line.
 * @param legendName The name of the legend.
 */
internal data class LegendEntry(
    val legendColor: Color,
    val legendName: String
)