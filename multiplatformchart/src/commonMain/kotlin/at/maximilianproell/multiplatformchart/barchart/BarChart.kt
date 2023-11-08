package at.maximilianproell.multiplatformchart.barchart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.maximilianproell.multiplatformchart.barchart.common.model.BarChartEntry
import at.maximilianproell.multiplatformchart.common.drawXLabel
import at.maximilianproell.multiplatformchart.linechart.common.axis.AxisConfigDefaults.yAxisConfigDefaults
import at.maximilianproell.multiplatformchart.linechart.common.axis.drawYAxisWithLabels
import kotlin.math.ceil

/**
 * A bar chart displaying the bars
 */
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    entries: List<BarChartEntry>,
    maxYValue: Float,
    barColor: Color = MaterialTheme.colorScheme.onBackground,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        color = LocalContentColor.current
    ), // TODO: create dedicated xAxisConfig,
    barWidthDp: Dp = 12.dp,
    animate: Boolean = true,
    axisColor: Color = MaterialTheme.colorScheme.onBackground,
    labelTextSize: TextUnit = 12.sp
) {
    val barPercentageHeights = if (animate) {
        val currentAnimationState = remember(entries) {
            MutableTransitionState(true).apply {
                targetState = false
            }
        }

        val barAnimationTransition = updateTransition(
            transitionState = currentAnimationState,
            label = "bar_chart_transition"
        )
        entries.map { dataPoint ->
            barAnimationTransition.animateFloat(
                transitionSpec = {
                    tween(
                        delayMillis = 500,
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing,
                    )
                }, label = dataPoint.xValue
            ) { progress ->
                if (progress) 0f
                else dataPoint.yValue / maxYValue
            }
        }
    } else entries.map { mutableStateOf(it.yValue / maxYValue) }

    val yAxisDefault = yAxisConfigDefaults()
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val xLabelsHeight = labelTextStyle.lineHeight.toPx() * 1.2f
        fun getBarSpacing(): Float {
            val sizeOccupiedByBars = barWidthDp.toPx() * entries.size
            return (size.width - sizeOccupiedByBars) / (entries.size - 1)
        }

        fun getBarOffsetAtIndex(index: Int) = (getBarSpacing() + barWidthDp.toPx()) * index

        inset(
            bottom = xLabelsHeight,
            top = 0f,
            left = 0f,
            right = 0f,
        ) {
            val (_, canvasHeight) = size

            // Draw bars
            repeat(entries.size) { index ->
                val barHeight = barPercentageHeights[index].value * canvasHeight
                val currentBarOffset = getBarOffsetAtIndex(index)
                drawRect(
                    color = barColor,
                    size = Size(width = barWidthDp.toPx(), height = barHeight),
                    topLeft = Offset(currentBarOffset, canvasHeight - barHeight)
                )
            }

            // X-Axis
            drawYAxisWithLabels(
                axisConfig = yAxisDefault,
                maxValue = ceil(maxYValue),
                textMeasurer = textMeasurer
            )
        }

        // Labels for each bar
        entries.forEachIndexed { index, dataPoint ->
            drawXLabel(
                data = dataPoint.xValue,
                // Adding half the bar width so text is centered.
                centerTextXPosition = getBarOffsetAtIndex(index = index) + barWidthDp.toPx() / 2,
                clipOnBorder = false,
                bottomOffset = -xLabelsHeight,
                textStyle = labelTextStyle,
                textMeasurer = textMeasurer,
            )
        }
    }
}
