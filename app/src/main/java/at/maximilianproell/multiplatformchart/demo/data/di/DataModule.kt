package at.maximilianproell.multiplatformchart.demo.data.di

import at.maximilianproell.multiplatformchart.demo.data.ChartDataRepository
import at.maximilianproell.multiplatformchart.demo.data.FakeChartDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindsChartDataRepository(
        chartDataRepository: FakeChartDataRepository
    ): ChartDataRepository
}