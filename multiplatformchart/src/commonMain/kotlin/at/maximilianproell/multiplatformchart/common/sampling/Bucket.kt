package at.maximilianproell.multiplatformchart.common.sampling

internal class Bucket private constructor(
    private val data: List<Point>,
    val first: Point,
    val last: Point,
    val center: Point,
    var result: Point
) {
    fun <U> map(mapper: (Point) -> U): List<U> {
        return data.map(mapper)
    }

    companion object {
        fun of(us: List<Point>): Bucket {
            val first = us[0]
            val last = us.last()
            val center = first + (last - first).half()
            return Bucket(
                data = us,
                first = first,
                last = last,
                center = center,
                result = first
            )
        }

        fun of(u: Point): Bucket {
            return Bucket(listOf(u), u, u, u, u)
        }
    }
}