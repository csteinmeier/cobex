package com.example.cobex

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentCreateRhythmBinding


class CreateRhythm : Fragment() {

    private var _binding: FragmentCreateRhythmBinding? = null
    private lateinit var event: ByteArray
    private var midihelper: MidiHelper = MidiHelper()
    private var mRecorder: MediaRecorder? = null

    // we can record up to 5 rhythms for each experience
    var mFileName1: String? = null
    var mFileName2: String? = null
    var mFileName3: String? = null
    var mFileName4: String? = null
    var mFileName5: String? = null
    var recordingno = 1

    var mStartRecording = true
    var playing = false
    private var playback: Runnable? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateRhythmBinding.inflate(inflater, container, false)

        //mFileName1 = activity?.externalCacheDir?.absolutePath  <-- use if recordings should be only temporary
        mFileName1 = activity?.filesDir?.absolutePath
        mFileName1 += "/rhythmrec1.3gp"
        mFileName2 = activity?.filesDir?.absolutePath
        mFileName2 += "/rhythmrec2.3gp"
        mFileName3 = activity?.filesDir?.absolutePath
        mFileName3 += "/rhythmrec3.3gp"
        mFileName4 = activity?.filesDir?.absolutePath
        mFileName4 += "/rhythmrec4.3gp"
        mFileName5 = activity?.filesDir?.absolutePath
        mFileName5 += "/rhythmrec5.3gp"

        getPermissionToRecordAudio()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBackCreate.setOnClickListener {
            findNavController().navigate(R.id.action_createRhythm_to_CreateNew)
        }

        binding.buttonPlay.setOnClickListener {
            togglePlay()
        }
    }

    private fun togglePlay() {
        if (!playing) {
            startPlaying()
            binding.buttonPlay.setImageDrawable(
                getContext()?.getResources()?.getDrawable(android.R.drawable.ic_media_pause)
            )
        } else {
            playing = false
            binding.buttonPlay.setImageDrawable(
                getContext()?.getResources()?.getDrawable(android.R.drawable.ic_media_play)
            )
        }
    }

    private fun startPlaying() {
        playback = object : Runnable {
            var count = 1
            override fun run() {
                while (playing) {
                    //Log.d("RHYTHM","ticked")
                    playTickSound()
                    count = (count + 1)

                    if (count == 13)
                        count = 1

                    val next: Long = 60 * 1000 / 120 //bpm
                    try {
                        Thread.sleep(next)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        playing = true
        val thandler: Thread = Thread(playback)
        thandler.start()
    }

    private fun playTickSound()
    {
        requireActivity().runOnUiThread()
        {
            view?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
            Log.d("RHYTHM","ticked")
        }

    }




    fun getPermissionToRecordAudio() {
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

    override fun onResume() {
        super.onResume()
        midihelper.start()

    }
}