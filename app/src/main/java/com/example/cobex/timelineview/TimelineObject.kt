package com.example.cobex.timelineview

import com.example.cobex.R

/**
 * Data classes for each Layout and its Views
 * Also used to Link the Adapter with their matching Holder
 */
abstract class TimelineObject(
    val id: String,
    val type: Type,
    var createdTimeAsString: String,
    val pos: Int?
) {

    /**
     * Data Class for [R.layout.timeline_item_picture_small]
     */
    class ImageItem(
        id: String,
        createdTimeAsString: String,
        val imgSrc: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.IMAGE_ITEM, createdTimeAsString, pos)

    /**
     * Data Class for [R.layout.timeline_item_recorded_activity]
     */
    class RecordItem(
        mID: String,
        createdTimeAsString: String,
        val detectedActivity: String,
        pos: Int? = -1
    ) :
        TimelineObject(mID, Type.RECORD_ITEM, createdTimeAsString, pos)

    /**
     * Data Class for [R.layout.timeline_item_picture_big]
     */
    class BigImage(
        mID: String,
        createdTimeAsString: String,
        val imgSrc: String,
        pos: Int? = -1
    ) :
        TimelineObject(mID, Type.BIG_IMAGE_ITEM, createdTimeAsString, pos)

    enum class Type(val layout: Int) {
        IMAGE_ITEM(R.layout.timeline_item_picture_small),
        RECORD_ITEM(R.layout.timeline_item_recorded_activity),
        BIG_IMAGE_ITEM(R.layout.timeline_item_picture_big)
    }


}