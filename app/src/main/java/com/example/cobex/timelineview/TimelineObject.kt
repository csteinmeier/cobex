package com.example.cobex.timelineview

import com.example.cobex.R
import com.example.cobex.CompositionArtifact
import java.time.format.DateTimeFormatter
import com.example.cobex.CaptureAction

/**
 * Data classes for each Layout and its Views
 * Also used to Link the Adapter with their matching Holder
 */
sealed class TimelineObject(
    val id: String,
    val type: Type,
    var createdTimeAsString: String,
    val pos: Int?
) {

    /**
     * Data Class for [R.layout.timeline_item_picture_small]
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param imgSrc Uri saved as String
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
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
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param detectedActivity saved Enum name of [CaptureAction.Activities.name]
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
     */
    class RecordItem(
        id: String,
        createdTimeAsString: String,
        val detectedActivity: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.RECORD_ITEM, createdTimeAsString, pos)

    /**
     * Data Class for [R.layout.timeline_item_picture_big]
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param imgSrc Uri saved as String
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
     */
    class BigImageItem(
        id: String,
        createdTimeAsString: String,
        val imgSrc: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.BIG_IMAGE_ITEM, createdTimeAsString, pos)

    /**
     * Used for linking between the individual components of the TimelineViews
     */
    enum class Type(val layout: Int) {

        /**
         * @see [ImageItem]
         */
        IMAGE_ITEM(R.layout.timeline_item_picture_small),

        /**
         * @see [RecordItem]
         */
        RECORD_ITEM(R.layout.timeline_item_recorded_activity),

        /**
         * @see [BigImageItem]
         */
        BIG_IMAGE_ITEM(R.layout.timeline_item_picture_big)
    }


}