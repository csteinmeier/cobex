package com.example.cobex

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.Extensions.toImage
import com.example.cobex.Extensions.toLayout
import com.example.cobex.TimeLineView.MHolder.Companion.getHolder
import com.example.cobex.databinding.FragmentTimelineViewBinding
import kotlinx.android.synthetic.main.fragment_timeline_view.*
import kotlinx.android.synthetic.main.timeline_item_line.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_small.view.*
import kotlinx.android.synthetic.main.timeline_item_recorded_activity.view.*


class TimeLineView : Fragment(), CompositionArtifact.IArtifact {

    private var _binding: FragmentTimelineViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timelineLineStartText.text = getStartText(requireContext())

        TimeLineManager(requireContext(), recyclerTimelineViewLeft)
    }

    private fun getStartText(context: Context): String {
        val projectStartString = context.getString(R.string.timelineStart)

        val timeStarted = getProjectStartTime(context)!!
        val timeStartedString = TimeHelper.fromCreatedTillNowEasyString(context, timeStarted)
        return "$projectStartString $timeStartedString"
    }

    private class TimeLineManager(
        val context: Context,
        recyclerView: RecyclerView,
    ) : CompositionArtifact.IArtifact {

        var adapter: TimelineAdapter

        val listOfItems = mutableListOf<TimelineObject>()

        val pictureList: List<TimelineObject.ImageItem>? = pictureSetToTimelineItems()

        val recordItem: List<TimelineObject.RecordItem>? = recordedActivitiesToTimelineItems()

        init {
            listOfItems.apply {

                if (recordItem != null)
                    addAll(recordItem)


                if (pictureList != null)
                    addAll(pictureList)

                sortBy { it.createdTimeAsString }
            }

            adapter = TimelineAdapter(listOfItems)
            val itemTouchHelper = ItemMoveAndSwipeHelper(context, adapter)
            val touchHelper = ItemTouchHelper(itemTouchHelper)
            adapter.itemTouchHelper = touchHelper
            touchHelper.attachToRecyclerView(recyclerView)

            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

        }

        private fun pictureSetToTimelineItems(): List<TimelineObject.ImageItem>? =
            getStringSet(context, CapturePicture::class.java)
                ?.map { pictureToTimelineItem(it.substringAfter("app_images/"), it) }

        private fun pictureToTimelineItem(timeAsString: String, imgSrc: String) =
            TimelineObject.ImageItem(timeAsString = timeAsString, imgSrc = imgSrc)

        private fun recordedActivitiesToTimelineItems(): List<TimelineObject.RecordItem>? =
            getStringSet(context, CaptureAction.ActivityTransitionReceiver::class.java)
                ?.map {
                    recordedActivityToTimelineItem(
                        timeAsString = it.substringBeforeLast(":"),
                        detectedActivity = it.substringAfterLast(":")
                    )
                }

        private fun recordedActivityToTimelineItem(timeAsString: String, detectedActivity: String) =
            TimelineObject.RecordItem(timeAsString, detectedActivity)
    }


    interface ViewTypeDelegateAdapter {
        fun onCreateViewHolder(
            parent: ViewGroup,
            onTouchHelper: ItemTouchHelper
        ): RecyclerView.ViewHolder

        fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: TimelineObject)
    }

    private class TimelineAdapter(
        val timelineItems: MutableList<TimelineObject>,
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        CompositionArtifact.IArtifact,
        ItemMoveAndSwipeHelperAdapter {

        var itemTouchHelper: ItemTouchHelper? = null
        val timelineObjects = TimelineObject.Type.values()

        var adapter: TimelineDelegate? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val timelineObject = timelineObjects[viewType]
            adapter = TimelineDelegate.getAdapter(timelineObject)
            return adapter!!.onCreateViewHolder(parent, itemTouchHelper!!)
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
            timelineItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    abstract class ViewHolderOnTouchListener(
        itemView: View?, private val touchHelper: ItemTouchHelper
    ) :
        RecyclerView.ViewHolder(itemView!!),
        View.OnTouchListener,
        GestureDetector.OnGestureListener {
        lateinit var gestureDetector: GestureDetector

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
        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = false
        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float) = true

        override fun onLongPress(event: MotionEvent?) {
            touchHelper.startDrag(this)
        }

        abstract fun bind(item: TimelineObject)

    }


    sealed class ViewWrapper {

        class SimpleTextField(val textView: TextView, val string: String) : ViewWrapper()

        class TextFieldResourceString(val textView: TextView, resource: Int, context: Context) :
            ViewWrapper() {
            val string = context.getString(resource)
        }

        class ImageViewDrawable(val imageView: ImageView, resource: Int, context: Context) :
            ViewWrapper() {
            val drawable = ContextCompat.getDrawable(context, resource)
        }

        class ImageViewBitmap(val imageView: ImageView, pathToBitmap: String, context: Context) :
            ViewWrapper() {
            val bitmap = pathToBitmap.toImage(context)
        }

        class TextFieldSimpleTime(val textView: TextView, string: String, context: Context) :
            ViewWrapper() {
            val formattedString = TimeHelper.fromCreatedTillNowEasyString(context, string)
        }
    }


    sealed class MHolder(
        type: TimelineObject.Type, val parent: ViewGroup, onTouchHelper: ItemTouchHelper
    ) : ViewHolderOnTouchListener(type.layout.toLayout(parent), onTouchHelper) {

        override fun bind(item: TimelineObject) {
            itemWithResource(item).forEach {
                Log.i("Item", it.toString())
                when (it) {
                    is ViewWrapper.SimpleTextField -> it.textView.text = it.string
                    is ViewWrapper.TextFieldSimpleTime -> it.textView.text = it.formattedString
                    is ViewWrapper.ImageViewBitmap -> it.imageView.setImageBitmap(it.bitmap)
                    is ViewWrapper.ImageViewDrawable -> it.imageView.setImageDrawable(it.drawable)
                    is ViewWrapper.TextFieldResourceString -> it.textView.text = it.string
                }
            }
        }

        abstract fun itemWithResource(item: TimelineObject): List<ViewWrapper>

        class LineHolder(
            type: TimelineObject.Type,
            parent: ViewGroup,
            onTouchHelper: ItemTouchHelper
        ) : MHolder(type, parent, onTouchHelper) {
            override fun itemWithResource(item: TimelineObject) = with(itemView) {
                item as TimelineObject.LineItemText
                listOf(
                    ViewWrapper.SimpleTextField(timeline_line_text, item.text!!)
                )
            }



        }

        class ImageHolder(
            type: TimelineObject.Type,
            parent: ViewGroup,
            onTouchHelper: ItemTouchHelper
        ) : MHolder(type, parent, onTouchHelper) {
            override fun itemWithResource(item: TimelineObject) = with(itemView) {
                item as TimelineObject.ImageItem

                listOf(
                    ViewWrapper
                        .TextFieldSimpleTime(
                            timeline_picture_small_time,
                            item.timeAsString,
                            context
                        ),
                    ViewWrapper
                        .ImageViewBitmap(timeline_picture_small_imageView, item.imgSrc, context)
                )
            }
        }

        class RecordedActivityHolder(
            type: TimelineObject.Type,
            parent: ViewGroup,
            onTouchHelper: ItemTouchHelper
        ) : MHolder(type, parent, onTouchHelper) {

            override fun itemWithResource(item: TimelineObject) = with(itemView) {
                val detectedActivity = CaptureAction.Activities.values()
                    .find { it.name == (item as TimelineObject.RecordItem).detectedActivity }!!

                item as TimelineObject.RecordItem

                listOf(
                    ViewWrapper
                        .TextFieldSimpleTime(
                            timeline_recorded_activity_time,
                            item.timeAsString,
                            context
                        ),
                    ViewWrapper
                        .ImageViewDrawable(
                            timeline_recorded_activity_icon,
                            detectedActivity.activityIcon,
                            context
                        ),
                    ViewWrapper
                        .TextFieldResourceString(
                            timeline_recorded_activity_as_string,
                            detectedActivity.activityRecognitionString,
                            context
                        )
                )
            }
        }

        companion object {
            fun getHolder(
                type: TimelineObject.Type,
                parent: ViewGroup,
                onTouchHelper: ItemTouchHelper
            ) =
                when (type) {
                    TimelineObject.Type.LINE_ITEM -> LineHolder(type, parent, onTouchHelper)
                    TimelineObject.Type.IMAGE_ITEM -> ImageHolder(
                        type,
                        parent,
                        onTouchHelper
                    )
                    TimelineObject.Type.RECORD_ITEM -> RecordedActivityHolder(
                        type,
                        parent,
                        onTouchHelper
                    )
                }
        }
    }

    sealed class TimelineDelegate : ViewTypeDelegateAdapter {

        override fun onCreateViewHolder(parent: ViewGroup, onTouchHelper: ItemTouchHelper)
                : RecyclerView.ViewHolder = getHolder(getType(), parent, onTouchHelper)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: TimelineObject) {
            holder as ViewHolderOnTouchListener
            holder.bind(item)
        }

        abstract fun getType(): TimelineObject.Type


        /**==========================  ALL ADAPTERS ==========================*/

        object LineAdapter : TimelineDelegate() {
            override fun getType() = TimelineObject.Type.LINE_ITEM
        }

        object ImageAdapter : TimelineDelegate() {
            override fun getType() = TimelineObject.Type.IMAGE_ITEM
        }

        object RecordAdapter : TimelineDelegate() {
            override fun getType() = TimelineObject.Type.RECORD_ITEM

        }

        companion object {
            fun getAdapter(type: TimelineObject.Type) =
                when (type) {
                    TimelineObject.Type.LINE_ITEM -> LineAdapter
                    TimelineObject.Type.IMAGE_ITEM -> ImageAdapter
                    TimelineObject.Type.RECORD_ITEM -> RecordAdapter
                }
        }
    }


    sealed class TimelineObject(
        val type: Type,
        var createdTimeAsString: String? = null
    ) {

        data class LineItemText(
            val text: String? = null,
        ) : TimelineObject(Type.LINE_ITEM)

        data class ImageItem(
            val timeAsString: String,
            val imgSrc: String
        ) : TimelineObject(Type.IMAGE_ITEM, timeAsString)

        data class RecordItem(
            val timeAsString: String,
            val detectedActivity: String,
        ) : TimelineObject(Type.RECORD_ITEM, timeAsString)


        enum class Type(val layout: Int) {
            LINE_ITEM(R.layout.timeline_item_line),
            IMAGE_ITEM(R.layout.timeline_item_picture_small),
            RECORD_ITEM(R.layout.timeline_item_recorded_activity)
        }
    }

    private interface ItemMoveAndSwipeHelperAdapter {
        fun onItemMove(initialPosition: Int, endPosition: Int)
        fun onItemSwiped(position: Int)
    }

    private class ItemMoveAndSwipeHelper(
        val context: Context,
        val moveAndSwipeHelperAdapter: ItemMoveAndSwipeHelperAdapter

    ) : ItemTouchHelper.Callback() {

        private fun cardList() = MartialCards.getListOfCards(context)
        private fun getCard(item: View?) = cardList().single { it.isCardActive(item) }

        override fun isLongPressDragEnabled() = false
        override fun isItemViewSwipeEnabled() = true

        override fun clearView(recycler: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recycler, viewHolder)
            resetColorMartialView(viewHolder.itemView)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                setGreyMartialView(viewHolder?.itemView!!)
            }
        }

        private fun resetColorMartialView(item: View){
            val card = getCard(item)
            setColour(card.getConcreteCard(item)!!, card.color)
        }

        private fun setGreyMartialView(item : View)  {
            setColour(getCard(item).getConcreteCard(item)!!, Color.LTGRAY)
        }

        private fun setColour(view: View, color: Int) {
            view.setBackgroundColor(color)
        }

        override fun getMovementFlags(
            recycler: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recycler: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            moveAndSwipeHelperAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            moveAndSwipeHelperAdapter.onItemSwiped(viewHolder.adapterPosition)
        }
    }

    private sealed class MartialCards(val color: Int){

        abstract fun getConcreteCard(item: View?) : View?

        abstract fun isCardActive(item: View?) : Boolean

        class RecordedActivity(context: Context)
            : MartialCards(context.getColor(android.R.color.holo_blue_dark)){

            override fun getConcreteCard(item: View?): View? =
                with(item){ this?.timeline_martial_view_record_activity }

            override fun isCardActive(item: View?): Boolean  =
                with(item){ this?.timeline_martial_view_record_activity } != null

        }

        class Image(context: Context)
            :MartialCards(context.getColor(android.R.color.holo_orange_light)){

            override fun getConcreteCard(item: View?): View? =
                with(item){ this?.timeline_martial_view_picture_small }

            override fun isCardActive(item: View?): Boolean =
                with(item){ this?.timeline_martial_view_picture_small } != null

        }

        companion object{
            fun getListOfCards(context: Context) = listOf(
                RecordedActivity(context),
                Image(context)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}