package at.maximilianproell.linechart.common.axis

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface AxisConfig {
    /**
     * The number of labels to draw. If set to 0, no labels are drawn.
     */
    val numberOfLabels: Int

    /**
     * The labels formatter.
     */
    val labelsFormatter: (Float) -> Any

    /**
     * The line color of this axis.
     */
    val axisColor: Color

    /**
     * The text style of the labels on this axis.
     */
    val labelTextStyle: TextStyle
}

data class XAxisConfig(
    override val numberOfLabels: Int,
    override val labelsFormatter: (Float) -> Any,
    override val axisColor: Color,
    override val labelTextStyle: TextStyle,

    /**
     * Whether the labels on the start/end of the chart should be clipped.
     */
    val borderTextClippingEnabled: Boolean,

    /**
     * The y offset of the labels on this X axis. A negative offset will move the labels up "inside" the chart, whereas
     * a positive offset will move the labels down, "outside" of the chart.
     */
    val labelsYOffset: Dp

) : AxisConfig

data class YAxisConfig(
    /**
     * Whether a horizontal line across the chart should be drawn for every y label.
     */
    val showLines: Boolean,
    override val numberOfLabels: Int,
    override val labelsFormatter: (Float) -> Any,
    override val axisColor: Color,
    override val labelTextStyle: TextStyle,

    // todo: make it similar to X axis implementation
    /**
     * The x offset of the labels on this Y axis.
     */
    val labelsXOffset: Dp
) : AxisConfig

object AxisConfigDefaults {
    @Composable
    fun xAxisConfigDefaults() = XAxisConfig(
        axisColor = Color.LightGray,
        numberOfLabels = 3,
        labelsFormatter = { it },
        borderTextClippingEnabled = false,
        labelsYOffset = 0.dp,
        labelTextStyle = MaterialTheme.typography.labelSmall,
    )

    @Composable
    fun yAxisConfigDefaults() = YAxisConfig(
        axisColor = Color.LightGray,
        showLines = true,
        numberOfLabels = 3,
        labelsFormatter = { it },
        labelsXOffset = 0.dp,
        labelTextStyle = MaterialTheme.typography.labelSmall,
    )
}