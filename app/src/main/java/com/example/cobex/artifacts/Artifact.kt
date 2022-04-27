package com.example.cobex.artifacts

import com.example.cobex.R
import com.example.cobex.timelineview.TimelineCards

sealed class Artifact(
    val ordinal : Int,
    val displayString: Int,
    val color: Int
) {

    object CapturePicture : Artifact(0, R.string.depCapturePicture, android.R.color.holo_blue_dark)
    object CaptureAction : Artifact(1, R.string.depCaptureAction, android.R.color.holo_orange_light)
    object CaptureSound : Artifact(2, R.string.depCaptureSound, android.R.color.holo_red_light)
    object InputMelody : Artifact(3, R.string.depInputMelody, android.R.color.holo_green_light)
    object InputKeywords : Artifact(4, R.string.depInputKeyword, android.R.color.holo_purple)

    companion object {
        fun getAllArtifactTypes() = listOf(
            CapturePicture,
            CaptureAction,
            CaptureSound,
            InputMelody,
            InputKeywords
        )
    }

}
