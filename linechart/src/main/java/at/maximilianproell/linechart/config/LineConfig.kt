package at.maximilianproell.linechart.config

data class LineConfig(
    val hasSmoothCurve: Boolean,
    val showLineDots: Boolean,
    val fillAreaUnderLine: Boolean,
)

object LineConfigDefaults {
    fun lineConfigDefaults() = LineConfig(
        hasSmoothCurve = true,
        showLineDots = true,
        fillAreaUnderLine = false
    )
}