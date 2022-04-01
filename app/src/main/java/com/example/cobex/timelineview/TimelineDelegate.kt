package com.example.cobex.timelineview

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.timelineview.TimelineViewHolder.Companion.getHolder

/**
 *
 * ## Parent class of all [RecyclerView.Adapter] of the TimeLineAdapters.
 * * Unlike normal [RecyclerView.Adapter] these have not an inner class of their holder!
 *
 * ### These Adapters should be and are linked to each other via their matching [TimelineObject].
 *
 *+ This class provides the static method [getAdapter].
 *This function returns the correct holder based on the [TimelineObject.type].<br />
 * *Like the function in [ViewHolderOnTouchListener.getHolder]*
 *
 *
 * @sample getHolder
 */
abstract class TimelineDelegate {

    fun onCreateViewHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        manager: TimelineManager
    ): RecyclerView.ViewHolder = getHolder(getType(), parent, onTouchHelper, manager)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: TimelineObject) {
        holder as TimelineViewHolder
        holder.bind(item)
    }

    abstract fun getType(): TimelineObject.Type


    /**==========================  ALL ADAPTERS ==========================*/


    /**
     * Adapter of [ImageAdapter]
     */
    private object ImageAdapter : TimelineDelegate() {
        override fun getType() = TimelineObject.Type.IMAGE_ITEM
    }

    /**
     *  Adapter of [RecordAdapter]
     */
    private object RecordAdapter : TimelineDelegate() {
        override fun getType() = TimelineObject.Type.RECORD_ITEM
    }

    /**
     *  Adapter of [BigImageAdapter]
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