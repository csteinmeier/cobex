package com.example.cobex.timelineview

import android.view.View
import com.example.cobex.artifacts.Artifact
import kotlinx.android.synthetic.main.timeline_item_picture_big.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_small.view.*
import kotlinx.android.synthetic.main.timeline_item_recorded_activity.view.*
import kotlinx.android.synthetic.main.timeline_item_capture_sound.view.*
import kotlinx.android.synthetic.main.timeline_item_input_melody.view.*
import kotlinx.android.synthetic.main.timeline_item_keyword.view.*

/**
 * Simple Wrapper Class for the MartialCards in Layouts
 * Is used to determine which Card is touched
 *
 */
sealed class TimelineCards {

    private fun isCardActive(item: View?) = getConcreteMartialCard(item) != null

    abstract fun getConcreteMartialCard(item: View?): View?

    private object RecordedActivity : TimelineCards() {

        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_record_activity }
    }

    private object Image : TimelineCards() {

        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_picture_small }
    }

    private object BigImage : TimelineCards() {
        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_picture_big }
    }

    private object CaptureSound : TimelineCards(){
        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_capture_sound }
    }

    private object InputMelody : TimelineCards(){
        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_input_melody }
    }

    private object Keyword : TimelineCards() {
        override fun getConcreteMartialCard(item: View?): View? =
            with(item) { this?.timeline_martial_view_keyword }
    }

    enum class Types(val card: TimelineCards, val color: Int) {
        CAPTURE_ACTION(RecordedActivity, Artifact.CaptureAction.color),
        CAPTURE_PICTURE(Image, Artifact.CapturePicture.color),
        BIG_IMAGE(BigImage, Artifact.CapturePicture.color),
        CAPTURE_SOUND(CaptureSound, Artifact.CaptureSound.color),
        INPUT_MELODY(InputMelody, Artifact.InputMelody.color),
        INPUT_KEYWORD(Keyword, Artifact.InputKeywords.color)
    }

    companion object {
        /**
         * @param item the current View
         *
         * @return Concrete TimelineCard like
         * [timeline_martial_view_picture_big]
         * [timeline_martial_view_record_activity]
         *
         * Used to identify the right type of card.
         *
         * @sample TimelineItemHelper.resetColorMartialView
         *
         */
        fun getActiveCard(item: View?) = Types.values().find { it.card.isCardActive(item) }
    }
}