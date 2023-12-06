package at.maximilianproell.multiplatformchart.common.sampling


internal class OnePassBucketizer {
    companion object {
        fun bucketize(input: List<Point>, inputSize: Int, desiredBuckets: Int): List<Bucket> {
            val middleSize = inputSize - 2
            val bucketSize = middleSize / desiredBuckets
            val remainingElements = middleSize % desiredBuckets
            if (bucketSize == 0) {
                // Input is smaller than desired number of buckets
                return input.map {
                    Bucket.of(it)
                }
            }
            val buckets: MutableList<Bucket> = mutableListOf()

            // Add first point as the only point in the first bucket
            buckets.add(Bucket.of(input[0]))
            var rest = input.subList(1, input.size - 1)

            // Add middle buckets.
            // When inputSize is not a multiple of desiredBuckets, remaining elements are equally distributed on the first buckets.
            while (buckets.size < desiredBuckets + 1) {
                val size = if (buckets.size <= remainingElements) bucketSize + 1 else bucketSize
                buckets.add(Bucket.of(rest.subList(0, size)))
                rest = rest.subList(size, rest.size)
            }

            // Add last point as the only point in the last bucket
            buckets.add(Bucket.of(input[input.size - 1]))
            return buckets
        }
    }
}