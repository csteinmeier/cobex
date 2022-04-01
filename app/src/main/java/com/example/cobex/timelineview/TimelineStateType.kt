package com.example.cobex.timelineview

import android.content.Context
import com.example.cobex.CaptureAction
import com.example.cobex.CapturePicture
import com.example.cobex.CompositionArtifact

sealed class TimelineStateType : CompositionArtifact.IArtifact {

    abstract fun getCapturedImagesStringSet(context: Context): Set<String>?
    abstract fun putCapturedImageStringSet(context: Context, set: Set<String>)

    abstract fun getRecordedActivitiesStringSet(context: Context): Set<String>?
    abstract fun putRecordedActivitiesStringSet(context: Context, set: Set<String>)

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
    }

    companion object {
        fun getArtefactType() = when (actualTimelineState) {
            CompositionArtifacts -> CompositionArtifacts
            StoredArtifacts -> StoredArtifacts
        }

        fun getOppositeArtefactType() = when (actualTimelineState) {
            CompositionArtifacts -> StoredArtifacts
            StoredArtifacts -> CompositionArtifacts
        }

        var actualTimelineState: TimelineStateType = CompositionArtifacts

        fun changeState() = when (actualTimelineState) {
            CompositionArtifacts -> actualTimelineState = StoredArtifacts
            StoredArtifacts -> actualTimelineState = CompositionArtifacts
        }
    }
}