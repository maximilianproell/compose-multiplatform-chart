package at.maximilianproell.multiplatformchart.common.sampling


object LTThreeBuckets {
    fun List<Point>.sorted(desiredBuckets: Int): List<Point> {
        return sorted(this, this.size, desiredBuckets)
    }

    fun sorted(input: List<Point>, inputSize: Int, desiredBuckets: Int): List<Point> {
        val results: MutableList<Point> = mutableListOf()
        OnePassBucketizer.bucketize(input, inputSize, desiredBuckets)
            .windowed(size = 3, step = 1)
            .map(Triangle::of)
            .forEach { triangle ->
                if (results.size == 0) results.add(triangle.first)
                results.add(triangle.result)
                if (results.size == desiredBuckets + 1) results.add(triangle.last)
            }
        return results
    }
}