package com.example.cobex.timelineview

import android.app.Activity
import android.media.MediaPlayer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.Extensions.prepareProgressWithSound
import com.example.cobex.Extensions.toLayout
import com.example.cobex.R
import com.example.cobex.ViewHelper
import com.example.cobex.capture_action.Activities
import com.example.cobex.timelineview.TimelineViewHolder.Companion.getHolder
import kotlinx.android.synthetic.main.timeline_item_capture_sound.view.*
import kotlinx.android.synthetic.main.timeline_item_input_melody.view.*
import kotlinx.android.synthetic.main.timeline_item_keyword.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_big.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_small.view.*
import kotlinx.android.synthetic.main.timeline_item_recorded_activity.view.*
import java.util.*


/**
 *
 * ## Parent class of all [RecyclerView.ViewHolder] of the TimeLineAdapters.
 * * Unlike normal [RecyclerView.ViewHolder] these are not an inner class of their adapter!
 *
 * ### These holders should be and are linked to each other via their matching [TimelineObject].
 *
 * + This class provides the static method [getHolder].
 * This function returns the correct holder based on the [TimelineObject.type].
 * *Like the function in [TimelineDelegate.getAdapter]*
 *
 * @sample getHolder
 */
sealed class TimelineViewHolder(
    type: TimelineObject.Type,
    parent: ViewGroup,
    private val touchHelper: ItemTouchHelper,
    protected val viewModel: TimelineViewModel
) :
    RecyclerView.ViewHolder(type.layout.toLayout(parent)),
    View.OnTouchListener,
    GestureDetector.OnGestureListener {
    private lateinit var gestureDetector: GestureDetector

    init {
        invoke()
    }

    operator fun invoke() {
        gestureDetector = GestureDetector(itemView.context, this)
        itemView.setOnTouchListener(this)
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onDown(p0: MotionEvent?) = false
    override fun onShowPress(p0: MotionEvent?) = Unit

    //OnClickEvent
    override fun onSingleTapUp(p0: MotionEvent?) = false

    //Wish
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = false
    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = true

    override fun onLongPress(event: MotionEvent?) {
        touchHelper.startDrag(this)
    }

    /**
     *  @param item Dataclass of TimelineObject
     */
    abstract fun bind(item: TimelineObject)

    /**
     * Adapter: [TimelineDelegate.ImageAdapter]
     *
     * Layout: [R.layout.timeline_item_picture_small]
     *
     * TimelineObject: [TimelineObject.ImageItem]
     *
     */
    private class ImageHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineObject.Type.IMAGE_ITEM,
        parent,
        onTouchHelper,
        viewModel
    ) {

        override fun bind(item: TimelineObject): Unit = with(itemView) {
            item as TimelineObject.ImageItem

            ViewHelper.TextFieldSimpleTime(
                timeline_time_picture_small, item.createdTimeAsString, context
            )
            ViewHelper.ImageViewBitmap(timeline_image_view_picture_small, item.imgSrc, context)
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            viewModel.adapter.extendImage(adapterPosition)
            return true
        }
    }

    /**
     * Adapter: [TimelineDelegate.RecordAdapter]
     *
     * Layout: [R.layout.timeline_item_recorded_activity]
     *
     * TimelineObject: [TimelineObject.RecordItem]
     *
     *
     */
    private class RecordedActivityHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineObject.Type.RECORD_ITEM,
        parent,
        onTouchHelper,
        viewModel
    ) {

        override fun bind(item: TimelineObject): Unit = with(itemView) {
            item as TimelineObject.RecordItem
            val detectedActivity = Activities.values()
                .find { it.name == item.detectedActivity }

            ViewHelper.TextFieldSimpleTime(
                timeline_text_view_recorded_activity_time, item.createdTimeAsString, context
            )

            ViewHelper.ImageViewDrawable(
                timeline_icon_recorded_activity, detectedActivity!!.activityIcon, context
            )

            ViewHelper.TextFieldResourceString(
                timeline_text_view_recorded_activity_as_string,
                detectedActivity.activityRecognitionString, context
            )
        }
    }

    /**
     * Adapter: [TimelineDelegate.BigImageAdapter]
     *
     * Layout: [R.layout.timeline_item_picture_big]
     *
     * TimelineObject: [TimelineObject.BigImageItem]
     */
    private class BigImageHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineItemType.BIG_IMAGE_ITEM,
        parent,
        onTouchHelper,
        viewModel
    ) {
        override fun bind(item: TimelineObject): Unit = with(itemView) {
            item as TimelineObject.BigImageItem

            ViewHelper.ImageViewBitmap(
                itemView.timeline_imageView_picture_big, item.imgSrc, context
            )
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            viewModel.adapter.shrinkImage(adapterPosition)
            return true
        }
    }


    private class CaptureSoundHolder(
        private val parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineItemType.CAPTURE_SOUND,
        parent,
        onTouchHelper,
        viewModel
    ) {
        var recordRes : String? = null

        override fun bind(item: TimelineObject) : Unit = with(itemView) {
            item as TimelineObject.CaptureSoundItem

            recordRes = item.mRecord

            ViewHelper.TextFieldDuration(
                itemView.timeline_duration_capture_sound, item.mRecord, context
            )

            ViewHelper.TextFieldSimpleTime(
                itemView.timeline_time_capture_sound, item.createdTimeAsString, context
            )

            ViewHelper.ImageViewDrawable(
                itemView.timeline_imageView_capture_sound, item.musicType, context
            )
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            val mPlayer = MediaPlayer.create(parent.context, recordRes?.toUri())
            val progressBar = itemView.timeline_progressbar_capture_sound
            progressBar.prepareProgressWithSound(mPlayer)
            mPlayer.start()
            return true
        }
    }


    private class InputMelodyHolder(
        private val parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineItemType.INPUT_MELODY,
        parent,
        onTouchHelper,
        viewModel
    ) {

        var recordRes : String? = null

        override fun bind(item: TimelineObject) : Unit = with(itemView) {
            item as TimelineObject.InputMelodyItem

            recordRes = item.mRecord

            ViewHelper.TextFieldSimpleTime(
                itemView.timeline_time_input_melody, item.createdTimeAsString, context
            )

            ViewHelper.TextFieldDuration(
                itemView.timeline_duration_input_melody, item.mRecord, context
            )
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            val mPlayer = MediaPlayer.create(parent.context, recordRes?.toUri())
            val progressBar = itemView.timeline_progressbar_input_melody
            progressBar.prepareProgressWithSound(mPlayer)
            mPlayer.start()
            return true
        }

    }

    private class KeywordHolder(
        parent: ViewGroup,
        onTouchHelper: ItemTouchHelper,
        viewModel: TimelineViewModel
    ) : TimelineViewHolder(
        TimelineItemType.KEYWORD,
        parent,
        onTouchHelper,
        viewModel
    ) {
        override fun bind(item: TimelineObject) : Unit = with(itemView) {
            item as TimelineObject.KeywordItem

            ViewHelper.TextFieldSimpleTime(
                itemView.timeline_time_keyword, item.createdTimeAsString, context
            )

            ViewHelper.SimpleTextField(
                itemView.timeline_counter_keyword, item.keywordAmount.toString()
            )
        }
    }

    companion object {
        /**
         * @return a Holder matching the [TimelineObject.type]
         *
         * @sample getHolder
         */
        fun getHolder(
            type: TimelineObject.Type,
            parent: ViewGroup,
            onTouchHelper: ItemTouchHelper,
            viewModel: TimelineViewModel
        ) = when (type) {
            TimelineItemType.IMAGE_ITEM -> ImageHolder(parent, onTouchHelper, viewModel)
            TimelineItemType.RECORD_ITEM -> RecordedActivityHolder( parent, onTouchHelper, viewModel)
            TimelineItemType.BIG_IMAGE_ITEM  -> BigImageHolder(parent, onTouchHelper, viewModel)
            TimelineItemType.INPUT_MELODY -> InputMelodyHolder(parent, onTouchHelper, viewModel)
            TimelineItemType.CAPTURE_SOUND -> CaptureSoundHolder(parent, onTouchHelper, viewModel)
            TimelineItemType.KEYWORD -> KeywordHolder(parent, onTouchHelper, viewModel)
        }
    }
}
