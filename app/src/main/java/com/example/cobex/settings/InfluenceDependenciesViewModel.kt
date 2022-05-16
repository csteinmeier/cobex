package com.example.cobex.settings

import android.content.Context
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToString

class InfluenceDependenciesViewModel(context: Context) {


    //All Components of Melody Pie Chart
    private val melodyArtifacts = listOf(
        Artifact.InputMelody,
        Artifact.CaptureSound,
        Artifact.InputKeywords,
        Artifact.CapturePicture
    )

    //All Components of Rhythm Pie Chart
    private val rhythmArtifacts = listOf(
        Artifact.CaptureAction,
        Artifact.CreateRhythm
    )

    // Manager of Melody pie chart
    private val melodyPieChart = PieChartManager(context, melodyArtifacts)

    // Checkbox for Visual Experience Checkbox
    private val visualCheckBox = PieCheckBoxManager()

    // Manager of Rhythm Pie Chart
    private val rhythmPieChart = PieChartManager(context, rhythmArtifacts)

    // Concrete pie chart of melody
    private val melodyModel = InfluenceDependenciesModel.ConcretePieChart(
        pieChartManager = melodyPieChart,
        context = context,
        title = R.string.depHeaderMelodyArtifacts.resourceToString(context)
    )

    // Concrete pie chart of rhythm
    private val rhythmModel = InfluenceDependenciesModel.ConcretePieChart(
        pieChartManager = rhythmPieChart,
        context = context,
        title = R.string.depHeaderRhythmArtifacts.resourceToString(context)
    )


    // Each component of RecylerView, order in which it is to be drawn
    val componentList = mutableListOf<InfluenceDependenciesModel>().apply {
        /*** Melody PieChart */
        add(melodyModel)


        val meanValueMelody = 100 / (melodyArtifacts.size + 1)

        addAll(listOf(
            InfluenceDependenciesModel.PieChartSeekbar(
                melodyPieChart, null,  Artifact.InputMelody, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbar(
                melodyPieChart, null,  Artifact.CaptureSound, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbar(
                melodyPieChart, null,  Artifact.InputKeywords, meanValueMelody,
            ),
            InfluenceDependenciesModel.PieChartSeekbar(
                melodyPieChart, visualCheckBox,  Artifact.CapturePicture, meanValueMelody,
            )
        ))

        add(InfluenceDependenciesModel.PieTickBox(
            melodyPieChart, R.string.checkBoxOptionVisualArtifacts, visualCheckBox, Artifact.CapturePicture
        ))

        /*** Rhythm PieChart*/
        add(rhythmModel)

        addAll(rhythmArtifacts.map {
            InfluenceDependenciesModel.PieChartSeekbar(
                rhythmPieChart, null ,it,100 / rhythmArtifacts.size
            )
        })

        add(InfluenceDependenciesModel.PieChartDivision(
            Artifact.AI, Artifact.Human
        ))

    }

}