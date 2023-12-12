package at.maximilianproell.multiplatformchart.demo

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.maximilianproell.multiplatformchart.demo.data.ChartDataRepository
import at.maximilianproell.multiplatformchart.linechart.model.DataPoint
import at.maximilianproell.multiplatformchart.linechart.model.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    repository: ChartDataRepository
) : ViewModel() {

    enum class DisplayMode {
        LIVE_LINE_CHART,
        HUGE_DATA_STATIC_LINE_CHART,
    }

    val currentDisplayMode = mutableStateOf(DisplayMode.LIVE_LINE_CHART)

    val liveChartData = repository.observeChartData().map { chartData ->
        chartData.map {
            // Convert to data points
            DataPoint(
                xValue = it.xTimestamp / 1000f,
                yValue = it.y
            )
        }
    }.map { dataPoints ->
        listOf(
            LineDataSet(
                name = "Demo data",
                dataPoints = dataPoints,
                lineColor = Color.Red
            )
        )
    }

    val staticHeartRateChartData = mutableStateOf<List<LineDataSet>>(emptyList())

    init {
        viewModelScope.launch {
            staticHeartRateChartData.value = repository.getHeartRateChartData()
        }
    }
}