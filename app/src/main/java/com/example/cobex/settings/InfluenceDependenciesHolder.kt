package com.example.cobex.settings

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.ViewHelper
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToString
import com.example.cobex.helper.Extensions.toLayout
import kotlinx.android.synthetic.main.pie_chart_concrete.view.*
import kotlinx.android.synthetic.main.pie_chart_seekbar.view.*

sealed class InfluenceDependenciesHolder(
    type: InfluenceDependenciesType,
    parent: ViewGroup
) :
    RecyclerView.ViewHolder(type.layout.toLayout(parent)){

    abstract fun bind(model: InfluenceDependenciesModel)

    private class ConcretePieChartHolder(
        parent: ViewGroup,
    ) : InfluenceDependenciesHolder(
        InfluenceDependenciesType.CONCRETE_PIE_CHART, parent,
    ) {

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.ConcretePieChartModel

            ViewHelper.SimpleTextField(
                concretePieTitle, model.title)

            model.pieChartManager.initPieChart(pieChart)

        }
    }

    private class PieChartSeekBarHolder(
        parent: ViewGroup
    ) : SeekBar.OnSeekBarChangeListener, InfluenceDependenciesHolder(
        InfluenceDependenciesType.PIE_CHART_SEEKBAR, parent
    ){
        var manager : PieChartManager ?= null
        var artifact: Artifact ?= null

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieChartSeekbarModel

            influence_dependencies_card_view.setCardBackgroundColor(ContextWrapper(context).getColor(model.artifact.color))

            influence_dependencies_seekBar.progress = model.value

            influence_dependencies_seekBar.setOnSeekBarChangeListener(this@PieChartSeekBarHolder)

            ViewHelper.ImageViewDrawable(
                influence_dependencies_icon_seekbar, model.artifact.symbol, context
            )

            ViewHelper.TextFieldResourceString(
                influence_dependencies_text_seekbar, model.artifact.displayString, context
            )


            this@PieChartSeekBarHolder.manager = model.pieChartManager
            this@PieChartSeekBarHolder.artifact = model.artifact

            influence_dependencies_seekBar.setOnSeekBarChangeListener(this@PieChartSeekBarHolder)

        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            this.manager!!.onChanged(progress, this.artifact!!)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }

    }

    companion object {

        fun getHolder(
            type: InfluenceDependenciesType,
            parent: ViewGroup
        ) = when(type){
            InfluenceDependenciesType.CONCRETE_PIE_CHART -> ConcretePieChartHolder(parent)
            InfluenceDependenciesType.PIE_CHART_SEEKBAR -> PieChartSeekBarHolder(parent)
        }
    }
}