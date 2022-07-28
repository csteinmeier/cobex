package com.example.cobex

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.artifacts.Artifact
import com.example.cobex.databinding.FragmentCreateRhythmBinding
import com.example.cobex.helper.Extensions.resourceToString
import com.example.cobex.helper.MidiHelper
import com.example.cobex.helper.PermissionHelper
import com.example.cobex.helper.PermissionHelper.hasPermission

class CreateRhythm : Fragment() , CompositionArtifact.IArtifact,  PermissionHelper.IRequirePermission
{
    private var _binding: FragmentCreateRhythmBinding? = null
    private val binding get() = _binding!!     // This property is only valid between onCreateView and onDestroyView.
    private lateinit var event: ByteArray
    private var midihelper: MidiHelper = MidiHelper()
    private var mRecorder: MediaRecorder? = null

    private var currentColumn : RhythmNode? = null
    private var rhythmNodes: MutableMap<Int, RhythmNode>? = null

    private fun rhythmFileDir(context: Context) = "${getFileDir(context)}/audiorec_rhythm_${getRecNo(context)}.3gp"
    private fun getRecNo(context: Context) = getCounter(context, Artifact.InputMelody.javaClass) % 5

    private var mStartRecording = true
    private var playing = false
    private var playback: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = FragmentCreateRhythmBinding.inflate(inflater, container, false)
        getPermissionToRecordAudio()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBackCreate.setOnClickListener {
            findNavController().navigate(R.id.action_createRhythm_to_CreateNew)
        }
        binding.buttonPlay.setOnClickListener {
            togglePlay()
        }
        binding.buttonRec.setOnClickListener {
            hasPermission { record() }
        }

        // row 1: kick drum
        binding.buttonR1C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C1,1,1)
        }
        binding.buttonR1C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C2,2,1 )
        }
        binding.buttonR1C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C3,3,1)
        }
        binding.buttonR1C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C4, 4, 1)
        }
        binding.buttonR1C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C5, 5, 1)
        }
        binding.buttonR1C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C6, 6, 1)
        }
        binding.buttonR1C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C7, 7, 1)
        }
        binding.buttonR1C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C8,8,1)
        }
        binding.buttonR1C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C9,9,1)
        }
        binding.buttonR1C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C10, 10, 1)
        }
        binding.buttonR1C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C11, 11, 1)
        }
        binding.buttonR1C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR1C12, 12, 1)
        }

        // row 2: tom
        binding.buttonR2C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C1,1,2)
        }
        binding.buttonR2C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C2,2,2)
        }
        binding.buttonR2C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C3,3,2)
        }
        binding.buttonR2C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C4,4,2)
        }
        binding.buttonR2C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C5,5,2)
        }
        binding.buttonR2C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C6,6,2)
        }
        binding.buttonR2C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C7,7,2)
        }
        binding.buttonR2C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C8,8,2)
        }
        binding.buttonR2C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C9,9,2)
        }
        binding.buttonR2C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C10,10,2)
        }
        binding.buttonR2C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C11,11,2)
        }
        binding.buttonR2C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR2C12,12,2)
        }

        // row 3: snare

        binding.buttonR3C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C1,1,3)
        }
        binding.buttonR3C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C2,2,3)
        }
        binding.buttonR3C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C3,3,3)
        }
        binding.buttonR3C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C4,4,3)
        }
        binding.buttonR3C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C5,5,3)
        }
        binding.buttonR3C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C6,6,3)
        }
        binding.buttonR3C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C7,7,3)
        }
        binding.buttonR3C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C8,8,3)
        }
        binding.buttonR3C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C9,9,3)
        }
        binding.buttonR3C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C10, 10,3)
        }
        binding.buttonR3C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C11, 11,3)
        }
        binding.buttonR3C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR3C12, 12, 3)
        }

        // row 4: hi hat
        binding.buttonR4C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C1, 1, 4)
        }
        binding.buttonR4C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C2, 2,4)
        }
        binding.buttonR4C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C3, 3, 4)
        }
        binding.buttonR4C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C4, 4,4)
        }
        binding.buttonR4C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C5, 5, 4)
        }
        binding.buttonR4C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C6,6,4)
        }
        binding.buttonR4C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C7,7,4)
        }
        binding.buttonR4C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C8,8,4)
        }
        binding.buttonR4C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C9, 9,4)
        }
        binding.buttonR4C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C10, 10, 4)
        }
        binding.buttonR4C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C11, 11, 4)
        }
        binding.buttonR4C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR4C12, 12, 4)
        }

        // row 5: crash
        binding.buttonR5C1.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C1,1,5)
        }
        binding.buttonR5C2.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C2,2,5)
        }
        binding.buttonR5C3.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C3,3,5)
        }
        binding.buttonR5C4.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C4,4,5)
        }
        binding.buttonR5C5.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C5,5,5)
        }
        binding.buttonR5C6.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C6, 6, 5)
        }
        binding.buttonR5C7.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C7, 7, 5)
        }
        binding.buttonR5C8.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C8, 8, 5)
        }
        binding.buttonR5C9.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C9, 9, 5)
        }
        binding.buttonR5C10.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C10, 10, 5)
        }
        binding.buttonR5C11.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C11, 11, 5)
        }
        binding.buttonR5C12.setOnClickListener{
            rhythmButtonClicked(binding.buttonR5C12, 12, 5)
        }

        hideAllTickButtons()
        initRhythmNodes()
        showTickButton(1)
    }

