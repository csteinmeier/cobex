package com.example.cobex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import java.io.File

object Extensions {

    fun String.toImage(context: Context): Bitmap =
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, File(this).toUri()))


    fun Int.toLayout(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(this, parent, false)


}