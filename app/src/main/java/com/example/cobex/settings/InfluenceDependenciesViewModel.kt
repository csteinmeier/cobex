package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToString

class InfluenceDependenciesViewModel(context: Context) {

    companion object {
        val pieChartManagerList = mutableListOf<PieChartManager>()
    }

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

    private val melodyPieChart = PieChartManager(context, melodyArtifacts)

    private val rhythmPieChart = PieChartManager(context, rhythmArtifacts)

    private val melodyModel = InfluenceDependenciesModel.ConcretePieChartModel(
        pieChartManager = melodyPieChart,
        context = context,
        title = R.string.depHeaderMelodyArtifacts.resourceToString(context)
    )

    private val rhythmModel = InfluenceDependenciesModel.ConcretePieChartModel(
        pieChartManager = rhythmPieChart,
        context = context,
        title = R.string.depHeaderRhythmArtifacts.resourceToString(context)
    )


    val componentList = mutableListOf<InfluenceDependenciesModel>().apply {
        /*** Melody PieChart */
        add(melodyModel)

        addAll(melodyArtifacts.map {
            InfluenceDependenciesModel.PieChartSeekbarModel(
                melodyPieChart, it, 100 / melodyArtifacts.size,
            )
        })

        /*** Rhythm PieChart*/
        add(rhythmModel)

        addAll(rhythmArtifacts.map {
            InfluenceDependenciesModel.PieChartSeekbarModel(
                rhythmPieChart ,it,100 / rhythmArtifacts.size
            )
        })

    }

}