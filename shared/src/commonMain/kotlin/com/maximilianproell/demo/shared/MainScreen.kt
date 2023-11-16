package com.maximilianproell.demo.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.maximilianproell.multiplatformchart.barchart.BarChart
import at.maximilianproell.multiplatformchart.barchart.config.BarConfigDefaults.defaultBarConfig
import at.maximilianproell.multiplatformchart.barchart.model.BarChartEntry

@Composable
fun MainScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        BarChart(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            entries = listOf(
                BarChartEntry("MO", 25f),
                BarChartEntry("DI", 50f),
                BarChartEntry("MI", 30f),
                BarChartEntry("DO", 60f),
            ),
            maxYValue = 60f,
            barConfig = defaultBarConfig.copy(animate = true),
        )
    }
}