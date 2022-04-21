package com.example.cobex

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), CompositionArtifact.IArtifact {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var artifact: CompositionArtifact

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artifact = CompositionArtifact.getInstance(requireContext())


        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.buttonCreateNew.setOnClickListener {
            if (isProjectStarted(requireContext())) {
                alertDeleteOldInstance()
            } else {
                markAsProjectStarted(requireContext())
                findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)
            }
        }


        binding.buttonContinue.visibility =
            if (isProjectStarted(requireContext()))
                View.VISIBLE
            else
                View.GONE

        binding.buttonContinue.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)


            /*
            CreateNew.clickedKeyword = getCounter(requireContext(), InputKeyword::class.java)
            CreateNew.capturedPicture = getCounter(requireContext(), CapturePicture::class.java)

             */
        }
    }

    private fun alertDeleteOldInstance(): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(context?.getString(R.string.dialog_delete_old_project_title))
            .setMessage(context?.getString(R.string.dialog_delete_old_project_msg))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ -> deleteOldInstanceListener() }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun deleteOldInstanceListener() {
        clearSavedInstance(requireContext())
        markAsProjectStarted(requireContext())
        findNavController().navigate(R.id.action_SecondFragment_to_CreateNew)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}