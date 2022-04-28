package com.example.cobex.settings

import android.content.Context
import android.os.Bundle
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


class InfluenceDependenciesFragment : Fragment() {

    private var _binding: FragmentInfluenceDependenciesBinding? = null
    private val binding get() = _binding!!

    private val melodyArtifacts = listOf(
        Artifact.InputMelody,
        Artifact.CaptureSound,
        Artifact.InputKeywords,
        Artifact.CapturePicture
    )

    private val rhythmArtifacts = listOf(
        Artifact.CaptureAction,
        Artifact.CreateRhythm
    )

    private fun melodyPieChart(context: Context) = InfluenceDependenciesModel.ConcretePieChartModel(
        context,
        melodyArtifacts,
        R.string.depHeaderMelodyArtifacts.resourceToString(requireContext())
    )

    private fun rhythmPieChart(context: Context) = InfluenceDependenciesModel.ConcretePieChartModel(
        context,
        rhythmArtifacts,
        R.string.depHeaderRhythmArtifacts.resourceToString(requireContext())
    )

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

            val componentList = mutableListOf<InfluenceDependenciesModel>().apply {
                /*** Melody PieChart */
                add(melodyPieChart(requireContext()))

                addAll(melodyArtifacts.map { InfluenceDependenciesModel.PieChartSeekbarModel(it) })

                /*** Rhythm PieChart*/
                add(rhythmPieChart(requireContext()))

                addAll(rhythmArtifacts.map { InfluenceDependenciesModel.PieChartSeekbarModel(it) })
            }

            val adapter = InfluenceDependenciesAdapter(componentList)
            recyclerInfluenceDependencies.layoutManager = LinearLayoutManager(context)
            recyclerInfluenceDependencies.adapter = adapter
        }


    }


}