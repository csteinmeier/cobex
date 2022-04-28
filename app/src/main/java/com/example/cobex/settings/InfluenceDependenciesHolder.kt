package com.example.cobex.settings

import android.content.ContextWrapper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.ViewHelper
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

            PieChartManager(
                context = context,
                pie = pieChart,
                model.artifacts
            )
        }
    }

    private class PieChartSeekBarHolder(
        parent: ViewGroup
    ) : InfluenceDependenciesHolder(
        InfluenceDependenciesType.PIE_CHART_SEEKBAR, parent
    ){
        override fun bind(model: InfluenceDependenciesModel) : Unit = with(itemView) {
            model as InfluenceDependenciesModel.PieChartSeekbarModel

            influence_dependencies_card_view.setCardBackgroundColor(ContextWrapper(context).getColor(model.artifact.color))

            ViewHelper.ImageViewDrawable(
                influence_dependencies_icon_seekbar, model.artifact.symbol, context
            )

            ViewHelper.TextFieldResourceString(
                influence_dependencies_text_seekbar, model.artifact.displayString, context
            )

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