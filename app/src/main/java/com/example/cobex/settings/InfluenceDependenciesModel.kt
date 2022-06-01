package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact

typealias InfluenceDependenciesType = InfluenceDependenciesModel.Type

/**
 * Data classes for each Layout and its Views
 */
sealed class InfluenceDependenciesModel(
    val type: InfluenceDependenciesType,
    val pieChartManager: PieChartManager?,
    ) {

    /**
     * Data Class for [R.layout.pie_chart_concrete]
     *
     * @param pieChartManager Connection between PieChart and PieChartSeekbar
     *
     * @param title shown as headline above the PieChart
     *
     */
    class ConcretePieChart(
        pieChartManager: PieChartManager,
        val context: Context,
        val title: String
    ) : InfluenceDependenciesModel(Type.CONCRETE_PIE_CHART, pieChartManager)


    /**
     * Data Class for [R.layout.pie_chart_seekbar]
     *
     * @param pieChartManager Connection between PieChart and PieChartSeekbar
     *
     * @param artifact same artifacts the Manager controls
     *
     */
    class PieChartSeekbar(
        pieChartManager: PieChartManager,
        val pieCheckBoxManager: PieCheckBoxManager ?= null,
        val artifact: Artifact,
        val value: Int
    ) : InfluenceDependenciesModel(Type.PIE_CHART_SEEKBAR, pieChartManager)

    /**
     * Data Class for [R.layout.pie_chart_tick_box]
     *
     * @param pieChartManager Connection between PieChart and PieChartSeekbar
     *
     * @param artifact which can be switched off or on
     *
     * @param pieCheckBoxManager informs artifact to be removed
     *
     * @param stringRes text to be shown on the UI
     *
     */
    class PieTickBox(
        pieChartManager: PieChartManager,
        val stringRes: Int,
        val pieCheckBoxManager: PieCheckBoxManager ?= null,
        val artifact: Artifact,
    ) : InfluenceDependenciesModel(Type.PIE_TICK_BOX, pieChartManager)

    /**
     * Data Class for [R.layout.pie_chart_division]
     *
     * @param artifact_0 artifact on the left side
     *
     * @param artifact_1 artifact on the right side
     *
     */
    class PieChartDivision(
        val artifact_0: Artifact,
        val artifact_1: Artifact,
    ) : InfluenceDependenciesModel(Type.PIE_CHART_DIVISION, null)


    /**
     * Used for linking between the individual components of the Influence Dependency Fragment
     */
    enum class Type(val layout: Int) {
        CONCRETE_PIE_CHART(R.layout.pie_chart_concrete),
        PIE_CHART_SEEKBAR(R.layout.pie_chart_seekbar),
        PIE_TICK_BOX(R.layout.pie_chart_tick_box),
        PIE_CHART_DIVISION(R.layout.pie_chart_division)
    }


}