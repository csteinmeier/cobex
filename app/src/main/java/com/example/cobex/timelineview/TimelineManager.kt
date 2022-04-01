package com.example.cobex.timelineview

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.CompositionArtifact

/**
 *
 */
class TimelineManager(
    val context: Context,
    recyclerView: RecyclerView
) : CompositionArtifact.IArtifact {

    private val model = Model()

    var adapter: TimelineAdapterTimeline = TimelineAdapterTimeline(
        model.getList(TimelineStateType.getArtefactType()).toMutableList(),
        this
    )

    init {

        val itemTouchHelper = TimelineItemHelper(context, adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelper)
        adapter.itemTouchHelper = touchHelper
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun updateStateChange() {
        saveOrderedItems()
        TimelineStateType.changeState()
        val artType = TimelineStateType.getArtefactType()
        adapter.timelineItems = model.getList(artType).toMutableList()
        adapter.notifyDataSetChanged()
    }

    fun saveOrderedItems() {
        if (TimelineStateType.actualTimelineState == TimelineStateType.CompositionArtifacts) {
            putStringSet(
                context,
                this.javaClass,
                adapter.timelineItems.mapIndexed() { index, timelineObject ->
                    "${timelineObject.type}!" +
                            timelineObject.id +
                            "!$index"
                }.toSet()
            )
        }
    }


    fun exchangeItem(item: TimelineObject) {
        val cList = currentArtefactList(item.type)!!
        Log.e("Current List before change", cList.size.toString())
        cList.remove(cList.find { item.id == it })
        putArtefactList(item.type, TimelineStateType.getArtefactType(), cList.toSet())
        Log.e("Current List after change", cList.size.toString())

        val oList = oppositeArtefactList(item.type)!!
        Log.e("Opposite List before change", oList.size.toString())
        oList.add(item.id)
        putArtefactList(item.type, TimelineStateType.getOppositeArtefactType(), oList.toSet())
        Log.e("Opposite List after change", oList.size.toString())
    }


    private fun currentArtefactList(type: TimelineObject.Type) =
        getArtefactList(type, TimelineStateType.getArtefactType())

    private fun oppositeArtefactList(type: TimelineObject.Type) =
        getArtefactList(type, TimelineStateType.getOppositeArtefactType())

    private fun getArtefactList(type: TimelineObject.Type, timelineStateType: TimelineStateType) =
        when (type) {

            TimelineObject.Type.IMAGE_ITEM ->
                timelineStateType.getCapturedImagesStringSet(context)?.toMutableList()

            TimelineObject.Type.RECORD_ITEM ->
                timelineStateType.getRecordedActivitiesStringSet(context)?.toMutableList()

            TimelineObject.Type.BIG_IMAGE_ITEM ->
                timelineStateType.getCapturedImagesStringSet(context)?.toMutableList()
        }

    private fun putArtefactList(
        type: TimelineObject.Type,
        timelineStateType: TimelineStateType,
        set: Set<String>
    ) =
        when (type) {

            TimelineObject.Type.IMAGE_ITEM ->
                timelineStateType.putCapturedImageStringSet(context, set)

            TimelineObject.Type.RECORD_ITEM ->
                timelineStateType.putRecordedActivitiesStringSet(context, set)

            TimelineObject.Type.BIG_IMAGE_ITEM ->
                timelineStateType.putCapturedImageStringSet(context, set)
        }


    private inner class Model {

        fun getList(timelineStateType: TimelineStateType): List<TimelineObject> {
            val pictureList =
                timelineStateType.getCapturedImagesStringSet(context)
                    ?.let { pictureSetToTimelineItems(it) }

            val recordedActivities =
                timelineStateType.getRecordedActivitiesStringSet(context)
                    ?.let { recordedActivitiesToTimelineItems(it) }

            val all = mutableListOf<TimelineObject>()

            all.apply {
                if (pictureList != null)
                    addAll(pictureList)
                if (recordedActivities != null)
                    addAll(recordedActivities)

                sortBy { it.createdTimeAsString }
            }

            val savedOrderedList = getStringSet(context, TimelineManager::class.java)
            var oldList = mutableListOf<TimelineObject>()
            if (savedOrderedList?.isEmpty() != true) {
                if (savedOrderedList != null) {
                    oldList = createOldList(savedOrderedList)
                }
            }

            if (oldList.size == all.size)
                return oldList

            return all
        }

        private fun createOldList(set: Set<String>): MutableList<TimelineObject> {
            val list = mutableListOf<TimelineObject>()
            set.forEach {
                val regularString = it
                    .substringAfter('!')
                    .substringBeforeLast('!')

                when (it.substringBefore('!')) {
                    TimelineObject.Type.IMAGE_ITEM.name ->
                        list.add(
                            pictureToTimelineItem(
                                id = regularString,
                                timeAsString = regularString.substringAfter("app_images/"),
                                imgSrc = regularString,
                                position = it.substringAfterLast('!').toInt()
                            )
                        )

                    TimelineObject.Type.BIG_IMAGE_ITEM.name ->
                        list.add(
                            pictureToTimelineItem(
                                id = regularString,
                                timeAsString = regularString.substringAfter("app_images/"),
                                imgSrc = regularString,
                                position = it.substringAfterLast('!').toInt()
                            )
                        )

                    TimelineObject.Type.RECORD_ITEM.name ->
                        list.add(
                            recordedActivityToTimelineItem(
                                id = regularString,
                                timeAsString = regularString.substringBeforeLast(":"),
                                detectedActivity = regularString.substringAfterLast(":"),
                                position = it.substringAfterLast('!').toInt()
                            )
                        )
                }
            }

            list.sortBy { it.pos }

            return list
        }


        private fun pictureToTimelineItem(id: String, timeAsString: String, imgSrc: String) =
            TimelineObject.ImageItem(
                id = id,
                createdTimeAsString = timeAsString,
                imgSrc = imgSrc
            )

        private fun pictureToTimelineItem(
            id: String,
            timeAsString: String,
            imgSrc: String,
            position: Int
        ) =
            TimelineObject.ImageItem(
                id = id,
                createdTimeAsString = timeAsString,
                imgSrc = imgSrc,
                pos = position
            )


        private fun pictureSetToTimelineItems(stringSet: Set<String>)
                : List<TimelineObject.ImageItem> = stringSet
            .map {
                pictureToTimelineItem(
                    id = it,
                    timeAsString = it.substringAfter("app_images/"),
                    imgSrc = it
                )
            }


        private fun recordedActivitiesToTimelineItems(stringSet: Set<String>)
                : List<TimelineObject.RecordItem> = stringSet
            .map {
                recordedActivityToTimelineItem(
                    id = it,
                    timeAsString = it.substringBeforeLast(":"),
                    detectedActivity = it.substringAfterLast(":")
                )
            }

        private fun recordedActivityToTimelineItem(
            id: String,
            timeAsString: String,
            detectedActivity: String
        ) =
            TimelineObject.RecordItem(
                mID = id,
                createdTimeAsString = timeAsString,
                detectedActivity = detectedActivity
            )

        private fun recordedActivityToTimelineItem(
            id: String,
            timeAsString: String,
            detectedActivity: String,
            position: Int
        ) =
            TimelineObject.RecordItem(
                mID = id,
                createdTimeAsString = timeAsString,
                detectedActivity = detectedActivity,
                pos = position
            )

    }
}