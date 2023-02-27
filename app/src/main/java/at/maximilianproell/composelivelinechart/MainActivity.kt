package at.maximilianproell.composelivelinechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import at.maximilianproell.composelivelinechart.ui.theme.ComposeLiveLinechartTheme
import at.maximilianproell.linechart.LineChart
import at.maximilianproell.linechart.config.LineConfigDefaults
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLiveLinechartTheme {
                val chartData by viewModel.chartData.collectAsState(initial = emptyList())

                Surface(modifier = Modifier.fillMaxSize()) {
                    LineChart(
                        lineDataSets = { chartData },
                        maxVisibleYValue = 80f,
                        lineConfig = LineConfigDefaults.lineConfigDefaults().copy(
                            showLineDots = false
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeLiveLinechartTheme {

    }
}