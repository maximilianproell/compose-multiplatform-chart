package at.maximilianproell.multiplatformchart.common.sampling

data class Point(
    val x: Double,
    val y: Double
) {

    fun add(other: Point): Point {
        return this.copy(
            x = x + other.x,
            y = y + other.y,
        )
    }

    fun subtract(other: Point): Point {
        return this.copy(
            x = x - other.x,
            y = y - other.y,
        )
    }

    fun half(): Point {
        return Point(
            x / 2,
            y / 2,
        )
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}