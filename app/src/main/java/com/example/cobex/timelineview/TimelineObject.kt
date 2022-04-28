package com.example.cobex.timelineview

import com.example.cobex.CompositionArtifact
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import java.time.format.DateTimeFormatter

typealias TimelineItemType = TimelineObject.Type

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
    class CapturePictureItem(
        id: String,
        createdTimeAsString: String,
        val imgSrc: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.CAPTURE_PICTURE_ITEM, createdTimeAsString, pos)

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
    class CaptureActionItem(
        id: String,
        createdTimeAsString: String,
        val detectedActivity: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.CAPTURE_ACTION_ITEM, createdTimeAsString, pos)

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
     * Data Class for [R.layout.timeline_item_capture_sound]
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param mRecord Uri saved as String
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
     */
    class CaptureSoundItem(
        id: String,
        createdTimeAsString: String,
        val mRecord: String,
        val musicType: Int,
        pos: Int? = -1
    ) :
    TimelineObject(id, Type.CAPTURE_SOUND_ITEM, createdTimeAsString, pos)

    /**
     * Data Class for [R.layout.timeline_item_input_melody]
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param mRecord Uri saved as String
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
     */
    class InputMelodyItem(
        id: String,
        createdTimeAsString: String,
        val mRecord: String,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.INPUT_MELODY_ITEM, createdTimeAsString, pos)

    /**
     * Data Class for [R.layout.timeline_item_keyword]
     *
     * @param id simply the whole string stored in [CompositionArtifact]
     *
     * @param createdTimeAsString String in [DateTimeFormatter.ISO_INSTANT] form
     *
     * @param keywords as a long string
     *
     * @param pos *default = -1*, should not be set manually, will be set to its adapter position
     *
     */
    class InputKeywordItem(
        id: String,
        createdTimeAsString: String,
        val keywords: String,
        val keywordAmount: Int,
        pos: Int? = -1
    ) :
        TimelineObject(id, Type.INPUT_KEYWORD_ITEM, createdTimeAsString, pos)

    /**
     * Used for linking between the individual components of the TimelineViews
     */
    enum class Type(val layout: Int) {

        /**
         * @see [CapturePictureItem]
         */
        CAPTURE_PICTURE_ITEM(R.layout.timeline_item_picture_small),

        /**
         * @see [CaptureActionItem]
         */
        CAPTURE_ACTION_ITEM(R.layout.timeline_item_recorded_activity),

        /**
         * @see [BigImageItem]
         */
        BIG_IMAGE_ITEM(R.layout.timeline_item_picture_big),

        /**
         * @see [CaptureSoundItem]
         */
        CAPTURE_SOUND_ITEM(R.layout.timeline_item_capture_sound),

        /**
         * @see [InputMelodyItem]
         */
        INPUT_MELODY_ITEM(R.layout.timeline_item_input_melody),

        /**
         * @see [InputKeywordItem]
         */
        INPUT_KEYWORD_ITEM(R.layout.timeline_item_keyword)
    }

}
