package com.example.cobex

import android.content.Context
import android.content.ContextWrapper
import android.text.BoringLayout
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

    private fun isSavedInstanceAvailable() =
        getPreferences().getBoolean(Keywords.INITIALIZED.name, false)

    private fun isSavedInstanceOfCategoryAvailable(identifier: String) =
        getPreferences().getBoolean(identifier, false)

    private fun clearSavedInstance() =
        getPreferencesEditor().clear().apply()

    private fun createInstanceOfPreference() =
        getPreferencesEditor().putBoolean(Keywords.INITIALIZED.name, true).apply()

    private fun <T>createInstanceOfPreference(clazz: Class<T>) =
        getPreferencesEditor().putBoolean(clazz.name, true).apply()

    private fun deleteTakenPictures() =
        getImageFileDir().deleteRecursively()

    private fun putStringSet(identifier: String, set: Set<String>) =
        getPreferencesEditor().putStringSet("${Keywords.SET.name} $identifier", set).apply()

    private fun getStringSet(identifier: String) =
        getPreferences().getStringSet("${Keywords.SET.name} $identifier", setOf())

    private fun putCounter(identifier: String, counter: Int) =
        getPreferencesEditor().putInt("${Keywords.COUNTER.name} $identifier", counter).apply()

    private fun getCounter(identifier: String) =
        getPreferences().getInt("${Keywords.COUNTER.name} $identifier", 0)

    private fun putBoolean(identifier: String, boolean: Boolean) =
        getPreferencesEditor().putBoolean("${Keywords.BOOLEAN.name} $identifier", boolean)

    private fun getBoolean(identifier: String) =
        getPreferences().getBoolean("${Keywords.BOOLEAN.name} $identifier", false)

    fun getImageFileDir(): File =
        ContextWrapper(context).getDir("images", Context.MODE_PRIVATE)

    fun getTimeStamp() = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

    /**
     * To write data to the artifact,
     * this interface should be implemented in the respective class.
     *
     * * To signal that an instance of an artifact has been started,
     * a Boolean can be saved with the command
     * [markAsSavedIfNotMarkedAsSaved].
     * Here a Boolean is stored which signals that something has been
     * placed in SharedPreferences as well as the executing class
     * which has saved data.
     *
     * * To see if data was written [isInstanceOfSavedPreferencesAvailable] can be used.
     * This can be done with the context alone or with the help of the respective class.
     *
     * * To write concrete data to the references it is enough to use one of the methods:
     * [putStringSet], [putCounter]
     * Internally the methods of SharedPreferences are used
     *
     * * To get the concrete data use one of this methods:
     * [getCounter], [getStringSet]
     *
     * * To delete everything saved use [clearSavedInstance]
     */
    interface IArtifact {

        fun clearSavedInstance(context: Context){
            CompositionArtifact.getInstance(context).clearSavedInstance()

            if(getStringSet(context, CapturePicture::class.java)!!.isNotEmpty())
                CompositionArtifact.getInstance(context).deleteTakenPictures()
        }

        /**
         * @return true if a saved instance of preferences is available
         *
         * */
        fun isInstanceOfSavedPreferencesAvailable(context: Context): Boolean =
            CompositionArtifact.getInstance(context).isSavedInstanceAvailable()

        /**
         * @param clazz which stands for a part of the artifact
         *
         * @return true if a saved instance of preferences is available
         * */
        fun <T>isInstanceOfSavedPreferencesAvailable(context: Context, clazz: Class<T>)
            = CompositionArtifact.getInstance(context).isSavedInstanceOfCategoryAvailable(clazz.name)

        /**
         * Stores a truth value which signals that the class has stored a part of the artifact
         * and a general boolean that a project has started.
         *
         *  @param clazz which stands for a part of the artifact
         */
        fun <T>markAsSavedIfNotMarkedAsSaved(context: Context, clazz: Class<T>){
            if(!isInstanceOfSavedPreferencesAvailable(context))
                CompositionArtifact.getInstance(context).createInstanceOfPreference()
            if(!isInstanceOfSavedPreferencesAvailable(context, clazz))
                CompositionArtifact.getInstance(context).createInstanceOfPreference(clazz)
        }

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


    }


}

