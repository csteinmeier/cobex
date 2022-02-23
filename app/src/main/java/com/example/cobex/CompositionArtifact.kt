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
        /**Keyword to recognize the amount of clicked keywords in InputKeyword*/
        KEYWORD_AMOUNT,
        /**Keyword to recognize a Set of Clicked Keywords <br>
         * Saved like this: <br>
         * [KeywordType:FEELING]:[KeywordName:Strong] <br>
         * [KeywordType:FEELING]:[KeywordName:Loving]*/
        CLICKED_KEYWORDS,
    }

    var clickedKeywords = 0


    /**
     *
     * @return number of clicked keywords in fragment "InputKeyword"
     */
    fun getSavedAmountOfKeywords(activity: Activity): Int {
        return getPreferences(activity).getInt(PreferenceKeywords.KEYWORD_AMOUNT.name, 0)
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
        activity.getPreferences(Context.MODE_PRIVATE).edit().putBoolean(
            PreferenceKeywords.INITIALIZED.name, true
        ).apply()
    }

}

