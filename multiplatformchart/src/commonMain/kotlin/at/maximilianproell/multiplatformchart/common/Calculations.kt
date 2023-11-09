package at.maximilianproell.multiplatformchart.common

import androidx.compose.ui.geometry.Size

internal fun xValueOffset(
    xValue: Float,
    minXData: Float,
    maxXData: Float,
    size: Size,
): Float {
    val length = if (maxXData == minXData) 1f else maxXData - minXData
    val normalizedX = (xValue - minXData) / length
    return normalizedX * size.width
}