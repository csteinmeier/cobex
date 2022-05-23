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
    abstract fun getType(): TimelineItemType


    /**==========================  ALL ADAPTERS ==========================*/


    /**
     *
     *  TimelineObject: [TimelineObject.Type.CAPTURE_PICTURE_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_picture_small]
     *
     *  ViewHolder: [TimelineViewHolder.CaptureImageHolder]
     *
     */
    private object CapturePictureAdapter : TimelineDelegate() {
        override fun getType() = TimelineItemType.CAPTURE_PICTURE_ITEM
    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.CAPTURE_ACTION_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_recorded_activity]
     *
     *  ViewHolder: [TimelineViewHolder.CaptureActionHolder]
     *
     */
    private object CaptureActionAdapter : TimelineDelegate() {
        override fun getType() = TimelineItemType.CAPTURE_ACTION_ITEM
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
        override fun getType() = TimelineItemType.BIG_IMAGE_ITEM

    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.CAPTURE_SOUND_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_capture_sound]
     *
     *  ViewHolder: [TimelineViewHolder.CaptureSoundHolder]
     *
     */
    private object CaptureSoundAdapter : TimelineDelegate() {
        override fun getType() = TimelineItemType.CAPTURE_SOUND_ITEM

    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.INPUT_MELODY_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_input_melody]
     *
     *  ViewHolder: [TimelineViewHolder.InputMelodyHolder]
     *
     */
    private object InputMelodyAdapter : TimelineDelegate() {
        override fun getType() = TimelineItemType.INPUT_MELODY_ITEM

    }

    /**
     *
     *  TimelineObject: [TimelineObject.Type.INPUT_KEYWORD_ITEM]
     *
     *  Adapter of Layout [R.layout.timeline_item_keyword]
     *
     *  ViewHolder: [TimelineViewHolder.KeywordHolder]
     *
     */
    private object InputKeywordAdapter : TimelineDelegate() {
        override fun getType() = TimelineItemType.INPUT_KEYWORD_ITEM

    }

    companion object {

        /**
         * @return a Adapter matching the [TimelineObject.type]
         *
         * @sample getHolder
         */
        fun getAdapter(type: TimelineObject.Type) =
            when (type) {
                TimelineItemType.CAPTURE_PICTURE_ITEM -> CapturePictureAdapter
                TimelineItemType.CAPTURE_ACTION_ITEM -> CaptureActionAdapter
                TimelineItemType.BIG_IMAGE_ITEM -> BigImageAdapter
                TimelineItemType.CAPTURE_SOUND_ITEM -> CaptureSoundAdapter
                TimelineItemType.INPUT_MELODY_ITEM -> InputMelodyAdapter
                TimelineItemType.INPUT_KEYWORD_ITEM -> InputKeywordAdapter
            }
    }
}