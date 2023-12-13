package at.maximilianproell.multiplatformchart.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.maximilianproell.multiplatformchart.barchart.BarChart
import at.maximilianproell.multiplatformchart.barchart.config.BarConfigDefaults.defaultBarConfig
import at.maximilianproell.multiplatformchart.barchart.model.BarChartEntry
import at.maximilianproell.multiplatformchart.common.AxisConfigDefaults
import at.maximilianproell.multiplatformchart.demo.ui.theme.ComposeLiveLinechartTheme
import at.maximilianproell.multiplatformchart.linechart.LineChart
import at.maximilianproell.multiplatformchart.linechart.LiveLineChart
import at.maximilianproell.multiplatformchart.linechart.config.LineConfigDefaults
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLiveLinechartTheme {
                val liveLineChartData by viewModel.liveChartData.collectAsStateWithLifecycle(initialValue = emptyList())
                val staticHeartRateChartData by viewModel.staticHeartRateChartData
                val currentDisplayMode by viewModel.currentDisplayMode

                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            viewModel.currentDisplayMode.value = when (currentDisplayMode) {
                                MainActivityViewModel.DisplayMode.LIVE_LINE_CHART ->
                                    MainActivityViewModel.DisplayMode.HUGE_DATA_STATIC_LINE_CHART

                                MainActivityViewModel.DisplayMode.HUGE_DATA_STATIC_LINE_CHART ->
                                    MainActivityViewModel.DisplayMode.LIVE_LINE_CHART
                            }
                        }) {
                            val textString = when (currentDisplayMode) {
                                MainActivityViewModel.DisplayMode.LIVE_LINE_CHART -> "Live Data"
                                MainActivityViewModel.DisplayMode.HUGE_DATA_STATIC_LINE_CHART -> "Static Data"
                            }

                            Text(text = textString)
                        }
                        when (currentDisplayMode) {
                            MainActivityViewModel.DisplayMode.LIVE_LINE_CHART -> {
                                LiveLineChart(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    lineDataSets = { liveLineChartData },
                                    minVisibleYValue = 40f,
                                    maxVisibleYValue = 80f,
                                    lineConfig = LineConfigDefaults.lineConfigDefaults()
                                        .copy(
                                            showLineDots = false,
                                            fillAreaUnderLine = false,
                                        ),
                                    xAxisConfig = AxisConfigDefaults.xAxisConfigDefaults()
                                        .copy(
                                            labelsYOffset = (-16).dp,
                                            labelsFormatter = { xValue ->
                                                val seconds = xValue.toInt() % 60
                                                val minutes = (xValue.toInt() / 60) % 60
                                                "$minutes:$seconds"
                                            }
                                        ),
                                    yAxisConfig = AxisConfigDefaults.yAxisConfigDefaults()
                                        .copy(
                                            showLines = false,
                                            numberOfLabels = 5
                                        )
                                )
                            }

                            MainActivityViewModel.DisplayMode.HUGE_DATA_STATIC_LINE_CHART -> {
                                LineChart(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .weight(1f),
                                    lineDataSets = staticHeartRateChartData,
                                    minVisibleYValue = 40f,
                                    maxVisibleYValue = 180f,
                                    lineConfig = LineConfigDefaults.lineConfigDefaults()
                                        .copy(
                                            showLineDots = false,
                                            fillAreaUnderLine = false,
                                        ),
                                    xAxisConfig = AxisConfigDefaults.xAxisConfigDefaults()
                                        .copy(
                                            labelsYOffset = (-16).dp,
                                            labelsFormatter = { xValue ->
                                                val seconds = xValue.toInt() % 60
                                                val minutes = (xValue.toInt() / 60) % 60
                                                val hours = xValue.toInt() / 3600
                                                "$hours:$minutes:$seconds"
                                            }
                                        ),
                                    yAxisConfig = AxisConfigDefaults.yAxisConfigDefaults()
                                        .copy(
                                            showLines = false,
                                            numberOfLabels = 5
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    ComposeLiveLinechartTheme {
        Surface {
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                entries = listOf(
                    BarChartEntry("One", 0.5f),
                    BarChartEntry("Two", 1.25f),
                    BarChartEntry("Three", 3f),
                    BarChartEntry("Four", 1f),
                ),
                maxYValue = 3f,
                barConfig = defaultBarConfig.copy(animate = false),
            )
        }
    }
}