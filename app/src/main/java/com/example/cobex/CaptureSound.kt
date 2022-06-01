package com.example.cobex

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cobex.helper.Extensions.resourceToString
import com.example.cobex.artifacts.Artifact
import com.example.cobex.artifacts.CompositionArtifact
import com.example.cobex.databinding.FragmentCaptureSoundBinding
import com.example.cobex.helper.PermissionHelper

/**
 * A simple [Fragment] subclass.
 */
class CaptureSound : Fragment(), CompositionArtifact.IArtifact, PermissionHelper.IRequirePermission {

    private var mRecorder: MediaRecorder? = null
    private var _binding: FragmentCaptureSoundBinding? = null

    private fun getRecNo(context: Context) = getCounter(context, Artifact.CaptureSound.javaClass) % 5
    private fun soundFileDir(context: Context) =
        "${getFileDir(context)}/recording${getRecNo(requireContext())}.mp3"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    /**used to recognize Type of Sound*/
    private var environmentSounds = false
    private var started = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCaptureSoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleSoundInputType()

        binding.buttonEnvironment.setOnClickListener {
            environmentSounds = true
            toggleSoundInputType()
        }
        binding.buttonMusic.setOnClickListener {

            environmentSounds = false
            toggleSoundInputType()
        }
        binding.buttonStart.setOnClickListener {
            hasPermission { toggleRecording() }
        }

        val web = binding.webView
        web.setBackgroundColor(Color.TRANSPARENT) //for gif without background
        web.loadUrl("file:///android_asset/htmls/gif.html")
        web.visibility= View.INVISIBLE

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        helperOnRequestPermissionResult(
            requestCode = requestCode,
            permission = permissions,
            grantResults = grantResults,
            hasPermission = { toggleRecording() },
            null
        )
    }

    private fun toggleRecording() {
        if (!started)
        {
            started = true
            binding.buttonStart.text = R.string.buttonStop.resourceToString(requireContext())
            binding.webView.visibility= View.VISIBLE
            startRecording()
        }
        else
        {
            started = false
            binding.buttonStart.text = R.string.buttonStart.resourceToString(requireContext())
            binding.webView.visibility= View.INVISIBLE
            stopRecording()
        }
    }


    private fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()

        Toast.makeText(activity?.baseContext, "Sound ${getRecNo(requireContext()) + 1} saved", Toast.LENGTH_SHORT).show()
        val soundType = if(environmentSounds) "ENV" else "MUSIC"
        val toSave = soundFileDir(requireContext()) + "TIME:" + getTimeStamp(requireContext())
        synchroniseArtifact(
            requireContext(),
            "TYPE:$soundType$toSave",
            Artifact.CaptureSound.javaClass,
            CompositionArtifact.IArtifact.SynchronizeMode.APPEND
        )
    }


    private fun startRecording()
    {

        val outputfile = soundFileDir(requireContext())

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder?.setOutputFile(outputfile)

        mRecorder?.prepare()
        mRecorder?.start()
    }

    private fun toggleSoundInputType() {
        if (environmentSounds)
        {
            binding.buttonEnvironment.setBackgroundColor(Color.BLACK)
            binding.buttonMusic.setBackgroundColor(Color.parseColor("#FF6200EE"))
        }
        else
        {
            binding.buttonMusic.setBackgroundColor(Color.BLACK)
            binding.buttonEnvironment.setBackgroundColor(Color.parseColor("#FF6200EE"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun mainPermission() = Manifest.permission.RECORD_AUDIO

    override fun fragment(): Fragment  = this

    override fun fragmentCode(): Int = PermissionHelper.FRAGMENT_CAPTURE_SOUND_CODE

    override fun requiredPermissions(): Array<out String> =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
}