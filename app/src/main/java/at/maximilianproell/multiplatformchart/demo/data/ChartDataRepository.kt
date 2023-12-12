package at.maximilianproell.multiplatformchart.demo.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import at.maximilianproell.multiplatformchart.demo.R
import at.maximilianproell.multiplatformchart.demo.data.local.ChartLocalDataSource
import at.maximilianproell.multiplatformchart.demo.di.ApplicationIOScope
import at.maximilianproell.multiplatformchart.linechart.model.DataPoint
import at.maximilianproell.multiplatformchart.linechart.model.LineDataSet
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ChartDataRepository {
    /**
     * Observe the live data of the last minute.
     */
    fun observeChartData(): StateFlow<List<ChartData>>


    suspend fun getHeartRateChartData(): List<LineDataSet>
}

class FakeChartDataRepository @Inject constructor(
    private val chartLocalDataSource: ChartLocalDataSource,
    @ApplicationIOScope applicationCoroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context,
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

    override suspend fun getHeartRateChartData(): List<LineDataSet> {
        val mutableDataPointList = mutableListOf<DataPoint>()
        val inputStream = context.resources.openRawResource(R.raw.heartrate_seconds_merged)
        csvReader().open(inputStream) {
            readAllAsSequence().drop(1).forEachIndexed { index, strings ->
                strings.drop(2).forEach { string ->
                    mutableDataPointList.add(
                        DataPoint(
                            xValue = index.toFloat(),
                            yValue = string.toFloat()
                        )
                    )
                }
            }
        }

        return listOf(
            LineDataSet(
                name = "Heart Rate",
                dataPoints = mutableDataPointList.toList(),
                lineColor = Color.Red,
            )
        )
    }
}

data class ChartData(
    val xTimestamp: Long,
    val y: Float
)