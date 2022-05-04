package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact

typealias InfluenceDependenciesType = InfluenceDependenciesModel.Type

sealed class InfluenceDependenciesModel(
    val type: InfluenceDependenciesType,
    val pieChartManager: PieChartManager
    ) {

    class ConcretePieChartModel(
        pieChartManager: PieChartManager,
        val context: Context,
        val title: String
    ) : InfluenceDependenciesModel(Type.CONCRETE_PIE_CHART, pieChartManager)


    class PieChartSeekbarModel(
        pieChartManager: PieChartManager,
        val artifact: Artifact,
        val value: Int
    ) : InfluenceDependenciesModel(Type.PIE_CHART_SEEKBAR, pieChartManager)

    enum class Type(val layout: Int) {
        CONCRETE_PIE_CHART(R.layout.pie_chart_concrete),
        PIE_CHART_SEEKBAR(R.layout.pie_chart_seekbar),
    }


}