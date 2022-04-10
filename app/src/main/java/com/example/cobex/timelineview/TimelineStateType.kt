package com.example.cobex.timelineview

import android.content.Context
import com.example.cobex.*
import com.example.cobex.timelineview.TimelineStateType.Companion.actualTimelineState
import com.example.cobex.timelineview.TimelineStateType.Companion.changeState
import com.example.cobex.timelineview.TimelineStateType.Companion.getOppositeArtefactStateType

/**
 * Class which is used to obtain the correct artifacts.
 * At the moment: Stored and the real Composition
 *
 * Contains 3 static methods and 1 variable
 *
 * * [actualTimelineState] simply shows which artifact is shown in the TimelineView
 *
 * * [getOppositeArtefactStateType] Opposite of the [actualTimelineState]
 *
 * * [changeState] will set the [actualTimelineState] to the opposite
 *
 */
sealed class TimelineStateType : CompositionArtifact.IArtifact {

    abstract fun getStringSet(context: Context, type: TimelineItemType): Set<String>?

    abstract fun putStringSet(context: Context, type: TimelineItemType, set: Set<String>)


    object CompositionArtifacts : TimelineStateType() {
        override fun getStringSet(context: Context, type: TimelineItemType): Set<String>? =
            when (type) {
                TimelineItemType.KEYWORD -> getStringSet(context, InputKeyword::class.java)
                TimelineItemType.CAPTURE_SOUND -> getStringSet(context, CaptureSound::class.java)
                TimelineItemType.BIG_IMAGE_ITEM -> getStringSet(context, CapturePicture::class.java)
                TimelineItemType.IMAGE_ITEM -> getStringSet(context, CapturePicture::class.java)
                TimelineItemType.RECORD_ITEM -> getStringSet(context, CaptureAction::class.java)
                TimelineItemType.INPUT_MELODY -> getStringSet(context, InputMelody::class.java)
            }

        override fun putStringSet(context: Context, type: TimelineItemType, set: Set<String>) =
            when(type){
                TimelineItemType.KEYWORD -> putStringSet(context, InputKeyword::class.java, set)
                TimelineItemType.CAPTURE_SOUND -> putStringSet(context, CaptureSound::class.java, set)
                TimelineItemType.BIG_IMAGE_ITEM -> putStringSet(context, CapturePicture::class.java, set)
                TimelineItemType.IMAGE_ITEM -> putStringSet(context, CapturePicture::class.java, set)
                TimelineItemType.RECORD_ITEM -> putStringSet(context, CaptureAction::class.java, set)
                TimelineItemType.INPUT_MELODY -> putStringSet(context, InputMelody::class.java, set)
            }
    }

    object StoredArtifacts : TimelineStateType() {
        override fun getStringSet(context: Context, type: TimelineObject.Type): Set<String>? =
            when (type) {
                TimelineItemType.KEYWORD -> getStringSet(context, TimelineObject.KeywordItem::class.java)
                TimelineItemType.CAPTURE_SOUND -> getStringSet(context, TimelineObject.CaptureSoundItem::class.java)
                TimelineItemType.BIG_IMAGE_ITEM -> getStringSet(context, TimelineObject.BigImageItem::class.java)
                TimelineItemType.IMAGE_ITEM -> getStringSet(context, TimelineObject.ImageItem::class.java)
                TimelineItemType.RECORD_ITEM -> getStringSet(context, TimelineObject.RecordItem::class.java)
                TimelineItemType.INPUT_MELODY -> getStringSet(context, TimelineObject.InputMelodyItem::class.java)
            }

        override fun putStringSet(context: Context, type: TimelineObject.Type, set: Set<String>) =
            when(type){
                TimelineItemType.KEYWORD -> putStringSet(context, TimelineObject.KeywordItem::class.java, set)
                TimelineItemType.CAPTURE_SOUND -> putStringSet(context, TimelineObject.CaptureSoundItem::class.java, set)
                TimelineItemType.BIG_IMAGE_ITEM -> putStringSet(context, TimelineObject.BigImageItem::class.java, set)
                TimelineItemType.IMAGE_ITEM -> putStringSet(context, TimelineObject.ImageItem::class.java, set)
                TimelineItemType.RECORD_ITEM -> putStringSet(context, TimelineObject.RecordItem::class.java, set)
                TimelineItemType.INPUT_MELODY -> putStringSet(context, TimelineObject.InputMelodyItem::class.java, set)
            }
    }

    companion object {

        var actualTimelineState: TimelineStateType = CompositionArtifacts

        /**
         * @sample getOppositeArtefactStateType
         */
        fun getOppositeArtefactStateType() = when (actualTimelineState) {
            CompositionArtifacts -> StoredArtifacts
            StoredArtifacts -> CompositionArtifacts
        }

        /**
         * @sample changeState
         */
        fun changeState() = when (actualTimelineState) {
            CompositionArtifacts -> actualTimelineState = StoredArtifacts
            StoredArtifacts -> actualTimelineState = CompositionArtifacts
        }
    }
}