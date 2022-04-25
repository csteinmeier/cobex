package com.example.cobex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentInfluenceDependenciesBinding


class InfluenceDependencies : Fragment() {

    private var _binding: FragmentInfluenceDependenciesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfluenceDependenciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.timeLineView)
            }
        }
    }


}