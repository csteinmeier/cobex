package com.example.cobex

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.ImageDecoder
import android.media.Image
import android.text.BoringLayout
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

/**
 *
 * Class to store data entered by the user,
 * for easy use the interface IArtifact must be implemented
 *
 */
class CompositionArtifact private constructor(private val context: Context) {

    companion object : SingletonHolder<CompositionArtifact, Context>(::CompositionArtifact)

    private enum class Keywords {

        // File name of the shared data
        COMPOSITION_ARTIFACT,

        // Flag name to indicate a saved instance
        INITIALIZED,

        // Used to indicate set of strings in Shared Preferences
        SET,

        // Used to indicate a integer which stands for a counter
        COUNTER,

        BOOLEAN
    }

    private fun getPreferences() =
        context.getSharedPreferences(Keywords.COMPOSITION_ARTIFACT.name, Context.MODE_PRIVATE)

    private fun getPreferencesEditor() =
        getPreferences().edit()

    private fun isProjectStarted() =
        !getPreferences().getString(Keywords.INITIALIZED.name, "").isNullOrEmpty()

    private fun markAsProjectStarted() =
        getPreferencesEditor().putString(Keywords.INITIALIZED.name, getTimeStamp()).apply()
    
    private fun getProjectStartTime() = 
        getPreferences().getString(Keywords.INITIALIZED.name, "")

    private fun isSavedInstanceOfCategoryAvailable(identifier: String) =
        getPreferences().getBoolean(identifier, false)

    private fun clearSavedInstance() =
        getPreferencesEditor().clear().apply()

    private fun <T>createInstanceOfPreference(clazz: Class<T>) =
        getPreferencesEditor().putBoolean(clazz.name, true).apply()

    private fun deleteTakenFiles() {
        getFileDir().deleteRecursively()
        getImageFileDir().deleteRecursively()
    }

    private fun putStringSet(identifier: String, set: Set<String>) =
        getPreferencesEditor().putStringSet("${Keywords.SET.name} $identifier", set).apply()

    private fun getStringSet(identifier: String) =
        getPreferences().getStringSet("${Keywords.SET.name} $identifier", setOf())

    private fun putCounter(identifier: String, counter: Int) =
        getPreferencesEditor().putInt("${Keywords.COUNTER.name} $identifier", counter).apply()

    private fun getCounter(identifier: String) =
        getPreferences().getInt("${Keywords.COUNTER.name} $identifier", 0)

    private fun putBoolean(identifier: String, boolean: Boolean) =
        getPreferencesEditor().putBoolean("${Keywords.BOOLEAN.name} $identifier", boolean).apply()

    private fun getBoolean(identifier: String) =
        getPreferences().getBoolean("${Keywords.BOOLEAN.name} $identifier", false)

    private fun putString(identifier: String, string: String) =
        getPreferencesEditor().putString(identifier, string)

    private fun getString(identifier: String) =
        getPreferences().getString(identifier, "")

    fun getFileDir(): File =
        context.filesDir.absoluteFile.absoluteFile

    fun getImageFileDir(): File =
        ContextWrapper(context).getDir("images", Context.MODE_PRIVATE)


    fun getTimeStamp(): String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())


    /**
     *
     * * To Save Data use [synchroniseArtifact]
     *
     * * To write concrete data to the references it is enough to use one of the methods:
     * [putStringSet], [putCounter] ...
     * Internally the methods of SharedPreferences are used
     *
     * * To get the concrete data use one of this methods:
     * [getCounter], [getStringSet] ... 
     *
     * * To delete everything saved use [clearSavedInstance]
     */
    interface IArtifact {


        enum class SynchronizeMode{
            APPEND,
            REMOVE
        }

        fun clearSavedInstance(context: Context){
            CompositionArtifact.getInstance(context).clearSavedInstance()

            CompositionArtifact.getInstance(context).deleteTakenFiles()

        }

        /**
         * @return true if a saved instance of preferences is available
         *
         * */
        fun isInstanceOfSavedPreferencesAvailable(context: Context): Boolean? =
            CompositionArtifact.getInstance(context).isProjectStarted()

        /**
         * @param clazz which stands for a part of the artifact
         *
         * @return true if a saved instance of preferences is available
         * */
        fun <T>isInstanceOfSavedPreferencesAvailable(context: Context, clazz: Class<T>)
            = CompositionArtifact.getInstance(context).isSavedInstanceOfCategoryAvailable(clazz.name)

        fun markAsProjectStarted(context: Context){
            CompositionArtifact.getInstance(context).markAsProjectStarted()
        }

        /**
         * Stores a truth value which signals that the class has stored a
         * part of the artifact
         *
         *  @param clazz which stands for a part of the artifact
         */
        fun <T>markAsSavedIfNotMarkedAsSaved(context: Context, clazz: Class<T>){
            if(!isInstanceOfSavedPreferencesAvailable(context, clazz))
                CompositionArtifact.getInstance(context).createInstanceOfPreference(clazz)
        }

        fun isProjectStarted(context: Context) =
            CompositionArtifact.getInstance(context).isProjectStarted()
        
        fun getProjectStartTime(context: Context) = 
            CompositionArtifact.getInstance(context).getProjectStartTime()

        fun <T>getStringSet(context: Context, clazz: Class<T>): Set<String>? =
            CompositionArtifact.getInstance(context).getStringSet(clazz.name)

        fun <T>putStringSet(context: Context, clazz: Class<T>, set: Set<String>) =
            CompositionArtifact.getInstance(context).putStringSet(clazz.name, set)

        fun <T>getCounter(context: Context, clazz: Class<T>) =
            CompositionArtifact.getInstance(context).getCounter(clazz.name)

        fun <T>putCounter(context: Context, clazz: Class<T>, counter: Int) =
            CompositionArtifact.getInstance(context).putCounter(clazz.name, counter)

        fun <T>putBoolean(context: Context, clazz: Class<T>, boolean: Boolean) =
            CompositionArtifact.getInstance(context).putBoolean(clazz.name, boolean)

        fun <T>getBoolean(context: Context, clazz: Class<T>) =
            CompositionArtifact.getInstance(context).getBoolean(clazz.name)

        fun getTimeStamp(context: Context): String =
            CompositionArtifact.getInstance(context).getTimeStamp()

        /**
         * Used to automatically save data, extending already saved data, or remove existing data,
         * also setting the counter in the Create New Fragment.
         *
         * @param clazz that can be identified with the stored string
         *
         * @param stringToSyn new data
         *
         * @param mode [SynchronizeMode.APPEND] or [SynchronizeMode.REMOVE] to remove or add a
         * artifact
         */
        fun <T>synchroniseArtifact(
            context: Context, stringToSyn: String, clazz: Class<T>, mode: SynchronizeMode){
            markAsSavedIfNotMarkedAsSaved(context, clazz)
            val list = getStringSet(context, clazz)?.toMutableList()?: mutableListOf()
            when(mode){
                SynchronizeMode.APPEND -> list.add(stringToSyn)
                SynchronizeMode.REMOVE -> list.remove(stringToSyn)
            }
            putCounter(context, clazz, list.size)
            putStringSet(context, clazz, list.toSet())
        }
        fun getFileDir(context: Context) =
            CompositionArtifact.getInstance(context).getFileDir()
    }



}

