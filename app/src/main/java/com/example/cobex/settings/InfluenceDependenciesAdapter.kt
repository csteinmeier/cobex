package com.example.cobex.settings

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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
    }

    override fun getItemCount() = dependencies.size

    override fun getItemViewType(position: Int): Int {
        return dependencies[position].type.ordinal
    }

}