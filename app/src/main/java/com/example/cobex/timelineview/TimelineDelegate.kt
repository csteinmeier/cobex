package com.example.cobex.timelineview

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.timelineview.TimelineDelegate.Companion.getAdapter
import com.example.cobex.timelineview.TimelineViewHolder.Companion.getHolder
import com.example.cobex.R

/**
 *
 * ## Parent class of all [RecyclerView.Adapter] of the TimeLineAdapters.
 * * Unlike normal [RecyclerView.Adapter] these have not an inner class of their holder!
 *
 * ### These Adapters should be and are linked to each other via their matching [TimelineObject].
 *
 *+ This class provides the static method [getAdapter].
 * This function returns the correct adapter based on the [TimelineObject.type].
 *
 * *Like the function in [TimelineViewHolder.getHolder]*
 *
 *
 * @sample getAdapter
 */
sealed class TimelineDelegate {

    fun onCreateViewHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ): RecyclerView.ViewHolder = getHolder(getType(), parent, onTouchHelper, viewModel)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: TimelineObject) {
        holder as TimelineViewHolder
        holder.bind(item)
    }

    /**
     * @return [TimelineObject.Type]
     */
    abstract fun getType(): TimelineObject.Type


    /**==========================  ALL ADAPTERS ==========================*/


    /**
     *
     *  TimelineObject: [TimelineObject.Type.IMAGE_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_picture_small]
     *
     *  ViewHolder: [TimelineViewHolder.ImageHolder]
     *
     */
    private object ImageAdapter : TimelineDelegate() {
        override fun getType() = TimelineObject.Type.IMAGE_ITEM
    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.RECORD_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_recorded_activity]
     *
     *  ViewHolder: [TimelineViewHolder.RecordedActivityHolder]
     *
     */
    private object RecordAdapter : TimelineDelegate() {
        override fun getType() = TimelineObject.Type.RECORD_ITEM
    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.BIG_IMAGE_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_picture_big]
     *
     *  ViewHolder: [TimelineViewHolder.BigImageHolder]
     *
     */
    private object BigImageAdapter : TimelineDelegate() {
        override fun getType() = TimelineObject.Type.BIG_IMAGE_ITEM

    }

    companion object {

        /**
         * @return a Adapter matching the [TimelineObject.type]
         *
         * @sample getHolder
         */
        fun getAdapter(type: TimelineObject.Type) =
            when (type) {
                TimelineObject.Type.IMAGE_ITEM -> ImageAdapter
                TimelineObject.Type.RECORD_ITEM -> RecordAdapter
                TimelineObject.Type.BIG_IMAGE_ITEM -> BigImageAdapter
            }
    }
}