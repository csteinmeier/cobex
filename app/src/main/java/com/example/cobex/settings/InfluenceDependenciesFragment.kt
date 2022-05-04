package com.example.cobex.settings

import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.databinding.FragmentInfluenceDependenciesBinding
import com.example.cobex.helper.Extensions.navigateOnClick
import com.example.cobex.helper.Extensions.resourceToString
import kotlinx.coroutines.android.awaitFrame


class InfluenceDependenciesFragment : Fragment() {

    private var _binding: FragmentInfluenceDependenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var model : InfluenceDependenciesViewModel
    lateinit var adapter: InfluenceDependenciesAdapter



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
            buttonBack.navigateOnClick(R.id.timeLineView)

            model = InfluenceDependenciesViewModel(requireContext())

            adapter = InfluenceDependenciesAdapter(model.componentList)
            recyclerInfluenceDependencies.layoutManager = LinearLayoutManager(context)
            recyclerInfluenceDependencies.adapter = adapter
        }
    }

}