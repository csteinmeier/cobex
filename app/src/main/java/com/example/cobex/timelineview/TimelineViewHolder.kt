package com.example.cobex.timelineview

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.CaptureAction
import com.example.cobex.Extensions.toLayout
import com.example.cobex.R
import com.example.cobex.ViewHelper

import kotlinx.android.synthetic.main.timeline_item_picture_big.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_small.view.*
import kotlinx.android.synthetic.main.timeline_item_recorded_activity.view.*


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
abstract class TimelineViewHolder(
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
                timeline_picture_small_time, item.createdTimeAsString, context
            )
            ViewHelper.ImageViewBitmap(timeline_picture_small_imageView, item.imgSrc, context)
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
            val detectedActivity = CaptureAction.Activities.values()
                .find { it.name == item.detectedActivity }

            ViewHelper.TextFieldSimpleTime(
                timeline_recorded_activity_time, item.createdTimeAsString, context
            )

            ViewHelper.ImageViewDrawable(
                timeline_recorded_activity_icon, detectedActivity!!.activityIcon, context
            )

            ViewHelper.TextFieldResourceString(
                timeline_recorded_activity_as_string,
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
        TimelineObject.Type.BIG_IMAGE_ITEM,
        parent,
        onTouchHelper,
        viewModel
    ) {
        override fun bind(item: TimelineObject): Unit = with(itemView) {
            item as TimelineObject.BigImageItem

            ViewHelper.ImageViewBitmap(
                itemView.timeline_picture_big_image, item.imgSrc, context
            )
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            viewModel.adapter.shrinkImage(adapterPosition)
            return true
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
            TimelineObject.Type.IMAGE_ITEM      -> ImageHolder(parent, onTouchHelper, viewModel)
            TimelineObject.Type.RECORD_ITEM     -> RecordedActivityHolder( parent, onTouchHelper, viewModel)
            TimelineObject.Type.BIG_IMAGE_ITEM  -> BigImageHolder(parent, onTouchHelper, viewModel)
        }
    }
}
