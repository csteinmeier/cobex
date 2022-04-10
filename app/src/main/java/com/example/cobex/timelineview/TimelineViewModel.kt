package com.example.cobex.timelineview

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.CompositionArtifact

/**
 *
 */
class TimelineViewModel(
    val context: Context,
    recyclerView: RecyclerView
) : CompositionArtifact.IArtifact {


    var adapter: TimelineAdapter =
        TimelineAdapter(
            StoredToItemHelper.getList(
                TimelineStateType.actualTimelineState, context).toMutableList(), this)

    init {
        val itemTouchHelper = TimelineItemHelper(context, adapter)
        val touchHelper = ItemTouchHelper(itemTouchHelper)
        adapter.itemTouchHelper = touchHelper
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun updateStateChange() {
        saveOrderedItems()
        TimelineStateType.changeState()
        val artType = TimelineStateType.actualTimelineState
        adapter.timelineItems = StoredToItemHelper.getList(artType, context).toMutableList()
        adapter.notifyDataSetChanged()
    }

    /**
     * Will save all Timeline Objects in one String Set
     *
     * Form:
     * [TimelineObject.Type]**!**[TimelineObject.id]**!**[TimelineObject.pos]
     *
     * + Substring before the first **!** for the [TimelineObject.Type]
     * + Substring after the last **!** for the [TimelineObject.pos],
     * will be -1 if never saved with this method
     * + Cut after the first **!**  and before the last **!**
     * to get the normally saved value in [CompositionArtifact]
     */
    fun saveOrderedItems() {
        if (TimelineStateType.actualTimelineState == TimelineStateType.CompositionArtifacts) {
            putStringSet(context, this.javaClass,
                adapter.timelineItems
                    .mapIndexed() { index, timelineObject ->
                        "${timelineObject.type}!" + timelineObject.id + "!$index" }.toSet())
        }
    }

    /**
     * Designed to change a Item in a TimelineView with two active Recyclerview
     *
     * @param item which change the list
     */
    fun exchangeItem(item: TimelineObject) {
        val cList = currentArtifactList(item.type)!!
        Log.i("Current List before change", cList.size.toString())
        cList.remove(cList.find { item.id == it })
        putArtifactList(item.type, TimelineStateType.actualTimelineState, cList.toSet())
        Log.i("Current List after change", cList.size.toString())

        val oList = oppositeArtefactList(item.type)!!
        Log.i("Opposite List before change", oList.size.toString())
        oList.add(item.id)
        putArtifactList(item.type, TimelineStateType.getOppositeArtefactType(), oList.toSet())
        Log.i("Opposite List after change", oList.size.toString())
    }

    /**
     * Artifact list of the currently shown RecyclerView
     *
     * *Designed for 2 active Artifact List*
     */
    private fun currentArtifactList(type: TimelineObject.Type) =
        getArtifactList(type, TimelineStateType.actualTimelineState)

    /**
     * Artifact list of the not shown RecyclerView
     *
     * *Designed for 2 active Artifact List*
     */
    private fun oppositeArtefactList(type: TimelineObject.Type) =
        getArtifactList(type, TimelineStateType.getOppositeArtefactType())

    /**
     * Used to get a matching list via [CompositionArtifact]
     */
    private fun getArtifactList(
        type: TimelineObject.Type, timelineStateType: TimelineStateType
    ) = when (type) {

        TimelineObject.Type.IMAGE_ITEM ->
            timelineStateType.getCapturedImagesStringSet(context)?.toMutableList()

        TimelineObject.Type.RECORD_ITEM ->
            timelineStateType.getRecordedActivitiesStringSet(context)?.toMutableList()

        TimelineObject.Type.BIG_IMAGE_ITEM ->
            timelineStateType.getCapturedImagesStringSet(context)?.toMutableList()

        TimelineObject.Type.CAPTURE_SOUND ->
            timelineStateType.getCaptureSoundStringSet(context)?.toMutableList()

        TimelineObject.Type.INPUT_MELODY ->
            timelineStateType.getInputMelodiesStringSet(context)?.toMutableList()

        TimelineObject.Type.KEYWORD ->
            timelineStateType.getClickedKeywordStringSet(context)?.toMutableList()
    }

    /**
     * Used to put a list via [CompositionArtifact]
     */
    private fun putArtifactList(
        type: TimelineObject.Type,
        timelineStateType: TimelineStateType,
        set: Set<String>
    ) = when (type) {

        TimelineObject.Type.IMAGE_ITEM ->
            timelineStateType.putCapturedImageStringSet(context, set)

        TimelineObject.Type.RECORD_ITEM ->
            timelineStateType.putRecordedActivitiesStringSet(context, set)

        TimelineObject.Type.BIG_IMAGE_ITEM ->
            timelineStateType.putCapturedImageStringSet(context, set)

        TimelineObject.Type.INPUT_MELODY ->
            timelineStateType.putInputMelodiesStringSet(context, set)

        TimelineObject.Type.CAPTURE_SOUND ->
            timelineStateType.putCaptureSoundStringSet(context, set)

        TimelineObject.Type.KEYWORD ->
            timelineStateType.putClickedKeywordStringSet(context, set)
    }

    /**
     * Helper class to convert a String to a [TimelineObject] or a StringSet to a list of
     * [TimelineObject]
     *
     * Public available is the function [StoredToItemHelper.getList]
     * This list is either a cobbled together list of all the [TimelineObject],
     * which should be able to be sorted and stored in via [CompositionArtifact],
     * or a list already sorted by the user consisting of [TimelineObject],
     * but also stored via [CompositionArtifact].
     * If new items are added to a list sorted by the user,
     * they are added at the end, but the sorting remains.
     * Sorted list exists only in [TimelineStateType.CompositionArtifacts] not in
     * [TimelineStateType.StoredArtifacts]
     *
     */
    private sealed class StoredToItemHelper {

        /**
         * @return List of [TimelineObject] converted from a Set of Strings, when available
         * in the Project and the specific [TimelineStateType]
         */
        abstract fun getList(timelineStateType: TimelineStateType, context: Context):
                List<TimelineObject>?

        /**
         * @return Converted [TimelineObject] from a String
         */
        abstract fun storedToItem(savedString: String, position: Int? = -1): TimelineObject


        /**
         * @return List of [TimelineObject] from a given StringSet
         */
        abstract fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject>


        private fun isStoredSetAvailable(
            timelineStateType: TimelineStateType, helper: StoredToItemHelper, context: Context
        ): Boolean = helper.getList(timelineStateType, context) != null


        private object Image : StoredToItemHelper() {

            override fun getList(timelineStateType: TimelineStateType, context: Context):
                    List<TimelineObject>? =
                timelineStateType.getCapturedImagesStringSet(context)
                    ?.let { storedSetToItemList(it) }

            override fun storedToItem(
                savedString: String, position: Int?
            ) = TimelineObject.ImageItem(
                id = savedString,
                createdTimeAsString = savedString.substringAfter("app_images/"),
                imgSrc = savedString,
                pos = position
            )

            override fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject> =
                storedSet.map { storedToItem(it) }


        }

        private object RecordActivity : StoredToItemHelper() {

            override fun getList(timelineStateType: TimelineStateType, context: Context):
                    List<TimelineObject>? =
                timelineStateType.getRecordedActivitiesStringSet(context)
                    ?.let { storedSetToItemList(it) }


            override fun storedToItem(
                savedString: String, position: Int?
            ) =
                TimelineObject.RecordItem(
                    id = savedString,
                    createdTimeAsString = savedString.substringBeforeLast(':'),
                    detectedActivity = savedString.substringAfterLast(':'),
                    pos = position
                )

            override fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject> =
                storedSet.map { storedToItem(it) }
        }

        private object CaptureSound : StoredToItemHelper() {

            override fun getList(
                timelineStateType: TimelineStateType,
                context: Context
            ): List<TimelineObject>? =
                timelineStateType.getCaptureSoundStringSet(context)
                    ?.let { storedSetToItemList(it) }


            override fun storedToItem(savedString: String, position: Int?)=
                TimelineObject.CaptureSoundItem(
                    id = savedString,
                    createdTimeAsString = savedString.substringAfter("TIME:"),
                    mRecord = savedString.substringBefore("TIME:"),
                    pos = position
                )

            override fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject> =
                storedSet.map { storedToItem(it) }

        }

        private object InputMelody : StoredToItemHelper() {

            override fun getList(
                timelineStateType: TimelineStateType,
                context: Context
            ): List<TimelineObject>? =
                timelineStateType.getInputMelodiesStringSet(context)
                    ?.let { storedSetToItemList(it) }

            override fun storedToItem(savedString: String, position: Int?)=
                TimelineObject.InputMelodyItem(
                    id = savedString,
                    createdTimeAsString = savedString.substringAfter("TIME:"),
                    mRecord = savedString.substringBefore("TIME:"),
                    pos = position
                )


            override fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject> =
                storedSet.map { storedToItem(it) }
        }

        private object Keyword : StoredToItemHelper() {

            override fun getList(
                timelineStateType: TimelineStateType,
                context: Context
            ): List<TimelineObject>? =
                timelineStateType.getClickedKeywordStringSet(context)
                    ?.let { storedSetToItemList(it) }

            override fun storedToItem(savedString: String, position: Int?)=
                TimelineObject.KeywordItem(
                    id = savedString,
                    createdTimeAsString = savedString.substringAfterLast("#"),
                    keywords = savedString.substringAfter("#"),
                    keywordAmount = getAmountOfKeywords(savedString),
                    pos = position
                )

            private fun getAmountOfKeywords(savedString: String) =
                savedString.count{ it == ',' }

            override fun storedSetToItemList(storedSet: Set<String>): List<TimelineObject> =
                storedSet.map { storedToItem(it) }
        }

        /**
         * Special class for the [TimelineObject]
         * which have already been put into a certain order by the user.
         */
        private object ModifiedList : CompositionArtifact.IArtifact {

            fun isModifiedListAvailable(context: Context) = storedList(context)?.isNotEmpty()

            private fun storedList(context: Context) =
                getStringSet(context, TimelineViewModel::class.java)

            /**
             * @sample saveOrderedItems
             */
            private fun stringFromSavedValue(savedString: String) =
                savedString
                    .substringAfter("!")
                    .substringBeforeLast("!")

            /**
             * @sample saveOrderedItems
             */
            private fun positionFromSavedValue(savedString: String) =
                savedString.substringAfterLast("!").toInt()

            /**
             * @sample saveOrderedItems
             */
            private fun typeFromSavedValue(savedString: String) =
                TimelineObject.Type.values()
                    .find { it.name == savedString.substringBefore("!") }


            /**
             * Will use the the specific [StoredToItemHelper] class to convert a String to a
             * [TimelineObject]
             */
            private fun storedToItem(savedString: String): TimelineObject {
                val stringValue = stringFromSavedValue(savedString)
                val position = positionFromSavedValue(savedString)
                Log.e("sdasd", stringValue.substringBefore("!"))

                return when (typeFromSavedValue(savedString)!!) {

                    TimelineObject.Type.IMAGE_ITEM -> Image.storedToItem(stringValue, position)

                    TimelineObject.Type.BIG_IMAGE_ITEM -> Image.storedToItem(stringValue, position)

                    TimelineObject.Type.RECORD_ITEM ->
                        RecordActivity.storedToItem(stringValue, position)

                    TimelineObject.Type.CAPTURE_SOUND ->
                        CaptureSound.storedToItem(stringValue, position)

                    TimelineObject.Type.INPUT_MELODY ->
                        InputMelody.storedToItem(stringValue, position)

                    TimelineObject.Type.KEYWORD ->
                        Keyword.storedToItem(stringValue, position)

                }
            }

            /**
             * @return by user sorted list of [TimelineObject]
             */
            fun getSortedList(context: Context): List<TimelineObject> =
                storedList(context)?.map { storedToItem(it) }?.sortedBy { it.pos }!!
        }


        companion object {

            /***************************List of All Helpers ***************************************/

            private fun getAllDefaultHelpers() =
                listOf(RecordActivity, Image, CaptureSound, InputMelody, Keyword)

            /**
             *
             * @param timelineStateType the StateType that is currently active
             *
             * @return
             *
             * + Case 1: No sorted list available or [TimelineStateType]
             * is not [TimelineStateType.CompositionArtifacts]:
             * a List of all in [CompositionArtifact] stored values
             *
             * + Case 2: A sorted list is available and the [TimelineStateType] is
             * [TimelineStateType.CompositionArtifacts]:
             *  if the list of unsorted *default* list is the same like the sorted list,
             *  it returns simply the list with the items on the same place.
             *  Else all elements that are not a part of the sorted list will added to the end of
             *  the list, items that was deleted by time, will be removed by the next called
             *  function [saveOrderedItems]
             */
            fun getList(timelineStateType: TimelineStateType, context: Context):
                    List<TimelineObject> {
                val mListAvailable = ModifiedList.isModifiedListAvailable(context)
                val isActualProject = timelineStateType == TimelineStateType.CompositionArtifacts

                val defaultList = getDefaultList(timelineStateType, context)
                val defaultListAsString = defaultList.map { it.id }

                if (mListAvailable == true && isActualProject) {
                    val sortedList = ModifiedList.getSortedList(context)
                    val sortedListAsString = sortedList.map { it.id }

                    // if modified list is  not the same
                    val defaultEqModified = defaultListAsString.containsAll(sortedListAsString)
                    // if modified list is  not the same
                    val modifiedEqDefault = sortedListAsString.containsAll(defaultListAsString)
                    // Same length same Words, has to be the same,
                    // so return simply the modified list
                    return if (defaultEqModified && modifiedEqDefault
                        && defaultListAsString.size == sortedListAsString.size
                    ) {
                        sortedList
                    } else {
                        adjustedSortedList(defaultList, sortedList)
                    }
                }
                return defaultList
            }

            private fun getDefaultList(timelineStateType: TimelineStateType, context: Context) =
                getAllDefaultHelpers()
                    .filter { it.isStoredSetAvailable(timelineStateType, it, context) }
                    .flatMap { it.getList(timelineStateType, context)!! }
                    .sortedBy { it.createdTimeAsString }

            private fun adjustedSortedList(
                defaultList: List<TimelineObject>, modifiedList: List<TimelineObject>
            ):
                    List<TimelineObject> {

                //check if some values was deleted / take only values that are also in default list
                val sortedList = defaultList
                    .flatMap { dIt ->
                        modifiedList
                            .filter { it.id == dIt.id }
                            .sortedBy { it.pos }
                    }
                    .toMutableList()

                // Take the last taken value as reference
                val earliestValue =
                    sortedList.maxByOrNull { it.createdTimeAsString }?.createdTimeAsString!!

                // Add all new taken values to the list
                sortedList.addAll(
                    defaultList.filter { it.createdTimeAsString > earliestValue }
                )

                return sortedList
            }
        }
    }
}