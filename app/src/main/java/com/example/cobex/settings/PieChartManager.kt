package com.example.cobex.settings

import android.content.Context
import com.example.cobex.CompositionArtifact
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToColor
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * Connection between PieChart and its seekbars.
 * Will update the specific Pie Chart if a Seekbar is dragged
 */
class PieChartManager(
    private val context: Context,
    private val artifacts: List<Artifact>
) : CompositionArtifact.IArtifact, SaveAbleChart {

    private lateinit var entries: List<PieEntry>
    lateinit var values: MutableMap<Artifact, Float>
    private lateinit var pieDataSet: PieDataSet
    private var pie: PieChart? = null



    fun initPieChart(pie: PieChart) = with(pie) {
        setUsePercentValues(false)

        this@PieChartManager.pie = pie

        description.isEnabled = false
        legend.isEnabled = false


        transparentCircleRadius = 61f
        holeRadius = 20f
        setHoleColor(R.color.defaultBackground.resourceToColor(context))


        values =  getSavedValuesOrNew(context, this@PieChartManager.javaClass, artifacts)


        entries = values.toPieEntries()

        pieDataSet = PieDataSet(entries, "")
        pieDataSet.initial(artifacts)
        data = PieData(pieDataSet)
    }


    /**
     * Transform a MuteAbleMap<Artifact, Float> to PieEntries
     */
    private fun MutableMap<Artifact, Float>.toPieEntries() =
        this.map { PieEntry(it.value, it.key) }

    /**
     * Colors to be assigned to the individual parts.
     * Set as attribute in the respective [Artifact]
     */
    private fun List<Artifact>.extractColor() =
        this.map { it.color.resourceToColor(context) }

    private fun PieDataSet.initial(artifacts: List<Artifact>) {
        sliceSpace = 5f
        selectionShift = 10f
        valueTextSize = 10f
        /***
         * TODO Percent?  ${value%}
         */
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float) = ""
        }

        colors = artifacts.extractColor()
    }

    /**
     * will update the Pie a new Value
     */
    fun onChanged(value: Int, artifact: Artifact) {
        values[artifact] = value.toFloat()
        updateData()
    }

    /**
     * Removes the matching artifact
     */
    fun onCheckBoxClicked(isChecked: Boolean, artifact: Artifact) {
        when (isChecked) {
            true -> values[artifact] = 0f
            false -> values[artifact] = 100f / entries.size
        }
        saveValues()
        updateData()
    }

    /**
     * Save the values in CompositionArtifact
     */
    fun saveValues() {
        saveValues(context, this.javaClass, this.values)
    }

    /**
     * Called to set new Data
     */
    private fun updateData() {
        val pieDataSet = PieDataSet(values.toPieEntries(), "")
        pieDataSet.initial(artifacts)
        this.pie!!.data = PieData(pieDataSet)
        this.pie!!.invalidate()
    }

}