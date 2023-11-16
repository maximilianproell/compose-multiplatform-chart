package at.maximilianproell.multiplatformchart.demo.data

import at.maximilianproell.multiplatformchart.demo.data.local.ChartLocalDataSource
import at.maximilianproell.multiplatformchart.demo.di.ApplicationIOScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface ChartDataRepository {
    /**
     * Observe the live data of the last minute.
     */
    fun observeChartData(): StateFlow<List<ChartData>>
}

class FakeChartDataRepository @Inject constructor(
    private val chartLocalDataSource: ChartLocalDataSource,
    @ApplicationIOScope applicationCoroutineScope: CoroutineScope
) : ChartDataRepository {
    private val mutableSensorDataOfLastMinute: MutableList<ChartData> = mutableListOf()
    private val mutableChartFlow: MutableStateFlow<List<ChartData>> = MutableStateFlow(emptyList())

    private val startTimestamp = System.currentTimeMillis()

    init {
        applicationCoroutineScope.launch {
            chartLocalDataSource.liveDataFlow(hz = 10).collect { value ->
                if (mutableSensorDataOfLastMinute.size > 100) {
                    mutableSensorDataOfLastMinute.removeFirst()
                }
                mutableSensorDataOfLastMinute.add(
                    ChartData(System.currentTimeMillis() - startTimestamp, value)
                )

                // update data
                mutableChartFlow.value = mutableSensorDataOfLastMinute.toList()
            }
        }
    }

    override fun observeChartData(): StateFlow<List<ChartData>> = mutableChartFlow.asStateFlow()
}

data class ChartData(
    val xTimestamp: Long,
    val y: Float
)