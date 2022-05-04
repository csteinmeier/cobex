package com.example.cobex.settings

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToColor
import com.example.cobex.helper.Extensions.resourceToString
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

class PieChartManager(
    val context: Context,
    val artifacts: List<Artifact>
)  {


    lateinit var entries : List<PieEntry>
    lateinit var values: MutableMap<Artifact, Float>
    lateinit var pieDataSet: PieDataSet
    private var pie : PieChart ?= null

    fun initPieChart(pie: PieChart) = with(pie){
        setUsePercentValues(false)

        this@PieChartManager.pie = pie

        description.isEnabled = false
        legend.isEnabled = false


        transparentCircleRadius = 61f
        holeRadius = 20f
        setHoleColor(R.color.defaultBackground.resourceToColor(context))

        values =
            artifacts.zip(artifacts.map { 100f / artifacts.size }).toMap() as MutableMap<Artifact, Float>

        entries =
            generatePieEntries()

        pieDataSet = PieDataSet(entries, "")
        pieDataSet.initial(artifacts)
        data = PieData(pieDataSet)
    }

    private fun generatePieEntries() = values.map { entry: Map.Entry<Artifact, Float> ->
        PieEntry(entry.value, entry.key)
    }

    private fun getColorsFromArtifact(artifacts: List<Artifact>) =
        artifacts.map { it.color.resourceToColor(context) }


    private fun PieDataSet.initial(artifacts: List<Artifact>){
        sliceSpace = 5f
        selectionShift = 10f
        valueTextSize = 10f
        /***
         * TODO Percent?  ${value%}
         */
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float) = ""
        }

        colors = getColorsFromArtifact(artifacts)
    }

    fun onChanged(value: Int, artifact: Artifact) {
        values[artifact] = value.toFloat()
        val pieDataSet = PieDataSet(generatePieEntries(), "")
        pieDataSet.initial(artifacts)
        this.pie!!.data = PieData(pieDataSet)
        this.pie!!.invalidate()
    }

}