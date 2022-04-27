package com.example.cobex.helper

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toUri
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

    fun Int.resourceToString(context: Context) = ContextWrapper(context).getString(this)

}