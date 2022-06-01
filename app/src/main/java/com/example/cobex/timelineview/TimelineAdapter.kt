package com.example.cobex.timelineview

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.artifacts.CompositionArtifact
import com.example.cobex.timelineview.TimelineDelegate.Companion.getAdapter

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
 * ## [TimelineDelegate] ->  Sealed class of all Adapters
 *
 * ## [TimelineViewHolder]  ->  Sealed class of all Holder
 *
 * ## [TimelineObject] -> Sealed class of concrete data of the Items
 *
 */
class TimelineAdapter(
    var timelineItems: MutableList<TimelineObject>,
    private val viewModel: TimelineViewModel
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
        return adapter!!.onCreateViewHolder(parent, itemTouchHelper!!, viewModel)
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

    /**
     * Will place the item to the other list
     */
    override fun onItemSwiped(position: Int) {
        exchangeList(timelineItems[position])
        timelineItems.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Simple function to "extend" an image.
     * Will replace a [TimelineObject.CapturePictureItem] with a [TimelineObject.BigImageItem]
     *
     * TODO (BUG if a User double click by animation)
     */
    fun extendImage(position: Int) {
        val image = timelineItems[position] as TimelineObject.CapturePictureItem
        val biggerImage =
            TimelineObject.BigImageItem(image.id, image.createdTimeAsString, image.imgSrc)
        replaceItems(position, biggerImage)
    }

    /**
     * Simple function to "extend" an image.
     * Will replace a [TimelineObject.BigImageItem] with a [TimelineObject.CapturePictureItem]
     *
     * TODO (BUG if a User double click by animation)
     */
    fun shrinkImage(position: Int) {
        val image = timelineItems[position] as TimelineObject.BigImageItem
        val smallerImage =
            TimelineObject.CapturePictureItem(image.id, image.createdTimeAsString, image.imgSrc)
        replaceItems(position, smallerImage)
    }

    private fun replaceItems(position: Int, ele: TimelineObject) {
        timelineItems.removeAt(position)
        timelineItems.add(position, ele)
        notifyItemChanged(position)
    }

    private fun exchangeList(item: TimelineObject) {
        viewModel.exchangeItem(item)
    }

}