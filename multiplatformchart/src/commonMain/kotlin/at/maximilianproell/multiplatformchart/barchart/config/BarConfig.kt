package at.maximilianproell.multiplatformchart.barchart.config

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class BarConfig(
    val barColor: Color,
    val barWidth: Dp = 12.dp,
    val animate: Boolean = true,
)

object BarConfigDefaults {
    val defaultBarConfig: BarConfig
        @Composable get() = BarConfig(barColor = MaterialTheme.colorScheme.onBackground)
}