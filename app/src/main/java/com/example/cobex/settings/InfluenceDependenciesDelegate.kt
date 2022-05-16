package com.example.cobex.settings

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.R
import com.example.cobex.settings.InfluenceDependenciesHolder.Companion.getHolder


/**
 *
 * ## Parent class of all [RecyclerView.Adapter] of the [InfluenceDependenciesAdapter].
 * * Unlike normal [RecyclerView.Adapter] these have not an inner class of their holder!
 *
 * ### These Adapters should be and are linked to each other via their matching [InfluenceDependenciesType].
 *
 *+ This class provides the static method [getAdapter].
 * This function returns the correct adapter based on the [InfluenceDependenciesType].
 *
 * *Like the function in [InfluenceDependenciesHolder.getHolder]*
 *
 *
 * @sample getAdapter
 */
sealed class InfluenceDependenciesDelegate{

    fun onCreateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
    = InfluenceDependenciesHolder.getHolder(getType(), parent)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, model: InfluenceDependenciesModel) {
        holder as InfluenceDependenciesHolder
        holder.bind(model)
    }

    /**
     * @return [InfluenceDependenciesType]
     */
    abstract fun getType(): InfluenceDependenciesType


    /**==========================  ALL ADAPTERS ==========================*/


    /**
     *
     *  TimelineObject: [InfluenceDependenciesModel.Type.CONCRETE_PIE_CHART]
     *
     *  Adapter of Layout [R.layout.pie_chart_concrete]
     *
     *  ViewHolder: [InfluenceDependenciesHolder.ConcretePieChartHolder]
     *
     */
    private object ConcretePieChartAdapter : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.CONCRETE_PIE_CHART
    }

    /**
     *
     *  TimelineObject: [InfluenceDependenciesModel.Type.PIE_CHART_SEEKBAR]
     *
     *  Adapter of Layout [R.layout.pie_chart_seekbar]
     *
     *  ViewHolder: [InfluenceDependenciesHolder.PieChartSeekBarHolder]
     *
     */
    private object PieChartSeekbarAdapter : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.PIE_CHART_SEEKBAR
    }

    /**
     *
     *  TimelineObject: [InfluenceDependenciesModel.Type.PIE_TICK_BOX]
     *
     *  Adapter of Layout [R.layout.pie_chart_tick_box]
     *
     *  ViewHolder: [InfluenceDependenciesHolder.PieChartTickBoxHolder]
     *
     */
    private object PieChartTickBox : InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.PIE_TICK_BOX
    }

    /**
     *
     *  TimelineObject: [InfluenceDependenciesModel.Type.PIE_CHART_DIVISION]
     *
     *  Adapter of Layout [R.layout.pie_chart_division]
     *
     *  ViewHolder: [InfluenceDependenciesHolder.PieChartDivisionHolder]
     *
     */
    private object PieChartDivision: InfluenceDependenciesDelegate(){
        override fun getType() = InfluenceDependenciesType.PIE_CHART_DIVISION
    }

    companion object{

        /**
         * @return a Adapter matching the [InfluenceDependenciesType]
         *
         * @sample getHolder
         */
        fun getAdapter(type: InfluenceDependenciesType) =
            when(type){
                InfluenceDependenciesType.PIE_CHART_SEEKBAR -> PieChartSeekbarAdapter
                InfluenceDependenciesType.CONCRETE_PIE_CHART -> ConcretePieChartAdapter
                InfluenceDependenciesType.PIE_TICK_BOX -> PieChartTickBox
                InfluenceDependenciesType.PIE_CHART_DIVISION -> PieChartDivision
            }
    }
}