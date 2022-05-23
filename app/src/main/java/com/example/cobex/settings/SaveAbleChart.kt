package com.example.cobex.settings

import android.content.Context
import android.util.Log
import com.example.cobex.CompositionArtifact
import com.example.cobex.artifacts.Artifact
import com.example.cobex.timelineview.TimelineStateType

/**
 * Child of CompositionArtifact.IArtifact
 * Specialized to Pie Chart Fragments
 */
interface SaveAbleChart : CompositionArtifact.IArtifact {

    /**
     * Method to extract an Artifact and its Value from a String
     *
     * @return Key Value Pair of an Artifact with its value
     */
    private fun String.toKeValuePair() =
        this.substringAfter("ARTIFACT:").substringBefore("VALUE").toInt()
            .toArtifact() to this.substringAfter("VALUE:").toFloat()

    private fun Int.toArtifact() = Artifact.getAllArtifactTypes().find { this == it.ordinal }


    /**
     * @param artifacts list of artifacts which values are in need
     *
     * @return Either a new Map<Artifact, Float> (Float = 100 / artifacts) or the saved
     * values in a Map<Artifact, Float> matching the artifacts in the given list.
     */
    fun <T>getSavedValuesOrNew(context: Context, clazz: Class<T>, artifacts: List<Artifact>):
            MutableMap<Artifact, Float> {
        val set = getStringSet(context, clazz)
            ?: return artifacts
                .zip(artifacts.map { 100f / artifacts.size })
                .toMap() as MutableMap<Artifact, Float>
        val values = set.associate { it.toKeValuePair() }

        val map = mutableMapOf<Artifact, Float>()
        artifacts.forEach {
            if(values[it] != null){
                map[it] = values[it]!!
            }
        }

        return map
    }

    /**
     * Simple method which will transform the MuteAbleMap<Artifact, Float> to a save able
     * string. The string will be saved through composition Artifact and its shared preferences
     */
    fun <T>saveValues(context: Context, clazz: Class<T>, values: MutableMap<Artifact, Float>) {
        val set = getStringSet(context, clazz)?: mutableSetOf()
        val savedMap = set.associate { it.toKeValuePair() }.toMutableMap()
        values.forEach { (t, u) ->  savedMap[t] = u }
        val stringSet = savedMap.map { "ARTIFACT:${it.key?.ordinal}VALUE:${it.value}" }.toSet()
        putStringSet(context, clazz, stringSet)
    }

}