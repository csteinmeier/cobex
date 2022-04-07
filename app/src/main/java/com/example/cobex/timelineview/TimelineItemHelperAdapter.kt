package com.example.cobex.timelineview

/**
 * * Interface implemented by [TimelineAdapter] and [TimelineItemHelper].
 *
 * * DRAG AND DROP:  [TimelineItemHelper.onMove] in [TimelineAdapter.onItemMove]
 *   about drag and drop actions
 *
 * * SWIPE: [TimelineItemHelper.onSwiped] in[TimelineAdapter.onItemSwiped]
 *
 */
interface TimelineItemHelperAdapter {
    /**
     * @param initialPosition position when the Item is dragged
     * @param endPosition position when the Item is dropped
     */
    fun onItemMove(initialPosition: Int, endPosition: Int)

    /**
     * @param position of the item that is swiped
     */
    fun onItemSwiped(position: Int)
}