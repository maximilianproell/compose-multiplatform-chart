package at.maximilianproell.multiplatformchart.demo

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.maximilianproell.multiplatformchart.demo.ui.theme.ComposeLiveLinechartTheme
import at.maximilianproell.multiplatformchart.barchart.BarChart
import at.maximilianproell.multiplatformchart.barchart.config.BarConfigDefaults.defaultBarConfig
import at.maximilianproell.multiplatformchart.barchart.model.BarChartEntry
import at.maximilianproell.multiplatformchart.common.AxisConfigDefaults
import at.maximilianproell.multiplatformchart.linechart.LineChart
import at.maximilianproell.multiplatformchart.linechart.config.LineConfigDefaults
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToLong

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLiveLinechartTheme {
                val chartData by viewModel.chartData.collectAsStateWithLifecycle(initialValue = emptyList())

                Surface(modifier = Modifier.fillMaxSize()) {
                    LineChart(
                        modifier = Modifier.padding(16.dp),
                        lineDataSets = { chartData },
                        minVisibleYValue = 40f,
                        maxVisibleYValue = 80f,
                        lineConfig = LineConfigDefaults.lineConfigDefaults()
                            .copy(
                                showLineDots = false,
                            ),
                        xAxisConfig = AxisConfigDefaults.xAxisConfigDefaults()
                            .copy(
                                numberOfLabels = 2,
                                labelsFormatter = { xValue ->
                                    DateUtils.formatElapsedTime(xValue.roundToLong())
                                },
                                labelsYOffset = 16.dp
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

@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    ComposeLiveLinechartTheme {
        Surface {
            BarChart(
                modifier = Modifier.fillMaxWidth().height(400.dp).padding(16.dp),
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