package com.example.cobex.timelineview

import android.view.View
import kotlinx.android.synthetic.main.timeline_item_picture_big.view.*
import kotlinx.android.synthetic.main.timeline_item_picture_small.view.*
import kotlinx.android.synthetic.main.timeline_item_recorded_activity.view.*

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

    enum class Types(val card: TimelineCards, val color: Int) {
        RECORD_ACTIVITY(RecordedActivity, android.R.color.holo_blue_dark),
        IMAGE(Image, android.R.color.holo_orange_light),
        BIG_IMAGE(BigImage, android.R.color.holo_orange_light)
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