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


class CaptureAction : Fragment(), PermissionHelper.IRequirePermission,
    CompositionArtifact.IArtifact {

    /************************************************************************************/
    /**                         All Activities to track                                 */
    /** To exclude or include other Activities it just need to add/remove from this enum*/
    /************************************************************************************/
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
            hasPermission { switchButtonState(b, isChecked) }
        }
    }

    private fun initSwitchButton(switch: CompoundButton) {
        val isOn = getBoolean(requireContext(), this.javaClass)
        switch.isChecked = isOn
        switch.text = setSwitchButtonText(isOn)
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
            hasPermission = { switchButtonState(binding.switchTrackYourActivities, true) },
            null
        )

    }

    private fun requestForUpdates() {
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
                                detectedActivity.toString(),
                                CaptureAction::class.java,
                                true
                            )
                    }
                }
            }
        }

        private fun shouldSave(savedActivities: List<String>, detectedActivity: String): Boolean =
            savedActivities.isEmpty() ||
                    savedActivities.last().substringAfterLast(":") != detectedActivity
    }

    private fun getPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            requireContext(),
            PermissionHelper.FRAGMENT_CAPTURE_ACTIVITY_CODE,
            Intent(this.requireContext(), ActivityTransitionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getActivitiesToTrack(): List<ActivityTransition> =
        Activities.values().flatMap { getEnterExitActivity(it.detectedActivity) }


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

    private fun switchButtonState(switch: CompoundButton, isChecked: Boolean) {
        switch.text = setSwitchButtonText(isChecked)
        if (isChecked) {
            requestForUpdates()
            markAsSavedIfNotMarkedAsSaved(requireContext(), this.javaClass)
            putBoolean(requireContext(), this.javaClass, true)
        } else {
            removeUpdates()
            putBoolean(requireContext(), this.javaClass, false)
        }
    }

    private fun setSwitchButtonText(isChecked: Boolean): CharSequence {
        return if (isChecked) requireActivity().getText(R.string.button_Switch_ON) else requireActivity().getText(
            R.string.button_Switch_OFF
        )
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