package com.example.cobex.capture_action

import com.example.cobex.R
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity

/**
 * Contains all Activities this App should recognize
 *
 * * **activityRecordDisplayString** is shown as a Broadcast message, for example:
 * activityRecordStringStill = Record: You stand still
 *
 * * **activityRecognitionString** simply a readable string for the user,
 * matching the detected activity
 *
 * * **detectedActivity** androids class for the detected activity
 *
 * * **activityIcon** symbolise the matching activity
 */
enum class Activities(
    val activityRecordDisplayString: Int,
    val activityRecognitionString: Int,
    val detectedActivity: Int,
    val activityIcon: Int
) {
    STILL(
        R.string.activityRecordStringStill,
        R.string.activityRecognitionStill,
        DetectedActivity.STILL,
        R.drawable.ic_directions_off_24
    ),
    WALKING(
        R.string.activityRecordStringWalking,
        R.string.activityRecognitionWalking,
        DetectedActivity.WALKING,
        R.drawable.ic_directions_walk_24
    ),
    RUNNING(
        R.string.activityRecordStringRunning,
        R.string.activityRecognitionRunning,
        DetectedActivity.RUNNING,
        R.drawable.ic_directions_run_24
    ),
    VEHICLE(
        R.string.activityRecordStringVehicle,
        R.string.activityRecognitionVehicle,
        DetectedActivity.IN_VEHICLE,
        R.drawable.ic_directions_vehicle_24
    ),
    BICYCLE(
        R.string.activityRecordStringBicycle,
        R.string.activityRecognitionBicycle,
        DetectedActivity.ON_BICYCLE,
        R.drawable.ic_directions_bike_24
    ),
}

object ActivityRecords{

    fun asListToTrack() =
        Activities.values().flatMap { getEnterExitTransition(it.detectedActivity) }

    private fun getEnterExitTransition(detectedActivity: Int) =
        listOf(
            getActivityTransition(detectedActivity, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getActivityTransition(detectedActivity, ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        )

    private fun getActivityTransition(detectedActivity: Int, activityTransition: Int) =
        ActivityTransition.Builder()
            .setActivityType(detectedActivity)
            .setActivityTransition(activityTransition)
            .build()
}