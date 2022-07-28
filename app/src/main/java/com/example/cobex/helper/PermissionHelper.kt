package com.example.cobex.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.cobex.R

object PermissionHelper {

    const val FRAGMENT_CAPTURE_PICTURE_CODE = 100

    const val FRAGMENT_CAPTURE_ACTIVITY_CODE = 110

    const val FRAGMENT_CAPTURE_SOUND_CODE = 120

    const val FRAGMENT_INPUT_MELODY_CODE = 130

    const val FRAGMENT_CREATE_RHYTHM_CODE = 140

    const val CAMARA_REQUEST_CODE = 11
    const val ACTIVITY_RECOGNITION_REQUEST_CODE = 43




    /**
     * asks for the permission
     *
     * @return true if the permission is already granted
     */
    fun hasPermission(
        fragment: Fragment, fragmentCode: Int, vararg permission: String
    ): Boolean {
        val hasPermission =
            permission.filter { !isPermissionGranted(fragment.requireContext(), it) }
        if (hasPermission.isNotEmpty()) {
            fragment.requestPermissions(permission, fragmentCode)
            return false
        }
        return true
    }

    /**
     * Shows a Toast message with
     * @see R.string.required_permission_denied_second_time
     */
    fun showSecondTimeDenied(context: Context) {
        Toast.makeText(context, R.string.required_permission_denied_second_time, Toast.LENGTH_LONG)
            .show()
    }

    private fun isPermissionGranted(context: Context, string: String) =
        ActivityCompat.checkSelfPermission(context, string) == PackageManager.PERMISSION_GRANTED

    /**
     * Simple Dialog to inform the user
     */
    class SimpleDialog(private val activity: Activity, private val textMsg: Int?) :
        Dialog(activity) {
        private lateinit var button: Button
        private lateinit var textView: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_required_permission)

            button = findViewById(R.id.button_dialog_required_permission)
            textView = findViewById(R.id.textView_dialog_required_permission)

            val msg = textMsg?: R.string.rational_required_permission

            textView.text = activity.getText(msg)

            button.setOnClickListener {
                this.dismiss()
            }
        }
    }


    /**
     * Provides two function which mostly needed.
     * * Function [hasPermission] will return a boolean if a permission is already granted.
     * If this is not the case it will ask for the permission in [requiredPermissions]
     *
     * * Function [helperOnRequestPermissionResult] to be called in [onRequestPermissionResult] in
     * a Fragment. [onRequestPermissionResult] will automatically called after
     * the user is asked for permission. For Example after [hasPermission].
     *
     */
    interface IRequirePermission {
        /** Main Permission like [Manifest.permission.Camara] for
         * helperOnRequestPermissionResult*/
        fun mainPermission(): String
        /** @return for
         * helperOnRequestPermissionResult*/
        fun fragment(): Fragment
        /** Like Request Code but for the fragment itself */
        fun fragmentCode(): Int
        /** All Permission that is needed */
        fun requiredPermissions(): Array<out String>


        /**
         * use this function in [onRequestPermissionResult]
         *
         * @param hasPermission a function that will be called if the permission is given
         * @param specialDialog a Dialog that will appear if the user denied the permission, if null
         * a simple generic Dialog will appear
         */
        fun helperOnRequestPermissionResult(
            requestCode: Int,
            permission: Array<out String>,
            grantResults: IntArray,
            hasPermission: () -> Unit,
            specialDialog: Dialog?
        ) {
            // Permission Code of this Fragment
            if (requestCode == fragmentCode())
            //  Permission granted ?
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    hasPermission.invoke()
                } else {
                    // show rational
                    if (fragment().shouldShowRequestPermissionRationale(mainPermission())) {
                        specialDialog ?: SimpleDialog(fragment().requireActivity(), null).show()
                    }
                    // second time denied, show the user he has to change his setting,
                    // in case of he want to use this part of the application
                    else {
                        showSecondTimeDenied(fragment().requireContext())
                    }
                }
        }

        /**
         * @return true if the permission is granted
         * else false and it will ask for permission in [requiredPermissions]
         */
        fun hasPermission(): Boolean {
            val hasPermission =
                requiredPermissions().filter { !isPermissionGranted(fragment().requireContext(), it) }
            if (hasPermission.isNotEmpty()) {
                fragment().requestPermissions(requiredPermissions(), fragmentCode())
                return false
            }
            return true
        }

        /**
         * @param onTrue a function called when permission is given
         * @return true if the permission is granted
         * else it will ask for permission in [requiredPermissions]
         */
        fun hasPermission(onTrue: () -> Unit): Boolean {
            val hasPermission =
                requiredPermissions().filter { !isPermissionGranted(fragment().requireContext(), it) }
            if (hasPermission.isNotEmpty()) {
                fragment().requestPermissions(requiredPermissions(), fragmentCode())
                return false
            }
            onTrue.invoke()
            return true
        }
    }
}