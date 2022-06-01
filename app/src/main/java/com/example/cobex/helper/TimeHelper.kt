package com.example.cobex.helper

import android.content.Context
import com.example.cobex.artifacts.CompositionArtifact
import com.example.cobex.R

object TimeHelper {

    fun fromCreatedTillNowEasyString(context: Context, timeAsString: String): String {
        val currentTime = stringToTimeMap(CompositionArtifact.getInstance(context).getTimeStamp())
        val createdTime = stringToTimeMap(timeAsString)
        val timeDifference = getTimeDifference(currentTime, createdTime)
        return getTimeDifferenceToShow(context, timeDifference)
    }

    private fun stringToTimeMap(time: String) =
        mapOf(
            TimeType.YEAR to time.substring(0, 4).toInt(),
            TimeType.MONTH to time.substring(5, 7).toInt(),
            TimeType.DAY to time.substring(8, 10).toInt(),
            TimeType.HOUR to time.substring(11, 13).toInt(),
            TimeType.MINUTE to time.substring(14, 16).toInt(),
        )


    private enum class TimeType(val readable: Int) {
        YEAR(R.string.timelineYears),
        MONTH(R.string.timelineMonths),
        DAY(R.string.timelineDays),
        HOUR(R.string.timelineHours),
        MINUTE(R.string.timelineMinutes),
    }

    private fun getTimeDifference(timeMap0: Map<TimeType, Int>, timeMap1: Map<TimeType, Int>)
            : Pair<TimeType, Int>? {
        // Year -> Minute
        for (entry in timeMap0) {
            // timeMap1 - timeMap0
            val diff = entry.value.minus(timeMap1[entry.key]!!)
            // if value of Year/Month.. is not the same -> true
            if (diff != 0) {
                return entry.key to diff
            }
            else if(entry.key == TimeType.MINUTE && diff == 0){
                return TimeType.MINUTE to 0
            }
        }
        return null
    }

    private fun getTimeDifferenceToShow(context: Context, pair: Pair<TimeType, Int>?): String {
        // more than a minute
        return if (pair?.second!! > 0)
            "${pair.second} ${pair.first.readable.let { context.getString(it) }}"
        else // less than a whole minute
            context.getString(R.string.timelineRecently)
    }

}