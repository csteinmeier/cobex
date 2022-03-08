package com.example.cobex

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isOldInstanceAvailable = CompositionArtifact.isSavedPreferenceAvailable(this.requireActivity())


        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.buttonCreateNew.setOnClickListener {
            if(isOldInstanceAvailable) alertDeleteOldInstance() else
                findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)
        }


        binding.buttonContinue.visibility = if(isOldInstanceAvailable) View.VISIBLE else View.GONE

        binding.buttonContinue.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)
            CompositionArtifact.clickedKeywords =
                CompositionArtifact.getSavedAmountOfKeywords(requireActivity(),
                CompositionArtifact.PreferenceKeywords.KEYWORD_AMOUNT)
            CompositionArtifact.capturedPicture =
                CompositionArtifact.getSavedAmountOfKeywords(requireActivity(),
                CompositionArtifact.PreferenceKeywords.PICTURE_AMOUNT)
        }
    }

    private fun alertDeleteOldInstance(): AlertDialog{
        return AlertDialog.Builder(context)
            .setTitle("Delete previous input")
            .setMessage("Do you really want to start a new composition? \n" +
                    "The old one will then be lost")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes) { _, _ -> deleteOldInstanceListener() }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteOldInstanceListener() {
        CompositionArtifact.clearSavedPreference(this.requireActivity())
        CompositionArtifact.clickedKeywords = 0
        CompositionArtifact.capturedPicture = 0
        CompositionArtifact.deleteAllTakenPictures(requireActivity())
        findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}