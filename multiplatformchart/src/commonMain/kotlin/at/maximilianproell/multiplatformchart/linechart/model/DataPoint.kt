package at.maximilianproell.multiplatformchart.linechart.model

import androidx.compose.ui.util.fastForEachIndexed
import kotlin.math.ceil

/**
 * Represents a data point in the line chart.
 */
data class DataPoint(val xValue: Float, val yValue: Float)

// TODO: very basic implementation, just for testing.
/**
 * This function assumes that the given list of [DataPoint] is already sorted.
 */
fun List<DataPoint>.filterInVisibleRange(minXData: Float, maxXData: Float): List<DataPoint> {
    return filter { it.xValue in minXData..maxXData }
}

// TODO: optimize; very basic implementation for testing
// TODO: move, since this is a generic implementation
fun <T> List<T>.downSample(maxValues: Int): List<T> {
    if (size <= maxValues) return this

    val stepSize = ceil(size / maxValues.toFloat()).toInt()

    return buildList {
        this@downSample.fastForEachIndexed { index, value ->
            if (index % stepSize == 0 || index == lastIndex) add(value)
        }
    }
}