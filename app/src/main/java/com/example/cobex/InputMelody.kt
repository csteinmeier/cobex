package com.example.cobex

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.cobex.databinding.FragmentInputMelodyBinding





/**
 * A simple [Fragment] subclass.
 * Use the [InputMelody.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputMelody : Fragment(), View.OnTouchListener,
    AdapterView.OnItemSelectedListener, CompositionArtifact.IArtifact,
    PermissionHelper.IRequirePermission {

    private var _binding: FragmentInputMelodyBinding? = null
    private lateinit var event: ByteArray
    private var midihelper : MidiHelper = MidiHelper()

    private var mRecorder: MediaRecorder? = null
    private val mPlayer: MediaPlayer? = null

    // we can record up to 5 melodies for each experience

    private fun getRecNo(context: Context) = getCounter(context, this::class.java) % 5
    private fun melodyFileDir(context: Context) =
        "${getFileDir(context)}/audiorec${getRecNo(context)}.3gp"

    var mStartRecording = true
    var mStartPlaying = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentInputMelodyBinding.inflate(inflater, container, false)

        //mFileName1 = activity?.externalCacheDir?.absolutePath  <-- use if recordings should be only temporary

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.record.setOnClickListener {
            hasPermission { record() }
        }

        binding.play.setOnClickListener {

        }

        //region set instruments for spinner
        binding.instrumentSpinner.onItemSelectedListener = this
        var instruments = arrayOf("Acoustic Piano", "Bright Piano", "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord",
                                "Clavi", "Celesta", "Glockenspiel", "Musical box", "Vibraphone", "Marimba" , "Xylophone" , "Tubular Bell" , "Dulcimer", "Drawbar Organ" ,
                                "Percussive Organ" , "Rock Organ" , "Church organ" , "Reed organ" , "Accordion" , "Harmonica" , "Tango Accordion" , "Acoustic Guitar (nylon)" ,
                                "Acoustic Guitar (steel)" , "Electric Guitar (jazz)" , "Electric Guitar (clean)" , "Electric Guitar (muted)" , "Overdriven Guitar" ,
                                "Distortion Guitar" , "Guitar harmonics" , "Acoustic Bass" , "Electric Bass (finger)" , "Electric Bass (pick)" , "Fretless Bass" ,
                                "Slap Bass 1" , "Slap Bass 2" , "Synth Bass 1" , "Synth Bass 2" , "Violin" , "Viola" , "Cello" , "Double bass" , "Tremolo Strings" ,
                                "Pizzicato Strings" , "Orchestral Harp" , "Timpani" , "String Ensemble 1" , "String Ensemble 2" , "Synth Strings 1" , "Synth Strings 2" ,
                                "Voice Aahs" , "Voice Oohs" , "Synth Voice" , "Orchestra Hit" , "Trumpet" , "Trombone" , "Tuba" , "Muted Trumpet" , "French horn" ,
                                "Brass Section" , "Synth Brass 1" , "Synth Brass 2" , "Soprano Sax" , "Alto Sax" , "Tenor Sax" , "Baritone Sax" , "Oboe" , "English Horn" ,
                                "Bassoon" , "Clarinet" , "Piccolo" , "Flute" , "Recorder" , "Pan Flute" , "Blown Bottle" , "Shakuhachi" , "Whistle" , "Ocarina")

        val aa= activity?.let { ArrayAdapter(it.baseContext,android.R.layout.simple_spinner_item, instruments) }

        with(aa) {
            this?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.instrumentSpinner.adapter = aa
        }
        //endregion

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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        helperOnRequestPermissionResult(
            requestCode = requestCode,
            permission = permissions,
            grantResults = grantResults,
            hasPermission = { record() },
            null
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun record() {
        onRecord(mStartRecording)
        if (mStartRecording) {
            binding.record.setText("Finish")
            binding.record.setBackgroundResource(R.drawable.recordbutton)
        } else {
            binding.record.setText("Record")
            binding.record.setBackgroundResource(R.drawable.recordbutton)
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
        val toSave = melodyFileDir(requireContext()) + "TIME:" +getTimeStamp(requireContext())
        synchroniseArtifact(requireContext(), toSave, this::class.java, true)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)


        mRecorder?.setOutputFile(melodyFileDir(requireContext()))

        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.prepare()
        mRecorder?.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != null) {
            var noteNumber = 0
            when (v.id) {
                // 1st octave
                R.id.w1 -> noteNumber = 36 //C3
                R.id.b1 -> noteNumber = 37 //C#3
                R.id.w2 -> noteNumber = 38 //D3
                R.id.b2 -> noteNumber = 39 //D#3
                R.id.w3 -> noteNumber = 40 //E3
                R.id.w4 -> noteNumber = 41 //F3
                R.id.b3 -> noteNumber = 42 //F#3
                R.id.w5 -> noteNumber = 43 //G3
                R.id.b4 -> noteNumber = 44 //G#3
                R.id.w6 -> noteNumber = 45 //A3
                R.id.b5 -> noteNumber = 46 //A#3
                R.id.w7 -> noteNumber = 47 //B3

                // 2nd octave
                R.id.w8 -> noteNumber = 48 //C4
                R.id.b6 -> noteNumber = 49 //C#4
                R.id.w9 -> noteNumber = 50 //D4
                R.id.b7 -> noteNumber = 51 //D#4
                R.id.w10 -> noteNumber = 52 //E4
                R.id.w11 -> noteNumber = 53 //F4
                R.id.b8 -> noteNumber = 54 //F#4
                R.id.w12 -> noteNumber = 55 //G4
                R.id.b9 -> noteNumber = 56 //G#4
                R.id.w13 -> noteNumber = 57 //A4
                R.id.b10 -> noteNumber = 58 //A#4
                R.id.w14 -> noteNumber = 59 //B4

                // 3rd octave
                R.id.w15 -> noteNumber = 60 //C5
                R.id.b11 -> noteNumber = 61 //C#5
                R.id.w16 -> noteNumber = 62 //D5
                R.id.b12 -> noteNumber = 63 //D#5
                R.id.w17 -> noteNumber = 64 //E5
                R.id.w18 -> noteNumber = 65 //F5
                R.id.b13 -> noteNumber = 66 //F#5
                R.id.w19 -> noteNumber = 67 //G5
                R.id.b14 -> noteNumber = 68 //G#5
                R.id.w20 -> noteNumber = 69 //A5
                R.id.b15 -> noteNumber = 70 //A#5
                R.id.w21 -> noteNumber = 71 //B5

                // 4th octave
                R.id.w22 -> noteNumber = 72 //C6
                R.id.b16 -> noteNumber = 73 //C#6
                R.id.w23 -> noteNumber = 74 //D6
                R.id.b17 -> noteNumber = 75 //D#6
                R.id.w24 -> noteNumber = 76 //E6
                R.id.w25 -> noteNumber = 77 //F6
                R.id.b18 -> noteNumber = 78 //F#6
                R.id.w26 -> noteNumber = 79 //G6
                R.id.b19 -> noteNumber = 80 //G#6
                R.id.w27 -> noteNumber = 81 //A6
                R.id.b20 -> noteNumber = 82 //A#6
                R.id.w28 -> noteNumber = 83 //B6

                // 5th octave
                R.id.w29 -> noteNumber = 84 //C7
                R.id.b21 -> noteNumber = 85 //C#7
                R.id.w30 -> noteNumber = 86 //D7
                R.id.b22 -> noteNumber = 87 //D#7
                R.id.w31 -> noteNumber = 88 //E7
                R.id.w32 -> noteNumber = 89 //F7
                R.id.b23 -> noteNumber = 90 //F#7
                R.id.w33 -> noteNumber = 91 //G7
                R.id.b24 -> noteNumber = 92 //G#7
                R.id.w34 -> noteNumber = 93 //A7
                R.id.b25 -> noteNumber = 94 //A#7
                R.id.w35 -> noteNumber = 95 //B7
            }

            if (event != null) {
                if (event.action == MotionEvent.ACTION_UP) {
                    stopNote(noteNumber)
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    playNote(noteNumber)
                }
            }
        }
        return false
    }

    private fun stopNote(noteNumber: Int) {
            // Construct a note OFF message for the note at minimum velocity on channel 1:
            event = ByteArray(3)
            event[0] = (0x80 or 0x00).toByte() // 0x80 = note Off, 0x00 = channel 1
            event[1] = noteNumber.toByte()
            event[2] = 0x00.toByte() // 0x00 = the minimum velocity (0)

            // Send the MIDI event to the synthesizer.
            midihelper.write(event)
    }

    private fun playNote(noteNumber: Int) {

        // Construct a note ON message for the note at maximum velocity on channel 1:
        event = ByteArray(3)
        event[0] = (0x90 or 0x00).toByte() // 0x90 = note On, 0x00 = channel 1
        event[1] = noteNumber.toByte()
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
        midihelper.selectInstrument(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        midihelper.selectInstrument(1)
    }

    override fun mainPermission() = Manifest.permission.RECORD_AUDIO

    override fun fragment() = this

    override fun fragmentCode() = PermissionHelper.FRAGMENT_INPUT_MELODY_CODE

    override fun requiredPermissions(): Array<out String> =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

}