//recording


    @RequiresApi(Build.VERSION_CODES.S)
    fun record() {
        onRecord(mStartRecording)
        if (mStartRecording)
        {
            binding.buttonRec.setImageDrawable(
                getContext()?.getResources()?.getDrawable(R.drawable.rec_on))
        }
        else
        {
            binding.buttonRec.setImageDrawable(
                getContext()?.getResources()?.getDrawable(R.drawable.rec_off))
        }
        mStartRecording = !mStartRecording
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onRecord(start: Boolean) {
        if (start) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null

        Toast.makeText(activity?.baseContext, "Song ${getRecNo(requireContext()) + 1} saved", Toast.LENGTH_SHORT).show()
        val toSave = rhythmFileDir(requireContext()) + "TIME:" +getTimeStamp(requireContext())
        synchroniseArtifact(
            requireContext(),
            toSave, this::class.java,
            CompositionArtifact.IArtifact.SynchronizeMode.APPEND
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)


        mRecorder?.setOutputFile(rhythmFileDir(requireContext()))

        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.prepare()
        mRecorder?.start()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        helperOnRequestPermissionResult(
            requestCode = requestCode,
            permission = permissions,
            grantResults = grantResults,
            hasPermission = { record() },
            null
        )
    }

    private fun rhythmButtonClicked(sender: Button, col: Int, row: Int)
    {
        // r1: kick, r2: tom, r3: snare, r4: hihat, r5: crash
        if(sender.text=="X")
        {
            sender.setBackgroundColor(resources.getColor(R.color.purple_500))
            sender.text = ""
            when(row)
            {
                1-> rhythmNodes?.get(col)?.kickActivated = false
                2-> rhythmNodes?.get(col)?.tomActivated = false
                3-> rhythmNodes?.get(col)?.snareActivated = false
                4-> rhythmNodes?.get(col)?.hihatActivated = false
                5-> rhythmNodes?.get(col)?.crashActivated = false
            }
        }
        else
        {
            sender.setBackgroundColor(resources.getColor(R.color.purple_200))
            sender.text = "X"
            when(row)
            {
                1-> rhythmNodes?.get(col)?.kickActivated = true
                2-> rhythmNodes?.get(col)?.tomActivated = true
                3-> rhythmNodes?.get(col)?.snareActivated = true
                4-> rhythmNodes?.get(col)?.hihatActivated = true
                5-> rhythmNodes?.get(col)?.crashActivated = true
            }
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


    private fun removeHighlightFromPrevColumn(count: Int)
    {
        when (count)
        {
            1 ->
            {
                if(binding.buttonR1C12.text=="X") binding.buttonR1C12.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C12.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C12.text=="X") binding.buttonR2C12.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C12.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C12.text=="X") binding.buttonR3C12.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C12.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C12.text=="X") binding.buttonR4C12.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C12.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C12.text=="X") binding.buttonR5C12.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C12.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            2 ->
            {
                if(binding.buttonR1C1.text=="X") binding.buttonR1C1.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C1.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C1.text=="X") binding.buttonR2C1.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C1.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C1.text=="X") binding.buttonR3C1.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C1.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C1.text=="X") binding.buttonR4C1.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C1.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C1.text=="X") binding.buttonR5C1.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C1.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            3 ->
            {
                if(binding.buttonR1C2.text=="X") binding.buttonR1C2.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C2.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C2.text=="X") binding.buttonR2C2.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C2.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C2.text=="X") binding.buttonR3C2.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C2.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C2.text=="X") binding.buttonR4C2.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C2.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C2.text=="X") binding.buttonR5C2.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C2.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            4->
            {
                if(binding.buttonR1C3.text=="X") binding.buttonR1C3.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C3.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C3.text=="X") binding.buttonR2C3.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C3.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C3.text=="X") binding.buttonR3C3.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C3.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C3.text=="X") binding.buttonR4C3.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C3.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C3.text=="X") binding.buttonR5C3.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C3.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            5 ->
            {
                if(binding.buttonR1C4.text=="X") binding.buttonR1C4.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C4.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C4.text=="X") binding.buttonR2C4.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C4.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C4.text=="X") binding.buttonR3C4.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C4.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C4.text=="X") binding.buttonR4C4.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C4.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C4.text=="X") binding.buttonR5C4.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C4.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            6 ->
            {
                if(binding.buttonR1C5.text=="X") binding.buttonR1C5.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C5.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C5.text=="X") binding.buttonR2C5.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C5.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C5.text=="X") binding.buttonR3C5.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C5.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C5.text=="X") binding.buttonR4C5.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C5.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C5.text=="X") binding.buttonR5C5.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C5.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            7 ->
            {
                if(binding.buttonR1C6.text=="X") binding.buttonR1C6.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C6.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C6.text=="X") binding.buttonR2C6.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C6.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C6.text=="X") binding.buttonR3C6.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C6.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C6.text=="X") binding.buttonR4C6.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C6.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C6.text=="X") binding.buttonR5C6.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C6.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            8->
            {
                if(binding.buttonR1C7.text=="X") binding.buttonR1C7.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C7.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C7.text=="X") binding.buttonR2C7.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C7.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C7.text=="X") binding.buttonR3C7.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C7.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C7.text=="X") binding.buttonR4C7.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C7.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C7.text=="X") binding.buttonR5C7.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C7.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            9 ->
            {
                if(binding.buttonR1C8.text=="X") binding.buttonR1C8.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C8.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C8.text=="X") binding.buttonR2C8.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C8.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C8.text=="X") binding.buttonR3C8.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C8.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C8.text=="X") binding.buttonR4C8.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C8.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C8.text=="X") binding.buttonR5C8.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C8.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            10 ->
            {
                if(binding.buttonR1C9.text=="X") binding.buttonR1C9.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C9.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C9.text=="X") binding.buttonR2C9.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C9.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C9.text=="X") binding.buttonR3C9.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C9.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C9.text=="X") binding.buttonR4C9.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C9.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C9.text=="X") binding.buttonR5C9.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C9.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            11 ->
            {
                if(binding.buttonR1C10.text=="X") binding.buttonR1C10.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C10.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C10.text=="X") binding.buttonR2C10.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C10.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C10.text=="X") binding.buttonR3C10.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C10.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C10.text=="X") binding.buttonR4C10.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C10.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C10.text=="X") binding.buttonR5C10.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C10.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            12->
            {
                if(binding.buttonR1C11.text=="X") binding.buttonR1C11.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR1C11.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR2C11.text=="X") binding.buttonR2C11.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR2C11.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR3C11.text=="X") binding.buttonR3C11.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR3C11.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR4C11.text=="X") binding.buttonR4C11.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR4C11.setBackgroundColor(resources.getColor(R.color.purple_500))
                if(binding.buttonR5C11.text=="X") binding.buttonR5C11.setBackgroundColor(resources.getColor(R.color.purple_200)) else binding.buttonR5C11.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
            else ->
            {
                Log.d("RHYTHM","ticked_with_problem")
            }
        }
    }

    private fun highlightCurrentColumn(count: Int)
    {
        when (count)
        {
            1 ->
            {
                if(binding.buttonR1C1.text=="X") binding.buttonR1C1.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C1.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C1.text=="X") binding.buttonR2C1.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C1.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C1.text=="X") binding.buttonR3C1.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C1.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C1.text=="X") binding.buttonR4C1.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C1.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C1.text=="X") binding.buttonR5C1.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C1.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            2 ->
            {
                if(binding.buttonR1C2.text=="X") binding.buttonR1C2.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C2.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C2.text=="X") binding.buttonR2C2.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C2.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C2.text=="X") binding.buttonR3C2.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C2.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C2.text=="X") binding.buttonR4C2.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C2.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C2.text=="X") binding.buttonR5C2.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C2.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            3 ->
            {
                if(binding.buttonR1C3.text=="X") binding.buttonR1C3.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C3.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C3.text=="X") binding.buttonR2C3.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C3.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C3.text=="X") binding.buttonR3C3.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C3.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C3.text=="X") binding.buttonR4C3.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C3.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C3.text=="X") binding.buttonR5C3.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C3.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            4->
            {
                if(binding.buttonR1C4.text=="X") binding.buttonR1C4.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C4.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C4.text=="X") binding.buttonR2C4.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C4.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C4.text=="X") binding.buttonR3C4.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C4.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C4.text=="X") binding.buttonR4C4.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C4.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C4.text=="X") binding.buttonR5C4.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C4.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            5 ->
            {
                if(binding.buttonR1C5.text=="X") binding.buttonR1C5.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C5.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C5.text=="X") binding.buttonR2C5.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C5.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C5.text=="X") binding.buttonR3C5.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C5.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C5.text=="X") binding.buttonR4C5.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C5.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C5.text=="X") binding.buttonR5C5.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C5.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            6 ->
            {
                if(binding.buttonR1C6.text=="X") binding.buttonR1C6.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C6.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C6.text=="X") binding.buttonR2C6.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C6.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C6.text=="X") binding.buttonR3C6.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C6.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C6.text=="X") binding.buttonR4C6.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C6.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C6.text=="X") binding.buttonR5C6.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C6.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            7 ->
            {
                if(binding.buttonR1C7.text=="X") binding.buttonR1C7.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C7.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C7.text=="X") binding.buttonR2C7.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C7.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C7.text=="X") binding.buttonR3C7.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C7.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C7.text=="X") binding.buttonR4C7.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C7.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C7.text=="X") binding.buttonR5C7.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C7.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            8->
            {
                if(binding.buttonR1C8.text=="X") binding.buttonR1C8.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C8.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C8.text=="X") binding.buttonR2C8.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C8.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C8.text=="X") binding.buttonR3C8.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C8.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C8.text=="X") binding.buttonR4C8.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C8.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C8.text=="X") binding.buttonR5C8.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C8.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            9 ->
            {
                if(binding.buttonR1C9.text=="X") binding.buttonR1C9.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C9.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C9.text=="X") binding.buttonR2C9.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C9.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C9.text=="X") binding.buttonR3C9.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C9.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C9.text=="X") binding.buttonR4C9.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C9.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C9.text=="X") binding.buttonR5C9.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C9.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            10 ->
            {
                if(binding.buttonR1C10.text=="X") binding.buttonR1C10.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C10.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C10.text=="X") binding.buttonR2C10.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C10.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C10.text=="X") binding.buttonR3C10.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C10.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C10.text=="X") binding.buttonR4C10.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C10.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C10.text=="X") binding.buttonR5C10.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C10.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            11 ->
            {
                if(binding.buttonR1C11.text=="X") binding.buttonR1C11.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C11.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C11.text=="X") binding.buttonR2C11.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C11.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C11.text=="X") binding.buttonR3C11.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C11.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C11.text=="X") binding.buttonR4C11.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C11.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C11.text=="X") binding.buttonR5C11.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C11.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            12->
            {
                if(binding.buttonR1C12.text=="X") binding.buttonR1C12.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR1C12.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR2C12.text=="X") binding.buttonR2C12.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR2C12.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR3C12.text=="X") binding.buttonR3C12.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR3C12.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR4C12.text=="X") binding.buttonR4C12.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR4C12.setBackgroundColor(resources.getColor(R.color.teal_700))
                if(binding.buttonR5C12.text=="X") binding.buttonR5C12.setBackgroundColor(resources.getColor(R.color.teal_200)) else binding.buttonR5C12.setBackgroundColor(resources.getColor(R.color.teal_700))
            }
            else ->
            {
                Log.d("RHYTHM","ticked_with_problem")
            }
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

                    handleCurrentTick(count)

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


    private fun handleCurrentTick(count:Int)
    {
        requireActivity().runOnUiThread()
        {
            currentColumn = rhythmNodes?.get(count)!!

            // tick display and sound
            hideAllTickButtons()
            showTickButton(count)

            if(!currentColumn?.snareActivated!! && !currentColumn?.hihatActivated!! && !currentColumn?.tomActivated!! && !currentColumn?.kickActivated!! && !currentColumn?.crashActivated!!)
            {
                view?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
            }

            // current column display and current sound(s)
            highlightCurrentColumn(count)
            removeHighlightFromPrevColumn(count)
            playCurrentSound()
            //Log.d("RHYTHM","ticked")
        }
    }

    private fun playNote(noteNumber: Int) {

        // Construct a note ON message for the note at maximum velocity on channel 10:
        event = ByteArray(3)
        event[0] = (0x90 or 0x09).toByte() // 0x90 = note On, 0x09 = channel 10
        event[1] = noteNumber.toByte()
        event[2] = 0x7F.toByte() // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midihelper.write(event)
    }

    private fun playCurrentSound()
    {
        if(currentColumn?.kickActivated==true)
        {
            // play kick sound
            playNote(36)
        }
        if(currentColumn?.crashActivated==true)
        {
            // play crash sound
            playNote(49)
        }
        if(currentColumn?.hihatActivated==true)
        {
            // play hihat sound
            playNote(44)
        }
        if(currentColumn?.snareActivated==true)
        {
            // play snare sound
            playNote(38)
        }
        if(currentColumn?.tomActivated==true)
        {
            // play tom sound
            playNote(43)
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

    override fun mainPermission() = Manifest.permission.RECORD_AUDIO

    override fun fragment() = this

    override fun fragmentCode() = PermissionHelper.FRAGMENT_CREATE_RHYTHM_CODE

    override fun requiredPermissions(): Array<out String> =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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