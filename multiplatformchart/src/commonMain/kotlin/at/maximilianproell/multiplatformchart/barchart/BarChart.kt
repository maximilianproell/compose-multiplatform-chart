package at.maximilianproell.multiplatformchart.barchart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import at.maximilianproell.multiplatformchart.barchart.config.BarConfig
import at.maximilianproell.multiplatformchart.barchart.config.BarConfigDefaults.defaultBarConfig
import at.maximilianproell.multiplatformchart.barchart.model.BarChartEntry
import at.maximilianproell.multiplatformchart.common.AxisConfigDefaults.yAxisConfigDefaults
import at.maximilianproell.multiplatformchart.common.LabeledYAxisConfig
import at.maximilianproell.multiplatformchart.common.drawXLabel
import at.maximilianproell.multiplatformchart.common.drawYAxisWithLabels
import kotlin.math.ceil

/**
 * A bar chart displaying the bars
 */
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    entries: List<BarChartEntry>,
    maxYValue: Float,
    barConfig: BarConfig = defaultBarConfig,
    yAxisConfig: LabeledYAxisConfig = yAxisConfigDefaults(),
    xLabelsTextStyle: TextStyle = yAxisConfig.labelTextStyle,
) {
    val barPercentageHeights = if (barConfig.animate) {
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

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val xLabelsHeight = xLabelsTextStyle.lineHeight.toPx() * 1.2f
        fun getBarSpacing(): Float {
            val sizeOccupiedByBars = barConfig.barWidth.toPx() * entries.size
            return (size.width - sizeOccupiedByBars) / (entries.size - 1)
        }

        fun getBarOffsetAtIndex(index: Int) = (getBarSpacing() + barConfig.barWidth.toPx()) * index

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
                    color = barConfig.barColor,
                    size = Size(width = barConfig.barWidth.toPx(), height = barHeight),
                    topLeft = Offset(currentBarOffset, canvasHeight - barHeight)
                )
            }

            // Y-Axis
            drawYAxisWithLabels(
                axisConfig = yAxisConfig,
                maxValue = maxYValue,
                textMeasurer = textMeasurer
            )
        }

        // Labels for each bar
        entries.forEachIndexed { index, dataPoint ->
            drawXLabel(
                data = dataPoint.xValue,
                // Adding half the bar width so text is centered.
                centerTextXPosition = getBarOffsetAtIndex(index = index) + barConfig.barWidth.toPx() / 2,
                clipOnBorder = false,
                bottomOffset = -xLabelsHeight,
                textStyle = xLabelsTextStyle,
                textMeasurer = textMeasurer,
            )
        }
    }
}
