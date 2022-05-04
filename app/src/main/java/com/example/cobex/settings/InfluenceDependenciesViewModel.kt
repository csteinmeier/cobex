package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToString

class InfluenceDependenciesViewModel(context: Context) {


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

    private val visualCheckBox = PieCheckBoxManager()

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


        val meanValueMelody = 100 / (melodyArtifacts.size + 1)

        addAll(listOf(
            InfluenceDependenciesModel.PieChartSeekbarModel(
                melodyPieChart, null,  Artifact.InputMelody, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbarModel(
                melodyPieChart, null,  Artifact.CaptureSound, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbarModel(
                melodyPieChart, null,  Artifact.InputKeywords, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbarModel(
                melodyPieChart, visualCheckBox,  Artifact.CapturePicture, meanValueMelody,
            )
        ))

        add(InfluenceDependenciesModel.PieTickBoxModel(
            melodyPieChart, R.string.checkBoxOptionVisualArtifacts, visualCheckBox, Artifact.CapturePicture
        ))

        /*** Rhythm PieChart*/
        add(rhythmModel)

        addAll(rhythmArtifacts.map {
            InfluenceDependenciesModel.PieChartSeekbarModel(
                rhythmPieChart, null ,it,100 / rhythmArtifacts.size
            )
        })

    }

}