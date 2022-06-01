package com.example.cobex.settings

import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.artifacts.CompositionArtifact
import com.example.cobex.R
import com.example.cobex.ViewHelper
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToString
import com.example.cobex.helper.Extensions.toLayout
import com.example.cobex.timelineview.TimelineViewHolder.Companion.getHolder
import kotlinx.android.synthetic.main.pie_chart_concrete.view.*
import kotlinx.android.synthetic.main.pie_chart_division.view.*
import kotlinx.android.synthetic.main.pie_chart_seekbar.view.*
import kotlinx.android.synthetic.main.pie_chart_tick_box.view.*

/**
 *
 * ## Parent class of all [RecyclerView.ViewHolder] of the TimeLineAdapters.
 * * Unlike normal [RecyclerView.ViewHolder] these are not an inner class of their adapter!
 *
 * ### These holders should be and are linked to each other via their matching [InfluenceDependenciesType].
 *
 * + This class provides the static method [getHolder].
 * This function returns the correct holder based on the [InfluenceDependenciesType].
 * *Like the function in [InfluenceDependenciesDelegate.getAdapter]*
 *
 * @sample getHolder
 */
sealed class InfluenceDependenciesHolder(
    type: InfluenceDependenciesType,
    parent: ViewGroup
) :
    RecyclerView.ViewHolder(type.layout.toLayout(parent)){

    /**
     * @param model Dataclass of InfluenceDependenciesModel
     */
    abstract fun bind(model: InfluenceDependenciesModel)

    /**
     * Adapter: [InfluenceDependenciesDelegate.ConcretePieChartAdapter]
     *
     * Layout: [R.layout.pie_chart_concrete]
     *
     * InfluenceDependenciesModel: [InfluenceDependenciesModel.ConcretePieChart]
     */
    private class ConcretePieChartHolder(
        parent: ViewGroup,
    ) : InfluenceDependenciesHolder(
        InfluenceDependenciesType.CONCRETE_PIE_CHART, parent,
    ) {

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.ConcretePieChart

            ViewHelper.SimpleTextField(
                concretePieTitle, model.title)

            model.pieChartManager?.initPieChart(pieChart)

        }
    }

    /**
     * Adapter: [InfluenceDependenciesDelegate.PieChartSeekbarAdapter]
     *
     * Layout: [R.layout.pie_chart_seekbar]
     *
     * InfluenceDependenciesModel: [InfluenceDependenciesModel.PieChartSeekbar]
     */
    private class PieChartSeekBarHolder(
        parent: ViewGroup
    ) : SeekBar.OnSeekBarChangeListener, CompositionArtifact.IArtifact, InfluenceDependenciesHolder(
        InfluenceDependenciesType.PIE_CHART_SEEKBAR, parent
    ){
        var manager : PieChartManager ?= null
        var artifact: Artifact ?= null

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieChartSeekbar

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

            /** To connect manager with the seekbar **/
            this@PieChartSeekBarHolder.manager = model.pieChartManager
            this@PieChartSeekBarHolder.artifact = model.artifact
            influence_dependencies_seekBar.setOnSeekBarChangeListener(this@PieChartSeekBarHolder)
            influence_dependencies_seekBar.progress =
                model.pieChartManager?.values?.get(model.artifact)?.toInt() ?:25

            model.pieCheckBoxManager?.map?.set(artifact!!, influence_dependencies_card_view)
            if(model.pieChartManager?.values?.get(model.artifact)?.toInt() == 0){
                model.pieCheckBoxManager?.toggleCardView(model.artifact)
            }
        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            this.manager!!.onChanged(progress, this.artifact!!)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            this.manager!!.saveValues()
        }

    }


    /**
     * Adapter: [InfluenceDependenciesDelegate.PieChartTickBox]
     *
     * Layout: [R.layout.pie_chart_tick_box]
     *
     * InfluenceDependenciesModel: [InfluenceDependenciesModel.PieTickBox]
     */
    private class PieChartTickBoxHolder(parent: ViewGroup)
        : InfluenceDependenciesHolder(InfluenceDependenciesType.PIE_TICK_BOX, parent){

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieTickBox

            pie_chart_checkBox.text = model.stringRes.resourceToString(context)

            pie_chart_checkBox.setOnCheckedChangeListener { _, isChecked ->
                model.pieCheckBoxManager?.toggleCardView(model.artifact)
                model.pieChartManager?.onCheckBoxClicked(isChecked, model.artifact)
            }
        }
    }


    /**
     * Adapter: [InfluenceDependenciesDelegate.PieChartDivision]
     *
     * Layout: [R.layout.pie_chart_division]
     *
     * InfluenceDependenciesModel: [InfluenceDependenciesModel.PieChartDivision]
     */
    private class PieChartDivisionHolder(parent : ViewGroup)
        : InfluenceDependenciesHolder(InfluenceDependenciesType.PIE_CHART_DIVISION, parent){

        private var divisionBar : DivisionBar?= null
        private var context : Context ?= null
        private var seekBar : SeekBar ?= null

        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieChartDivision

            this@PieChartDivisionHolder.context = context

            influence_dependencies_division_title01.text =
                Artifact.AI.displayString.resourceToString(context)
            influence_dependencies_division_title02.text =
                Artifact.Human.displayString.resourceToString(context)

            this@PieChartDivisionHolder.divisionBar = influence_dependencies_big_bar
            this@PieChartDivisionHolder.seekBar = influence_dependencies_division_seekbar

            divisionBar!!.initValues(model.artifact_0, model.artifact_1, seekBar!!)
        }


    }

    companion object {

        /**
         * @return a holder matching the [InfluenceDependenciesType]
         *
         * @sample getHolder
         */
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