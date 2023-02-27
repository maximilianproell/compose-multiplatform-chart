package at.maximilianproell.composelivelinechart

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import at.maximilianproell.composelivelinechart.data.ChartDataRepository
import at.maximilianproell.linechart.model.DataPoint
import at.maximilianproell.linechart.model.LineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    repository: ChartDataRepository
) : ViewModel() {

    val chartData = repository.observeChartData().map { chartData ->
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
}