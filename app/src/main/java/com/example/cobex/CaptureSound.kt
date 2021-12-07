package com.example.cobex

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cobex.databinding.FragmentCaptureSoundBinding

/**
 * A simple [Fragment] subclass.
 */
class CaptureSound : Fragment() {

    private var recNo: Int = 1
    private var mRecorder: MediaRecorder? = null
    private var _binding: FragmentCaptureSoundBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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
            toggleRecording()
        }

        var web = binding.webView
        web.setBackgroundColor(Color.TRANSPARENT) //for gif without background
        web.loadUrl("file:///android_asset/htmls/gif.html")
        web.visibility= View.INVISIBLE
    }

    private fun toggleRecording() {
        if (!started)
        {
            started = true
            binding.buttonStart.text = "Stop"
            binding.webView.visibility= View.VISIBLE
            startRecording()
        }
        else
        {
            started = false
            binding.buttonStart.text = "Start"
            binding.webView.visibility= View.INVISIBLE
            stopRecording()
        }
    }

    private fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()

        if (recNo==5)
        {
            recNo = 1
        }
        else
        {
            recNo++
        }
    }


    private fun startRecording()
    {
        getPermissions();

        var outputfile = activity?.filesDir?.absolutePath + "/recording"+ recNo + ".mp3"

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder?.setOutputFile(outputfile)

        mRecorder?.prepare()
        mRecorder?.start()
    }

    private fun getPermissions() {
        if (activity?.let { ContextCompat.checkSelfPermission(it.baseContext, Manifest.permission.RECORD_AUDIO) } != PackageManager.PERMISSION_GRANTED ||
            activity?.let { ContextCompat.checkSelfPermission(it.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED ||
            activity?.let { ContextCompat.checkSelfPermission(it.baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED )
        {
            // The permission is NOT already granted. Check if the user has been asked about this permission already and denied it.
            // If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission. This will show the standard permission request dialog UI
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 13
            )
        }
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
}