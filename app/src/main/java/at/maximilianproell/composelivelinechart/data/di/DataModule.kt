package at.maximilianproell.composelivelinechart.data.di

import at.maximilianproell.composelivelinechart.data.ChartDataRepository
import at.maximilianproell.composelivelinechart.data.FakeChartDataRepository
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