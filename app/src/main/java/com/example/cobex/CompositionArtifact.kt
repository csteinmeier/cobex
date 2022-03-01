package com.example.cobex

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputEditText

object CompositionArtifact {

    enum class PreferenceKeywords {
        /**Keyword for the boolean value if an instance is available */
        INITIALIZED,
        /**Keyword for the boolean value if an instance of Keywords is available*/
        KEYWORD_INIT,
        /**Keyword to recognize the amount of clicked keywords in InputKeyword*/
        KEYWORD_AMOUNT,
        /**Keyword to recognize a Set of Clicked Keywords <br>
         * Saved like this: <br>
         * [KeywordType:FEELING]:[KeywordName:Strong] <br>
         * [KeywordType:FEELING]:[KeywordName:Loving]*/
        CLICKED_KEYWORDS,
        /**Keyword for the boolean value if an instance of CapturePicture is available*/
        PICTURE_INIT,
        /**Keyword to recognize the amount of taken pictures in InputKeyword*/
        PICTURE_AMOUNT,

        TAKEN_PICTURES
    }

    var clickedKeywords = 0
    var capturedPicture = 0


    /**
     *
     * @return number of clicked keywords in fragment "InputKeyword"
     */
    fun getSavedAmountOfKeywords(activity: Activity, preferenceKeywords: PreferenceKeywords): Int {
        return getPreferences(activity).getInt(preferenceKeywords.name, 0)
    }

    /**
     *
     * @return true if an instance of the SharedPreferences was created
     */
    fun isSavedPreferenceAvailable(activity: Activity): Boolean {
        return activity.getPreferences(Context.MODE_PRIVATE).getBoolean(
            PreferenceKeywords.INITIALIZED.name, false
        )
    }

    /**
     *
     * @return true if an instance of the SharedPreferences was created
     */
    fun isSavedPreferenceAvailable(activity: Activity,  preferenceKeywords: PreferenceKeywords): Boolean {
        return activity.getPreferences(Context.MODE_PRIVATE).getBoolean(
            preferenceKeywords.name, false
        )
    }

    /**
     *
     * will clear all values
     */
    fun clearSavedPreference(activity: Activity) {
        activity.getPreferences(Context.MODE_PRIVATE).edit().clear().apply()
    }

    /**
     *
     * @return editor to modify values in SharedPreferences
     */
    fun getPreferenceEditor(activity: Activity): SharedPreferences.Editor {
        return activity.getPreferences(Context.MODE_PRIVATE).edit()
    }

    /**
     *
     * @return SharedPreferences in Context.MODE_PRIVATE
     */
    fun getPreferences(activity: Activity): SharedPreferences {
        return activity.getPreferences(Context.MODE_PRIVATE)
    }

    /***
     *  will save a boolean in SharedPreferences to indicate that a saved instance is available
     */
    fun createInstanceOfPreference(activity: Activity) {
        Log.i("Instance of", "preference was created")
        activity.getPreferences(Context.MODE_PRIVATE).edit().putBoolean(
            PreferenceKeywords.INITIALIZED.name, true
        ).apply()
    }

    /***
     *  will save a boolean in SharedPreferences to indicate that a saved instance is available
     */
    fun createInstanceOfPreference(activity: Activity, preferenceKeywords: PreferenceKeywords) {
        Log.i("Instance of", "$preferenceKeywords was created")
        activity.getPreferences(Context.MODE_PRIVATE).edit().putBoolean(
            preferenceKeywords.name, true
        ).apply()
    }

    interface IArtifact{

        /**Will save a boolean to indicate a saved Instance is available*/
        fun createInstanceOfSavedPreferences(activity: Activity){
            if(!isSavedPreferenceAvailable(activity)){
                createInstanceOfPreference(activity)
            }
        }

        /**Will save a boolean to indicate a saved Instance is available*/
        fun createInstanceOfSavedPreferences(activity: Activity, preferenceKeywords: PreferenceKeywords){
            if(!isSavedPreferenceAvailable(activity, preferenceKeywords)){
                createInstanceOfPreference(activity, preferenceKeywords)
            }
        }

        /** @return true if a saved instance of preferences is available*/
        fun isInstanceOfSavedPreferencesAvailable(activity: Activity): Boolean {
            return isSavedPreferenceAvailable(activity)
        }

        /** @return true if a saved instance of preferences is available*/
        fun isInstanceOfSavedPreferencesAvailable(activity: Activity, preferenceKeywords: PreferenceKeywords): Boolean {
            return isSavedPreferenceAvailable(activity, preferenceKeywords)
        }

    }
}

