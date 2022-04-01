package com.example.cobex.timelineview

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.CompositionArtifact
import com.example.cobex.timelineview.TimelineDelegate.Companion.getAdapter

/**
 * # The real Adapter
 *
 * * Work with a delegate approach
 *
 * * Unlike normal recyclerview approaches,
 *   holders are not an inner class of adapters.
 *
 * * Adapter and Holder a linked over its own TimelineObject!
 *
 * ## [TimelineDelegate] ->  Sealed class of all Adapters
 *
 * ## [TimelineViewHolder]  ->  Sealed class of all Holder
 *
 * ## [TimelineObject] -> Sealed class of concrete data of the Items
 *
 *
 */
class TimelineAdapterTimeline(
    var timelineItems: MutableList<TimelineObject>,
    private val manager: TimelineManager
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CompositionArtifact.IArtifact,
    TimelineItemHelperAdapter {

    var itemTouchHelper: ItemTouchHelper? = null
    private val timelineObjects = TimelineObject.Type.values()

    private var adapter: TimelineDelegate? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        val timelineObject = timelineObjects[viewType]
        adapter = getAdapter(timelineObject)
        return adapter!!.onCreateViewHolder(parent, itemTouchHelper!!, manager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapter!!.onBindViewHolder(holder, timelineItems[position])
    }

    override fun getItemViewType(position: Int) = timelineItems[position].type.ordinal

    override fun getItemCount(): Int = timelineItems.size

    override fun onItemMove(initialPosition: Int, endPosition: Int) {
        val temp = timelineItems[initialPosition]
        timelineItems.removeAt(initialPosition)
        timelineItems.add(endPosition, temp)
        notifyItemMoved(initialPosition, endPosition)
    }

    override fun onItemSwiped(position: Int) {
        exchangeList(timelineItems[position])
        timelineItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun extendImage(position: Int) {
        val image = timelineItems[position] as TimelineObject.ImageItem
        val biggerImage =
            TimelineObject.BigImage(image.id, image.createdTimeAsString, image.imgSrc)
        replaceItems(position, biggerImage)
    }

    fun shrinkImage(position: Int) {
        val image = timelineItems[position] as TimelineObject.BigImage
        val smallerImage =
            TimelineObject.ImageItem(image.id, image.createdTimeAsString, image.imgSrc)
        replaceItems(position, smallerImage)
    }

    private fun replaceItems(position: Int, ele: TimelineObject) {
        timelineItems.removeAt(position)
        timelineItems.add(position, ele)
        notifyItemChanged(position)
    }

    private fun exchangeList(item: TimelineObject) {
        manager.exchangeItem(item)
    }

}