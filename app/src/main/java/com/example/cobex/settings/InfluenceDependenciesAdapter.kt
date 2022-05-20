package com.example.cobex.settings

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.timelineview.TimelineDelegate
import com.example.cobex.timelineview.TimelineObject
import com.example.cobex.timelineview.TimelineViewHolder

/**
 * # The real Adapter
 *
 * * Works with a delegate approach
 *
 * * Unlike normal recyclerview approaches,
 *   holders are not an inner class of adapters.
 *
 * * Adapter and Holder a linked over its own TimelineObject!
 *
 * ## [InfluenceDependenciesDelegate] ->  Sealed class of all Adapters
 *
 * ## [InfluenceDependenciesHolder]  ->  Sealed class of all Holder
 *
 * ## [InfluenceDependenciesModel] -> Sealed class of concrete data of the Items
 *
 */
class InfluenceDependenciesAdapter(
    var dependencies : MutableList<InfluenceDependenciesModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val dependencyTypes = InfluenceDependenciesType.values()

    private var adapter: InfluenceDependenciesDelegate? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : RecyclerView.ViewHolder {
        adapter = InfluenceDependenciesDelegate.getAdapter(dependencyTypes[viewType])
        return adapter!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapter!!.onBindViewHolder(holder, dependencies[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = dependencies.size

    override fun getItemViewType(position: Int): Int {
        return dependencies[position].type.ordinal
    }

}