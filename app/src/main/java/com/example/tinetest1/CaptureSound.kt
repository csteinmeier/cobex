package com.example.tinetest1

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tinetest1.databinding.FragmentCaptureSoundBinding

/**
 * A simple [Fragment] subclass.
 */
class CaptureSound : Fragment() {

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
        }
    }


    private fun startRecording()
    {
        if (CheckPermissions())
        {

        }
        else
        {
            RequestPermissions()
        }
    }

    private fun RequestPermissions() {
        TODO("Not yet implemented")
    }


    private fun CheckPermissions(): Boolean {
        TODO("Not yet implemented")
/*        // this method is used to check permission
        val result = ContextCompat.checkSelfPermission(
            ApplicationProvider.getApplicationContext<Context>(),
            WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(
            ApplicationProvider.getApplicationContext<Context>(),
            RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED*/
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