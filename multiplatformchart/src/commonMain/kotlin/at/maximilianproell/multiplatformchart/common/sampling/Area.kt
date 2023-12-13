package at.maximilianproell.multiplatformchart.common.sampling

import kotlin.math.absoluteValue


internal class Area private constructor(val generator: Point, val value: Double) {

    companion object {
        fun ofTriangle(a: Point, b: Point, c: Point): Area {
            // area of a triangle = |[Ax(By - Cy) + Bx(Cy - Ay) + Cx(Ay - By)] / 2
            val addends: List<Double> = listOf(
                a.x * (b.y - c.y),
                b.x * (c.y - a.y),
                c.x * (a.y - b.y)
            )
            val sum: Double = addends.sum()
            val half: Double = sum / 2
            val value: Double = half.absoluteValue
            return Area(b, value)
        }
    }
}