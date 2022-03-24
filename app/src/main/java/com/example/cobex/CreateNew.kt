package com.example.cobex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentCreateNewBinding

/**
 * A simple [Fragment] subclass.
 */
class CreateNew : Fragment() {

    private var _binding: FragmentCreateNewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object{
        var clickedKeyword = 0
        var takenPicture = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCreateNewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBackCreate.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_SecondFragment)
        }

        binding.buttonInputMic.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_captureSound)
        }

        binding.buttonInputMelody.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_inputMelody)
        }

        binding.buttonKeyword.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_inputKeyword)
        }

        binding.buttonInputCam.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_InputPicture)
        }

        binding.buttonInputActivity.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_captureAction)
        }

        binding.buttonInputRhythm.setOnClickListener {
            findNavController().navigate(R.id.action_CreateNew_to_createRhythm)
        }


        setCounter(binding.counterKeywordsFeeling, clickedKeyword)
        setCounter(binding.counterPictures, takenPicture)

    }

    private fun setCounter(textView: TextView, counter: Int){
        if(counter > 0){
            textView.apply {
                visibility = View.VISIBLE
                text = counter.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}