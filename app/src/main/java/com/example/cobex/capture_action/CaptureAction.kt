package com.example.cobex.capture_action

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cobex.CompositionArtifact
import com.example.cobex.Extensions.showAsToast
import com.example.cobex.PermissionHelper
import com.example.cobex.R
import com.example.cobex.databinding.FragmentCaptureActionBinding
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransitionRequest

class CaptureAction : Fragment(), PermissionHelper.IRequirePermission,
    CompositionArtifact.IArtifact {

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

    private fun requestForUpdates() {
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionRequest(ActivityRecords.asListToTrack()),
                getPendingIntent()
            )
            .addOnSuccessListener {
                getString(R.string.activityRecognitionTrackingON)
                    .showAsToast(requireContext(), Toast.LENGTH_SHORT)
            }
            .addOnFailureListener {
                getString(R.string.somethingWentWrong)
                    .showAsToast(requireContext(), Toast.LENGTH_SHORT)
            }
    }


    private fun removeUpdates() {
        client.removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                getString(R.string.activityRecognitionTrackingOFF)
                    .showAsToast(requireContext(), Toast.LENGTH_SHORT)
            }
            .addOnFailureListener {
                getString(R.string.somethingWentWrong)
                    .showAsToast(requireContext(), Toast.LENGTH_SHORT)
            }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            requireContext(),
            PermissionHelper.FRAGMENT_CAPTURE_ACTIVITY_CODE,
            Intent(this.requireContext(), ActivityBroadcastService::class.java),
             PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun setSwitchButtonText(isChecked: Boolean): CharSequence {
        return if (isChecked)
            requireActivity().getText(R.string.button_Switch_ON)
        else
            requireActivity().getText(R.string.button_Switch_OFF
        )
    }

    override fun mainPermission() = Manifest.permission.ACTIVITY_RECOGNITION

    override fun fragment() = this

    override fun fragmentCode() = PermissionHelper.FRAGMENT_CAPTURE_ACTIVITY_CODE

    override fun requiredPermissions() =
        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)

}