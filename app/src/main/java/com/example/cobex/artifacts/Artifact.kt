package com.example.cobex.artifacts

import com.example.cobex.R
import com.example.cobex.timelineview.TimelineCards

sealed class Artifact(
    val ordinal : Int,
    val displayString: Int,
    val color: Int,
    val symbol: Int
) {

    object CapturePicture
        : Artifact(0, R.string.depCapturePicture, android.R.color.holo_blue_dark, R.drawable.ic_lense_24)
    object CaptureAction
        : Artifact(1, R.string.depCaptureAction, android.R.color.holo_orange_light, R.drawable.ic_directions_run_24)
    object CaptureSound
        : Artifact(2, R.string.depCaptureSound, android.R.color.holo_red_light, R.drawable.ic_music_note_24)
    object InputMelody
        : Artifact(3, R.string.depInputMelody, android.R.color.holo_green_light, R.drawable.ic_nature_24)
    object InputKeywords
        : Artifact(4, R.string.depInputKeyword, android.R.color.holo_purple, R.drawable.ic_arrow_circle_24)
    object CreateRhythm
        : Artifact(5, R.string.depCreateRhythm, android.R.color.holo_green_dark, R.drawable.play)
    object AI
        :Artifact(6, R.string.depAI, android.R.color.holo_red_dark, R.drawable.ic_developer_board_24)
    object Human
        :Artifact(7, R.string.depUser, android.R.color.holo_blue_dark, R.drawable.ic_person_24)


    companion object {
        fun getAllArtifactTypes() = listOf(
            CapturePicture,
            CaptureAction,
            CaptureSound,
            InputMelody,
            InputKeywords,
            CreateRhythm,
            AI,
            Human
        )
    }

}
