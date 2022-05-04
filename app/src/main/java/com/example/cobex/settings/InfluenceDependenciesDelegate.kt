package com.example.cobex.settings

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

sealed class InfluenceDependenciesDelegate{

    fun onCreateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
    = InfluenceDependenciesHolder.getHolder(getType(), parent)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, model: InfluenceDependenciesModel) {
        holder as InfluenceDependenciesHolder
        holder.bind(model)
    }

    abstract fun getType(): InfluenceDependenciesType

    private object ConcretePieChartAdapter : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.CONCRETE_PIE_CHART
    }

    private object PieChartSeekbarAdapter : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.PIE_CHART_SEEKBAR
    }

    private object PieChartTickBox : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.PIE_TICK_BOX
    }

    companion object{
        fun getAdapter(type: InfluenceDependenciesType) =
            when(type){
                InfluenceDependenciesType.PIE_CHART_SEEKBAR -> PieChartSeekbarAdapter
                InfluenceDependenciesType.CONCRETE_PIE_CHART -> ConcretePieChartAdapter
                InfluenceDependenciesType.PIE_TICK_BOX -> PieChartTickBox
            }
    }
}