package com.example.tinetest1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.tinetest1.databinding.FragmentInputMelodyBinding

/**
 * A simple [Fragment] subclass.
 * Use the [InputMelody.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputMelody : Fragment(), View.OnTouchListener {

    private var _binding: FragmentInputMelodyBinding? = null

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

        binding.w1.setOnTouchListener(this)
        binding.w2.setOnTouchListener(this)
        binding.w3.setOnTouchListener(this)
        binding.w4.setOnTouchListener(this)
        binding.w5.setOnTouchListener(this)
        binding.w6.setOnTouchListener(this)
        binding.w7.setOnTouchListener(this)
        binding.w8.setOnTouchListener(this)
        binding.b1.setOnTouchListener(this)
        binding.b2.setOnTouchListener(this)
        binding.b3.setOnTouchListener(this)
        binding.b4.setOnTouchListener(this)
        binding.b5.setOnTouchListener(this)
        binding.b6.setOnTouchListener(this)
        //todo: implement this...

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v != null) {
            var noteNumber = 0
            when (v.id) {
                R.id.w1 -> noteNumber = 36
                R.id.b1 -> noteNumber = 37
                R.id.w2 -> noteNumber = 38
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

    private fun stopNote(noteNumber: Any, b: Boolean) {

    }

    private fun playNote(noteNumber: Any) {

    }

}