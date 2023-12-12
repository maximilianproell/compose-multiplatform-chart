package at.maximilianproell.multiplatformchart.common.sampling

internal class Triangle private constructor(
    left: Bucket,
    center: Bucket,
    right: Bucket
) {
    private val left: Bucket
    private val center: Bucket
    private val right: Bucket

    init {
        this.left = left
        this.center = center
        this.right = right
    }

    val first: Point
        get() = left.first
    val last: Point
        get() = right.last
    val result: Point
        get() {
            val resultPoint = center.map { b ->
                Area.ofTriangle(
                    left.result,
                    b,
                    right.center
                )
            }.maxBy(Area::value).generator
            center.result = resultPoint
            return resultPoint
        }

    companion object {
        fun of(buckets: List<Bucket>): Triangle {
            return Triangle(
                buckets[0],
                buckets[1],
                buckets[2]
            )
        }
    }
}