package at.maximilianproell.multiplatformchart.common

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface LabeledAxisConfig {
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

data class LabeledXAxisConfig(
    override val numberOfLabels: Int,
    override val labelsFormatter: (Float) -> Any,
    override val axisColor: Color,
    override val labelTextStyle: TextStyle,

    /**
     * Whether the labels on the start/end of the chart should be clipped.
     */
    val allowBorderTextClipping: Boolean,

    /**
     * The y offset of the labels on this X axis. A negative offset will move the labels up "inside" the chart, whereas
     * a positive offset will move the labels down, "outside" of the chart.
     */
    val labelsYOffset: Dp

) : LabeledAxisConfig

data class LabeledYAxisConfig(
    /**
     * Whether a horizontal line across the chart should be drawn for every y label.
     */
    val showLines: Boolean,
    override val numberOfLabels: Int,
    override val labelsFormatter: (Float) -> Any,
    override val axisColor: Color,
    override val labelTextStyle: TextStyle,

    /**
     * The x offset of the labels on this Y axis.
     */
    val labelsXOffset: Dp
) : LabeledAxisConfig

object AxisConfigDefaults {
    @Composable
    fun xAxisConfigDefaults() = LabeledXAxisConfig(
        axisColor = Color.LightGray,
        numberOfLabels = 3,
        labelsFormatter = { it },
        allowBorderTextClipping = false,
        labelsYOffset = 0.dp,
        labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = LocalContentColor.current),
    )

    @Composable
    fun yAxisConfigDefaults() = LabeledYAxisConfig(
        axisColor = Color.LightGray,
        showLines = true,
        numberOfLabels = 3,
        labelsFormatter = { it },
        labelsXOffset = 0.dp,
        labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = LocalContentColor.current),
    )
}