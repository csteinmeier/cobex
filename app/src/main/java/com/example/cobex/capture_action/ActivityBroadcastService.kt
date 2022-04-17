package com.example.cobex.capture_action

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.cobex.CompositionArtifact
import com.example.cobex.NotificationHelper
import com.google.android.gms.location.ActivityTransitionResult

class ActivityBroadcastService : BroadcastReceiver(), CompositionArtifact.IArtifact {

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)

            result?.let {
                result.transitionEvents.forEach { event ->

                    Log.i("ActivityTransitionEvent", event.toString())

                    markAsSavedIfNotMarkedAsSaved(context, this.javaClass)

                    val detectedActivity = Activities.values()
                        .find { it.detectedActivity == event.activityType }

                    NotificationHelper.setNotification(
                        context = context,
                        channel = NotificationHelper.CHANNEL.ACTIVITY_RECOGNITION,
                        icon = null,
                        title = null,
                        text = detectedActivity?.let { it1 ->
                            context.getString(it1.activityRecordDisplayString)
                        }
                    )

                    val savedDetectedActivities =
                        getStringSet(context, this::class.java)?.sorted()?.toList()
                            ?: listOf()

                    if (shouldSave(savedDetectedActivities, detectedActivity.toString()))
                        synchroniseArtifact(
                            context,
                            detectedActivity.toString()+ "TIME:" + getTimeStamp(context),
                            CaptureAction::class.java, true
                        )
                }
            }
        }
    }

    private fun shouldSave(savedActivities: List<String>, detectedActivity: String): Boolean =
        savedActivities.isEmpty() ||
                savedActivities.last().substringAfter("TIME:") != detectedActivity
}