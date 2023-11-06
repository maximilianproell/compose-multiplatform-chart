package at.maximilianproell.multiplatformchart.linechart.common.dimens

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ChartDimens(
    val padding: Dp
)

// todo check if this can be deleted

internal object ChartDimensDefaults {

    fun chartDimesDefaults() = ChartDimens(
        padding = 4.dp
    )
    fun horizontalChartDimesDefaults() = ChartDimens(
        padding = 4.dp
    )
}