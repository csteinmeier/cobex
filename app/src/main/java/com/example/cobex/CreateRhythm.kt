package com.example.cobex

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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

    private var rhythmNodes: MutableMap<Int, RhythmNode>? = null

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

        // row 1: kick drum
        binding.buttonR1C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C1)
        }
        binding.buttonR1C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C2)
        }
        binding.buttonR1C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C3)
        }
        binding.buttonR1C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C4)
        }
        binding.buttonR1C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C5)
        }
        binding.buttonR1C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C6)
        }
        binding.buttonR1C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C7)
        }
        binding.buttonR1C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C8)
        }
        binding.buttonR1C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C9)
        }
        binding.buttonR1C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C10)
        }
        binding.buttonR1C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C11)
        }
        binding.buttonR1C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C12)
        }

        // row 2: tom
        binding.buttonR2C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C1)
        }
        binding.buttonR2C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C2)
        }
        binding.buttonR2C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C3)
        }
        binding.buttonR2C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C4)
        }
        binding.buttonR2C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C5)
        }
        binding.buttonR2C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C6)
        }
        binding.buttonR2C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C7)
        }
        binding.buttonR2C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C8)
        }
        binding.buttonR2C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C9)
        }
        binding.buttonR2C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C10)
        }
        binding.buttonR2C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C11)
        }
        binding.buttonR2C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C12)
        }

        // row 3: snare

        binding.buttonR3C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C1)
        }
        binding.buttonR3C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C2)
        }
        binding.buttonR3C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C3)
        }
        binding.buttonR3C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C4)
        }
        binding.buttonR3C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C5)
        }
        binding.buttonR3C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C6)
        }
        binding.buttonR3C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C7)
        }
        binding.buttonR3C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C8)
        }
        binding.buttonR3C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C9)
        }
        binding.buttonR3C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C10)
        }
        binding.buttonR3C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C11)
        }
        binding.buttonR3C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C12)
        }

        // row 4: hi hat
        binding.buttonR4C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C1)
        }
        binding.buttonR4C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C2)
        }
        binding.buttonR4C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C3)
        }
        binding.buttonR4C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C4)
        }
        binding.buttonR4C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C5)
        }
        binding.buttonR4C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C6)
        }
        binding.buttonR4C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C7)
        }
        binding.buttonR4C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C8)
        }
        binding.buttonR4C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C9)
        }
        binding.buttonR4C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C10)
        }
        binding.buttonR4C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C11)
        }
        binding.buttonR4C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C12)
        }

        // row 5: crash
        binding.buttonR5C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C1)
        }
        binding.buttonR5C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C2)
        }
        binding.buttonR5C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C3)
        }
        binding.buttonR5C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C4)
        }
        binding.buttonR5C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C5)
        }
        binding.buttonR5C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C6)
        }
        binding.buttonR5C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C7)
        }
        binding.buttonR5C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C8)
        }
        binding.buttonR5C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C9)
        }
        binding.buttonR5C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C10)
        }
        binding.buttonR5C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C11)
        }
        binding.buttonR5C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C12)
        }

        hideAllTickButtons()
        initRhythmNodes()
        showTickButton(1)
    }

    private fun rhythmButtonClicked(sender: Button) {
        if(sender.text=="X")
        {
            sender.setBackgroundColor(resources.getColor(R.color.purple_500))
            sender.text = ""
        }
        else
        {
            sender.setBackgroundColor(Color.MAGENTA)
            sender.text = "X"
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

    private fun hideAllTickButtons()
    {
        binding.tick1.visibility= INVISIBLE
        binding.tick2.visibility= INVISIBLE
        binding.tick3.visibility= INVISIBLE
        binding.tick4.visibility= INVISIBLE
        binding.tick5.visibility= INVISIBLE
        binding.tick6.visibility= INVISIBLE
        binding.tick7.visibility= INVISIBLE
        binding.tick8.visibility= INVISIBLE
        binding.tick9.visibility= INVISIBLE
        binding.tick10.visibility= INVISIBLE
        binding.tick11.visibility= INVISIBLE
        binding.tick12.visibility= INVISIBLE
    }

    private fun showTickButton(count: Int)
    {
        rhythmNodes?.get(count)?.tickDisplay?.visibility= VISIBLE
        //Log.d("Tickbutton", "nr $count")
    }

    private fun startPlaying() {
        playback = object : Runnable {
            var count = 1
            override fun run() {
                while (playing) {
                    //Log.d("RHYTHM","ticked")

                    playTickSound(count)
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



    private fun playTickSound(count:Int)
    {
        requireActivity().runOnUiThread()
        {
            hideAllTickButtons()
            showTickButton(count)
            view?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
            //Log.d("RHYTHM","ticked")
        }

    }

    private fun initRhythmNodes()
    {
        rhythmNodes = mutableMapOf<Int, RhythmNode>()
        rhythmNodes!![1] = RhythmNode(1,binding.tick1)
        rhythmNodes!![2] = RhythmNode(2,binding.tick2)
        rhythmNodes!![3] = RhythmNode(3,binding.tick3)
        rhythmNodes!![4] = RhythmNode(4,binding.tick4)
        rhythmNodes!![5] = RhythmNode(5,binding.tick5)
        rhythmNodes!![6] = RhythmNode(6,binding.tick6)
        rhythmNodes!![7] = RhythmNode(7,binding.tick7)
        rhythmNodes!![8] = RhythmNode(8,binding.tick8)
        rhythmNodes!![9] = RhythmNode(9,binding.tick9)
        rhythmNodes!![10] = RhythmNode(10,binding.tick10)
        rhythmNodes!![11] = RhythmNode(11,binding.tick11)
        rhythmNodes!![12] = RhythmNode(12,binding.tick12)
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

class RhythmNode
{
    constructor(i: Int, dot:ImageView) {
        number = i
        tickDisplay = dot
    }

    var kickActivated: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var tomActivated: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var snareActivated: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var hihatActivated: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var crashActivated: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var number: Int

    var tickDisplay : ImageView
        get() = field
        set(value) {
            field =value
        }
}