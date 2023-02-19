package at.maximilianproell.linechart.config

data class LineConfig(
    val hasSmoothCurve: Boolean,
    val hasDotMarker: Boolean,
    val fillAreaUnderLine: Boolean,
)

object LineConfigDefaults {
    fun lineConfigDefaults() = LineConfig(
        hasSmoothCurve = true,
        hasDotMarker = true,
        fillAreaUnderLine = false
    )
}