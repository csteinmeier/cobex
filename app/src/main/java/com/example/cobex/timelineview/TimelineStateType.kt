package com.example.cobex.timelineview

import android.content.Context
import com.example.cobex.*

/**
 * Class which is used to obtain the correct artifacts.
 * At the moment: Stored and the real Composition
 *
 * Contains 3 static methods and 1 variable
 *
 * * [actualTimelineState] simply shows which artifact is shown in the TimelineView
 *
 * * [getOppositeArtefactType] Opposite of the [actualTimelineState]
 *
 * * [changeState] will set the [actualTimelineState] to the opposite
 *
 */
sealed class TimelineStateType : CompositionArtifact.IArtifact {

    /**
     * Will get a CapturedImageStringSet via [CompositionArtifact]
     */
    abstract fun getCapturedImagesStringSet(context: Context): Set<String>?

    /**
     * Will put a CapturedImageStringSet via [CompositionArtifact]
     */
    abstract fun putCapturedImageStringSet(context: Context, set: Set<String>)

    /**
     * Will get a RecordActivityStringSet via [CompositionArtifact]
     */
    abstract fun getRecordedActivitiesStringSet(context: Context): Set<String>?

    /**
     * Will put a RecordActivitySet via [CompositionArtifact]
     */
    abstract fun putRecordedActivitiesStringSet(context: Context, set: Set<String>)

    abstract fun getInputMelodiesStringSet(context: Context) : Set<String>?

    abstract fun putInputMelodiesStringSet(context: Context, set: Set<String>)

    abstract fun getCaptureSoundStringSet(context: Context) : Set<String>?

    abstract fun putCaptureSoundStringSet(context: Context, set: Set<String>)

    object CompositionArtifacts : TimelineStateType() {

        override fun getCapturedImagesStringSet(context: Context): Set<String>? =
            getStringSet(context, CapturePicture::class.java)

        override fun putCapturedImageStringSet(context: Context, set: Set<String>) {
            putStringSet(context, CapturePicture::class.java, set)
        }

        override fun getRecordedActivitiesStringSet(context: Context): Set<String>? =
            getStringSet(context, CaptureAction.ActivityTransitionReceiver::class.java)

        override fun putRecordedActivitiesStringSet(context: Context, set: Set<String>) {
            putStringSet(context, CaptureAction.ActivityTransitionReceiver::class.java, set)
        }

        override fun getInputMelodiesStringSet(context: Context) =
            getStringSet(context, InputMelody::class.java)


        override fun putInputMelodiesStringSet(context: Context, set: Set<String>) {
            putStringSet(context, InputMelody::class.java, set)
        }

        override fun getCaptureSoundStringSet(context: Context) =
            getStringSet(context, CaptureSound::class.java)


        override fun putCaptureSoundStringSet(context: Context, set: Set<String>) {
            putStringSet(context, CaptureSound::class.java, set)
        }


    }

    object StoredArtifacts : TimelineStateType() {

        override fun getCapturedImagesStringSet(context: Context): Set<String>? =
            getStringSet(context, TimelineObject.ImageItem::class.java)

        override fun putCapturedImageStringSet(context: Context, set: Set<String>) {
            putStringSet(context, TimelineObject.ImageItem::class.java, set)
        }

        override fun getRecordedActivitiesStringSet(context: Context): Set<String>? =
            getStringSet(context, TimelineObject.RecordItem::class.java)

        override fun putRecordedActivitiesStringSet(context: Context, set: Set<String>) {
            putStringSet(context, TimelineObject.RecordItem::class.java, set)
        }

        override fun getInputMelodiesStringSet(context: Context) =
            getStringSet(context, TimelineObject.InputMelodyItem::class.java)


        override fun putInputMelodiesStringSet(context: Context, set: Set<String>) {
            putStringSet(context, TimelineObject.InputMelodyItem::class.java, set)
        }

        override fun getCaptureSoundStringSet(context: Context) =
            getStringSet(context, TimelineObject.CaptureSoundItem::class.java)


        override fun putCaptureSoundStringSet(context: Context, set: Set<String>) {
            putStringSet(context, TimelineObject.CaptureSoundItem::class.java, set)
        }
    }

    companion object {

        var actualTimelineState: TimelineStateType = CompositionArtifacts

        /**
         * @sample getOppositeArtefactType
         */
        fun getOppositeArtefactType() = when (actualTimelineState) {
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