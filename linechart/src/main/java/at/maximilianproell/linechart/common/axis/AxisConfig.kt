package at.maximilianproell.linechart.common.axis

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AxisConfig(
    val showAxis: Boolean,
    val showLabels: Boolean,
    val labelsFormatter: (Float) -> Any,
    val axisColor: Color,
    /**
     * The offset of the labels on the axis.
     */
    val labelsOffset: Dp,
    val labelTextStyle: TextStyle,
)

object AxisConfigDefaults {
    @Composable
    fun axisConfigDefaults() = AxisConfig(
        axisColor = Color.LightGray,
        showAxis = true,
        showLabels = true,
        labelsFormatter = { it },
        labelsOffset = 0.dp,
        labelTextStyle = MaterialTheme.typography.labelSmall,
    )
}