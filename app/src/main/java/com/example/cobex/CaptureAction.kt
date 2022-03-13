package com.example.cobex

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cobex.databinding.FragmentCaptureActionBinding
import com.google.android.gms.location.*
import java.time.Instant
import java.time.format.DateTimeFormatter


class CaptureAction : Fragment(), PermissionHelper.IRequirePermission, CompositionArtifact.IArtifact {

    private var _binding: FragmentCaptureActionBinding? = null
    private val binding get() = _binding!!

    private lateinit var client: ActivityRecognitionClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = ActivityRecognition.getClient(this.requireActivity())

        initSwitchButton(binding.switchTrackYourActivities)

        binding.switchTrackYourActivities.setOnCheckedChangeListener { b, isChecked ->
            hasPermission { setSwitchButtonText(b, isChecked) }
        }

    }

    private fun initSwitchButton(switch: CompoundButton){
        switch.isChecked = getBoolean(requireContext(), this.javaClass)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        helperOnRequestPermissionResult(
            requestCode = requestCode,
            permission = permissions,
            grantResults = grantResults,
            hasPermission = { setSwitchButtonText(binding.switchTrackYourActivities, true) },
            null
        )

    }

    private fun requestForUpdates() {
        Log.i("Recognition", getActivitiesToTrack().toString())
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionRequest(getActivitiesToTrack()),
                getPendingIntent()
            )
            .addOnSuccessListener {
                showToast(requireContext().getString(R.string.activityRecognitionTrackingON))
            }
            .addOnFailureListener {
                showToast(requireContext().getString(R.string.somethingWentWrong))
            }
    }


    private fun removeUpdates() {
        client.removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                showToast(requireContext().getString(R.string.activityRecognitionTrackingOFF))
            }
            .addOnFailureListener {
                showToast(requireContext().getString(R.string.somethingWentWrong))
            }
    }


    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    class ActivityTransitionReceiver : BroadcastReceiver(), CompositionArtifact.IArtifact {

        override fun onReceive(context: Context, intent: Intent) {
            if (ActivityTransitionResult.hasResult(intent)) {

                val result = ActivityTransitionResult.extractResult(intent)

                result?.let {
                    result.transitionEvents.forEach { event ->

                        markAsSavedIfNotMarkedAsSaved(context, this.javaClass)

                        val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                        val trackedActivities = getStringSet(context, this.javaClass)
                        val newActivity = "Detected Activity $timeStamp :${activityToString(event)}"
                        val newSet: MutableSet<String> = mutableSetOf()
                        trackedActivities?.forEach { savedActivity -> newSet.add(savedActivity) }
                        newSet.add(newActivity)
                        putStringSet(context, this.javaClass, newSet)

                        NotificationHelper.setNotification(
                            context = context,
                            channel = NotificationHelper.CHANNEL.ACTIVITY_RECOGNITION,
                            icon = null,
                            title = null,
                            text = activityToUserString(context, event)
                        )
                    }
                }
            }
        }

        private fun activityToString(
            activityTransitionEvent: ActivityTransitionEvent
        ) = when (activityTransitionEvent.activityType) {
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
            DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
            DetectedActivity.RUNNING -> "RUNNING"
            DetectedActivity.TILTING -> "TILTING"
            else -> "UKNOWN"
        }

        private fun activityToUserString(
            context: Context,
            activityTransitionEvent: ActivityTransitionEvent
        ) = when (activityTransitionEvent.activityType) {
            DetectedActivity.STILL -> context.getString(R.string.activityStill)
            DetectedActivity.WALKING -> context.getString(R.string.activityWalking)
            DetectedActivity.IN_VEHICLE -> context.getString(R.string.activityVehicle)
            DetectedActivity.ON_BICYCLE -> context.getString(R.string.activityBicycle)
            DetectedActivity.RUNNING -> context.getString(R.string.activityRunning)
            DetectedActivity.TILTING -> context.getString(R.string.activityTilting)
            else -> "UKNOWN"
        }

    }

    private fun getPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            requireContext(),
            PermissionHelper.FRAGMENT_CAPTURE_ACTIVITY_CODE,
            Intent(this.requireContext(), ActivityTransitionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    /*****************************************************************************/
    /**                         All Activities to track                         **/
    /*****************************************************************************/

    private fun getActivitiesToTrack(): List<ActivityTransition> {
        val list = mutableListOf<ActivityTransition>().apply {

            addAll(getEnterExitActivity(DetectedActivity.STILL))

            addAll(getEnterExitActivity(DetectedActivity.WALKING))

            addAll(getEnterExitActivity(DetectedActivity.IN_VEHICLE))

            addAll(getEnterExitActivity(DetectedActivity.ON_BICYCLE))

            addAll(getEnterExitActivity(DetectedActivity.RUNNING))


        }
        return list
    }


    private fun getEnterExitActivity(detectedActivity: Int): List<ActivityTransition> =
        listOf(
            getActivityTransition(detectedActivity, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getActivityTransition(detectedActivity, ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        )

    private fun getActivityTransition(
        detectedActivity: Int,
        activityTransition: Int
    ) =
        ActivityTransition.Builder()
            .setActivityType(detectedActivity)
            .setActivityTransition(activityTransition)
            .build()

    private fun setSwitchButtonText(switch: CompoundButton, isChecked: Boolean) {
        switch.text =
            if (isChecked) {
                requestForUpdates()
                markAsSavedIfNotMarkedAsSaved(requireContext(), this.javaClass)
                putBoolean(requireContext(), this.javaClass, true)
                requireActivity().getText(R.string.button_Switch_ON)
            } else {
                getBoolean(requireContext(), this.javaClass)
                removeUpdates()
                requireActivity().getText(R.string.button_Switch_OFF)
            }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun mainPermission() = Manifest.permission.ACTIVITY_RECOGNITION

    override fun fragment() = this

    override fun fragmentCode() = PermissionHelper.FRAGMENT_CAPTURE_ACTIVITY_CODE

    override fun requiredPermissions() =
        arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

}