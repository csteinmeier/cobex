package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact

typealias InfluenceDependenciesType = InfluenceDependenciesModel.Type

sealed class InfluenceDependenciesModel(val type: InfluenceDependenciesType)  {

    class ConcretePieChartModel(
        val context: Context,
        val artifacts: List<Artifact>,
        val title: String
    ) : InfluenceDependenciesModel(Type.CONCRETE_PIE_CHART)


    class PieChartSeekbarModel(
        val artifact: Artifact
    )
        : InfluenceDependenciesModel(Type.PIE_CHART_SEEKBAR)

    enum class Type(val layout: Int){
        CONCRETE_PIE_CHART(R.layout.pie_chart_concrete),
        PIE_CHART_SEEKBAR(R.layout.pie_chart_seekbar),
    }
}