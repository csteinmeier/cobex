package com.example.cobex.timelineview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cobex.CompositionArtifact
import com.example.cobex.R
import com.example.cobex.TimeHelper
import com.example.cobex.databinding.FragmentTimelineViewBinding

/**
 * Concrete Fragment of [R.layout.fragment_timeline_view]
 */
class TimelineViewFragment : Fragment(), CompositionArtifact.IArtifact {

    private var _binding: FragmentTimelineViewBinding? = null
    private val binding get() = _binding!!


    private lateinit var viewModel: TimelineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            setTitle(timelineLineStartTitle, requireContext())

            viewModel = TimelineViewModel(requireContext(), recyclerTimeline)

            timelineButtonChangeState.setOnClickListener {
                viewModel.updateStateChange()
                setTitle(timelineLineStartTitle, requireContext())
            }
            buttonBack.setOnClickListener {
                findNavController().navigate(R.id.CreateNew)
            }
            buttonNext.setOnClickListener {
                findNavController().navigate(R.id.influenceDependencies)
            }
        }
    }


    private fun timeStartedString(context: Context) = with(context) {
        getProjectStartTime(context)
            ?.let { TimeHelper.fromCreatedTillNowEasyString(this, it) }
    }

    private fun setTitle(textView: TextView, context: Context) =
        run { textView.text = getTimelineTitle(context) }


    private fun getTimelineTitle(context: Context): String = with(context) {
        when (TimelineStateType.actualTimelineState) {
            TimelineStateType.CompositionArtifacts ->
                getTimelineTitleTime(context) + "\n" + getString(R.string.timelineProject)
            TimelineStateType.StoredArtifacts ->
                getTimelineTitleTime(context) + "\n" + getString(R.string.timelineStored)
        }
    }

    private fun getTimelineTitleTime(context: Context): String = with(context) {
        val projectStartString = getString(R.string.timelineStart)

        projectStartString + timeStartedString(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (TimelineStateType.actualTimelineState == TimelineStateType.CompositionArtifacts)
            viewModel.saveOrderedItems()
        markAsSavedIfNotMarkedAsSaved(requireContext(), this::class.java)
        _binding = null
    }
}
