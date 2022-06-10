package com.example.cobex.helper

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.findNavController
import java.io.File

object Extensions {

    fun String.toImage(context: Context): Bitmap =
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, File(this).toUri())
        )

    fun String.showAsToast(context: Context, toastLength: Int) =
        Toast.makeText(context, this, toastLength).show()

    fun Int.toLayout(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(this, parent, false)

    fun Int.millisToMinFormat(): String {
        val seconds = this / 1000
        val min: Int = (seconds / 60)
        val r = seconds - (min * 60)
        return if (r < 10) "$min:0$r"
        else "$min:$r"
    }

    fun ProgressBar.prepareProgressWithSound(mediaPlayer: MediaPlayer) {
        this.progress = 0
        this.max = mediaPlayer.duration
        this.postDelayed(progressRun(mediaPlayer, this), 15)
    }

    private fun progressRun(mediaPlayer: MediaPlayer, progressBar: ProgressBar):
            Runnable = object : Runnable {
        override fun run() {

            progressBar.progress = mediaPlayer.currentPosition

            // Call this thread again after 15 milliseconds => ~ 1000/60fps
            if(!mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            else
                progressBar.postDelayed(this, 15)

        }
    }

    fun Button.navigateOnClick(dest: Int) = this.setOnClickListener { findNavController().navigate(dest) }

    fun Int.resourceToString(context: Context) = ContextWrapper(context).getString(this)
    fun Int.resourceToColor(context: Context) = ContextWrapper(context).getColor(this)
    fun Int.resourceToBitmap(context: Context, width: Int, height: Int) : Bitmap{
        val drawAble = ContextCompat.getDrawable(context, this)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawAble!!.setBounds(0, 0, width, height)
        drawAble.draw(canvas)
        return bitmap
    }

    fun String.extractPathOfImage() = this.substringBefore('!')
    fun String.extractPossiblePredictionsOfImage() = this.substringAfter('!')

}