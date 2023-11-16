package at.maximilianproell.multiplatformchart.demo.data.local

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class ChartLocalDataSource @Inject constructor() {
    private fun someValueAtTimestamp(timestamp: Long): Float {
        val x = timestamp / 100
        val tsi = (cos(x * 0.06) + 5) * 12
        return tsi.toFloat()
    }

    fun liveDataFlow(hz: Long): Flow<Float> {
        val delayTime = 1000 / hz
        return flow {
            while (true) {
                emit(someValueAtTimestamp(System.currentTimeMillis()))
                delay(delayTime)
            }
        }
    }
}