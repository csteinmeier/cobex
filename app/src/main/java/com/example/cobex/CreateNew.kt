package com.example.cobex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}