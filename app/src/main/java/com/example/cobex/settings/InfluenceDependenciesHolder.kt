package com.example.cobex.settings

import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.ViewHelper
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToBitmap
import com.example.cobex.helper.Extensions.resourceToString
import com.example.cobex.helper.Extensions.toLayout
import com.example.cobex.view.DivisionBar
import kotlinx.android.synthetic.main.pie_chart_concrete.view.*
import kotlinx.android.synthetic.main.pie_chart_division.view.*
import kotlinx.android.synthetic.main.pie_chart_seekbar.view.*
import kotlinx.android.synthetic.main.pie_chart_tick_box.view.*

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

            model.pieChartManager?.initPieChart(pieChart)

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

            influence_dependencies_seekBar.apply {
                progress = model.value
                setOnSeekBarChangeListener(this@PieChartSeekBarHolder)
            }

            ViewHelper.ImageViewDrawable(
                influence_dependencies_icon_seekbar, model.artifact.symbol, context
            )

            ViewHelper.TextFieldResourceString(
                influence_dependencies_text_seekbar, model.artifact.displayString, context
            )

            this@PieChartSeekBarHolder.manager = model.pieChartManager
            this@PieChartSeekBarHolder.artifact = model.artifact

            influence_dependencies_seekBar.setOnSeekBarChangeListener(this@PieChartSeekBarHolder)

            model.pieCheckBoxManager?.map?.set(artifact!!, influence_dependencies_card_view)

        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            this.manager!!.onChanged(progress, this.artifact!!)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    }

    private class PieChartTickBoxHolder(parent: ViewGroup)
        : InfluenceDependenciesHolder(InfluenceDependenciesType.PIE_TICK_BOX, parent){

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieTickBoxModel

            pie_chart_checkBox.text = model.stringRes.resourceToString(context)

            pie_chart_checkBox.setOnCheckedChangeListener { _, isChecked ->
                model.pieCheckBoxManager?.toggleCardView(model.artifact)
                model.pieChartManager?.onCheckBoxClicked(isChecked, model.artifact)
            }
        }
    }


    private class PieChartDivisionHolder(parent : ViewGroup)
        : InfluenceDependenciesHolder(InfluenceDependenciesType.PIE_CHART_DIVISION, parent),
    SeekBar.OnSeekBarChangeListener{

        private var divisionBar : DivisionBar ?= null
        private var context : Context ?= null

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieChartDivisionModel

            this@PieChartDivisionHolder.context = context

            influence_dependencies_division_title01.text =
                Artifact.AI.displayString.resourceToString(context)
            influence_dependencies_division_title02.text =
                Artifact.Human.displayString.resourceToString(context)

            divisionBar = influence_dependencies_big_bar
            divisionBar!!.symbol01 = Artifact.AI.symbol.resourceToBitmap(context, 70, 70)
            divisionBar!!.symbol02 = Artifact.Human.symbol.resourceToBitmap(context, 70, 70)
            influence_dependencies_division_seekbar.setOnSeekBarChangeListener(this@PieChartDivisionHolder)
        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            divisionBar?.resize(progress.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }

    companion object {

        fun getHolder(
            type: InfluenceDependenciesType,
            parent: ViewGroup
        ) = when(type){
            InfluenceDependenciesType.CONCRETE_PIE_CHART -> ConcretePieChartHolder(parent)
            InfluenceDependenciesType.PIE_CHART_SEEKBAR -> PieChartSeekBarHolder(parent)
            InfluenceDependenciesType.PIE_TICK_BOX -> PieChartTickBoxHolder(parent)
            InfluenceDependenciesType.PIE_CHART_DIVISION -> PieChartDivisionHolder(parent)
        }
    }
}