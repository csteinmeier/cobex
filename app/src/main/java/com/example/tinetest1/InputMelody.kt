package com.example.tinetest1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.tinetest1.databinding.FragmentInputMelodyBinding

/**
 * A simple [Fragment] subclass.
 * Use the [InputMelody.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputMelody : Fragment(), View.OnTouchListener, AdapterView.OnItemSelectedListener,
    AdapterView.OnItemClickListener {

    private var _binding: FragmentInputMelodyBinding? = null
    private lateinit var event: ByteArray
    private var midihelper : MidiHelper = MidiHelper()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val button = AppCompatButton(inflater.context)
        _binding = FragmentInputMelodyBinding.inflate(inflater, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.record.setOnClickListener {

        }

        binding.play.setOnClickListener {

        }

        binding.instrumentSpinner.onItemClickListener = this
        binding.instrumentSpinner.onItemSelectedListener = this

        //region listener binding for piano keys
        binding.w1.setOnTouchListener(this)
        binding.w2.setOnTouchListener(this)
        binding.w3.setOnTouchListener(this)
        binding.w4.setOnTouchListener(this)
        binding.w5.setOnTouchListener(this)
        binding.w6.setOnTouchListener(this)
        binding.w7.setOnTouchListener(this)
        binding.w8.setOnTouchListener(this)
        binding.w9.setOnTouchListener(this)
        binding.w10.setOnTouchListener(this)
        binding.w11.setOnTouchListener(this)
        binding.w12.setOnTouchListener(this)
        binding.w13.setOnTouchListener(this)
        binding.w14.setOnTouchListener(this)
        binding.w15.setOnTouchListener(this)
        binding.w16.setOnTouchListener(this)
        binding.w17.setOnTouchListener(this)
        binding.w18.setOnTouchListener(this)
        binding.w19.setOnTouchListener(this)
        binding.w20.setOnTouchListener(this)
        binding.w21.setOnTouchListener(this)
        binding.w22.setOnTouchListener(this)
        binding.w23.setOnTouchListener(this)
        binding.w24.setOnTouchListener(this)
        binding.w25.setOnTouchListener(this)
        binding.w26.setOnTouchListener(this)
        binding.w27.setOnTouchListener(this)
        binding.w28.setOnTouchListener(this)
        binding.w29.setOnTouchListener(this)
        binding.w30.setOnTouchListener(this)
        binding.w31.setOnTouchListener(this)
        binding.w32.setOnTouchListener(this)
        binding.w33.setOnTouchListener(this)
        binding.w34.setOnTouchListener(this)
        binding.w35.setOnTouchListener(this)

        binding.b1.setOnTouchListener(this)
        binding.b2.setOnTouchListener(this)
        binding.b3.setOnTouchListener(this)
        binding.b4.setOnTouchListener(this)
        binding.b5.setOnTouchListener(this)
        binding.b6.setOnTouchListener(this)
        binding.b7.setOnTouchListener(this)
        binding.b8.setOnTouchListener(this)
        binding.b9.setOnTouchListener(this)
        binding.b10.setOnTouchListener(this)
        binding.b11.setOnTouchListener(this)
        binding.b12.setOnTouchListener(this)
        binding.b13.setOnTouchListener(this)
        binding.b14.setOnTouchListener(this)
        binding.b15.setOnTouchListener(this)
        binding.b16.setOnTouchListener(this)
        binding.b17.setOnTouchListener(this)
        binding.b18.setOnTouchListener(this)
        binding.b19.setOnTouchListener(this)
        binding.b20.setOnTouchListener(this)
        binding.b21.setOnTouchListener(this)
        binding.b22.setOnTouchListener(this)
        binding.b23.setOnTouchListener(this)
        binding.b24.setOnTouchListener(this)
        binding.b25.setOnTouchListener(this)
//endregion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != null) {
            var noteNumber = 0
            when (v.id) {
                R.id.w1 -> noteNumber = 36 //C3
                R.id.b1 -> noteNumber = 37 //C#3
                R.id.w2 -> noteNumber = 38 //D3
                R.id.b2 -> noteNumber = 39 //D#3
                R.id.w3 -> noteNumber = 40 //E3
                R.id.w4 -> noteNumber = 41 //F3
                //todo: implement this...
            }

            if (event != null) {
                if (event.action == MotionEvent.ACTION_UP) {
                    stopNote(noteNumber, false);
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    playNote(noteNumber);
                }
            }
        }
        return false
    }

    private fun stopNote(noteNumber: Int, sustainUpEvent: Boolean) {

        // Stop the note unless the sustain button is currently pressed. Or stop the note if the
        // sustain button was depressed and the note's button is not pressed.
        // Stop the note unless the sustain button is currently pressed. Or stop the note if the
        // sustain button was depressed and the note's button is not pressed.
        if (sustainUpEvent) {
            // Construct a note OFF message for the note at minimum velocity on channel 1:
            event = ByteArray(3)
            event[0] = (0x80 or 0x00).toByte() // 0x80 = note Off, 0x00 = channel 1
            event[1] = noteNumber as Byte
            event[2] = 0x00.toByte() // 0x00 = the minimum velocity (0)

            // Send the MIDI event to the synthesizer.
            midihelper.write(event)
        }
    }

    private fun playNote(noteNumber: Int) {

        // Construct a note ON message for the note at maximum velocity on channel 1:
        event = ByteArray(3)
        event[0] = (0x90 or 0x00).toByte() // 0x90 = note On, 0x00 = channel 1
        event[1] = noteNumber as Byte
        event[2] = 0x7F.toByte() // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midihelper.write(event)
    }

    override fun onResume() {
        super.onResume()
        midihelper.start()

        // Get the configuration.
        var config = midihelper.config()

        // Print out the details.
        Log.d(this.javaClass.name, "maxVoices: " + config.get(0))
        Log.d(this.javaClass.name, "numChannels: " + config.get(1))
        Log.d(this.javaClass.name, "sampleRate: " + config.get(2))
        Log.d(this.javaClass.name, "mixBufferSize: " + config.get(3))
    }

    override fun onPause() {
        super.onPause()
        midihelper.stop()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

}

data class SpinnerEntry(
    // define whatever properties you want
    val label: String,
    val key: Int,
) {
    override fun toString(): String {
        return label // this will display to user
    }
}