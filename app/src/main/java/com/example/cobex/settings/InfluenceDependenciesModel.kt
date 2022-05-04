package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact

typealias InfluenceDependenciesType = InfluenceDependenciesModel.Type

sealed class InfluenceDependenciesModel(
    val type: InfluenceDependenciesType,
    val pieChartManager: PieChartManager?,
    ) {

    class ConcretePieChartModel(
        pieChartManager: PieChartManager,
        val context: Context,
        val title: String
    ) : InfluenceDependenciesModel(Type.CONCRETE_PIE_CHART, pieChartManager)


    class PieChartSeekbarModel(
        pieChartManager: PieChartManager,
        val pieCheckBoxManager: PieCheckBoxManager ?= null,
        val artifact: Artifact,
        val value: Int
    ) : InfluenceDependenciesModel(Type.PIE_CHART_SEEKBAR, pieChartManager)

    class PieTickBoxModel(
        pieChartManager: PieChartManager,
        val stringRes: Int,
        val pieCheckBoxManager: PieCheckBoxManager ?= null,
        val artifact: Artifact,
    ) : InfluenceDependenciesModel(Type.PIE_TICK_BOX, pieChartManager)

    class PieChartDivisionModel(
        val artifact_0: Artifact,
        val artifact_1: Artifact
    ) : InfluenceDependenciesModel(Type.PIE_CHART_DIVISION, null)

    enum class Type(val layout: Int) {
        CONCRETE_PIE_CHART(R.layout.pie_chart_concrete),
        PIE_CHART_SEEKBAR(R.layout.pie_chart_seekbar),
        PIE_TICK_BOX(R.layout.pie_chart_tick_box),
        PIE_CHART_DIVISION(R.layout.pie_chart_division)
    }


}