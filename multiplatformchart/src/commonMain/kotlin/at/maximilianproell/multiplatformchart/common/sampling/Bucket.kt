package at.maximilianproell.multiplatformchart.common.sampling

internal class Bucket private constructor(
    private val data: List<Point>,
    val first: Point,
    val last: Point,
    val center: Point,
    result: Point
) {
    var result: Point
        private set

    init {
        this.result = result
    }

    fun setResult(result: Point) {
        this.result = result
    }

    fun <U> map(mapper: (Point) -> U): List<U> {
        return data.map(mapper)
    }

    companion object {
        fun of(us: List<Point>): Bucket {
            val first = us[0]
            val last = us[us.size - 1]
            val center = first.add(last.subtract(first).half())
            return Bucket(us, first, last, center, first)
        }

        fun of(u: Point): Bucket {
            return Bucket(listOf(u), u, u, u, u)
        }
    }
}