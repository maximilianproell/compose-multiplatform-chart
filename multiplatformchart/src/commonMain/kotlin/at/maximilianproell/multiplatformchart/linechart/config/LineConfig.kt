package at.maximilianproell.multiplatformchart.linechart.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class LineConfig(
    val hasSmoothCurve: Boolean,
    val showLineDots: Boolean,
    val fillAreaUnderLine: Boolean,
    val strokeWidth: Dp,
)

object LineConfigDefaults {
    fun lineConfigDefaults() = LineConfig(
        hasSmoothCurve = true,
        showLineDots = true,
        fillAreaUnderLine = false,
        strokeWidth = 1.dp
    )
}