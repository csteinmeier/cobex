package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToColor
import com.example.cobex.helper.Extensions.resourceToString
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class PieChartManager(
    val context: Context,
    pie: PieChart,
    artifacts: List<Artifact>,
) {

    init {
        pie.initial(artifacts)
    }

    private fun getColorsFromArtifact(artifacts: List<Artifact>) =
        artifacts.map { it.color.resourceToColor(context) }

    private fun PieChart.initial(artifacts: List<Artifact>){
        setUsePercentValues(false)

        description.isEnabled = false
        legend.isEnabled = false


        transparentCircleRadius = 61f
        holeRadius = 20f
        setHoleColor(R.color.defaultBackground.resourceToColor(context))

        val values : List<PieEntry> =
            artifacts.map {
                PieEntry( 100f / artifacts.size, it.displayString.resourceToString(context))
            }

        val pieDataSet = PieDataSet(values, "")
        pieDataSet.initial(artifacts)
        data = PieData(pieDataSet)
    }

    private fun PieDataSet.initial(artifacts: List<Artifact>){
        sliceSpace = 5f
        selectionShift = 10f
        valueTextSize = 10f
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float) = "$value%"
        }

        colors = getColorsFromArtifact(artifacts)
    }

}