package com.example.cobex.timelineview

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * [ItemTouchHelper.Callback] Class
 *
 * Used for some Interactions with [TimelineViewHolder]
 */
class TimelineItemHelper(
    val context: Context,
    private val helperAdapterTimeline: TimelineItemHelperAdapter
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false
    override fun isItemViewSwipeEnabled() = true

    override fun clearView(recycler: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recycler, viewHolder)
        resetColorMartialView(viewHolder.itemView)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            setTimelineCards(viewHolder?.itemView!!)
        }
    }

    private fun resetColorMartialView(item: View) {
        val touchedCard = TimelineCards.getActiveCard(item)
        val oldColor = touchedCard?.color?.let { ContextCompat.getColor(context, it) }
        if (oldColor != null) {
            setColour(touchedCard.card.getConcreteMartialCard(item)!!, oldColor)
        }
    }

    private fun setTimelineCards(item: View) {
        val touchedCard = TimelineCards.getActiveCard(item)
        setColour(touchedCard?.card?.getConcreteMartialCard(item)!!, Color.LTGRAY)
    }

    private fun setColour(view: View, color: Int) {
        view.setBackgroundColor(color)
    }

    override fun getMovementFlags(recycler: RecyclerView, viewHolder: RecyclerView.ViewHolder)
            : Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recycler: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        helperAdapterTimeline.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        helperAdapterTimeline.onItemSwiped(viewHolder.adapterPosition)
    }
}
