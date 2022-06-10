/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cobex.capture_picture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.util.Size
import androidx.core.graphics.get
import com.example.cobex.helper.SingletonHolder
import com.example.cobex.ml.ImageScene
import com.example.cobex.ml.LiteModelImageScene1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.util.*

/**
 * Helper class used to communicate between our app and the TF object detection model
 */
class ObjectDetectionHelper private constructor(private val context: Context) {

    data class ObjectPrediction(val label: String, val score: Float){
        override fun toString() =
            "[Label:${label}|Score: ${String.format(Locale.CANADA,"%.2f", score * 100)}%]"

    }

    private val intValues = IntArray(DIM_X * DIM_Y)

    private val labels: MutableList<String> by lazy {
        FileUtil.loadLabels(context, LABELS_PATH)
    }


    private val byteBuffer by lazy {
        ByteBuffer.allocateDirect(  4 * BATCH_SIZE * DIM_X * DIM_Y * PIXEL_SIZE)
    }

    private val tensorBuffer by lazy {
        TensorBuffer.createFixedSize(intArrayOf(1, DIM_X, DIM_Y, 3), DATATYPE)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        byteBuffer.order(ByteOrder.nativeOrder())
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        byteBuffer.rewind()

        var pixel = 0
        for (i in 0 until DIM_X) {
            for (j in 0 until DIM_Y) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF)  - 1f))
                byteBuffer.putFloat(((`val` shr 8 and 0xFF)  - 1f))
                byteBuffer.putFloat(((`val` and 0xFF) - 1f))
            }
        }
        return byteBuffer
    }


    private fun getPredictionMap(predictions: FloatArray) = labels.zip(predictions.toList())

    private fun getNBest(n: Int, predictions: FloatArray) =
        getPredictionMap(predictions).sortedByDescending { it.second }.take(n)

    private fun List<Pair<String, Float>>.toObjectPredictions() =
        this.map { ObjectPrediction(it.first, it.second) }

    fun predict(bitmap: Bitmap): List<ObjectPrediction> {

        val model = ImageScene.newInstance(context)

        tensorBuffer.loadBuffer(convertBitmapToByteBuffer(bitmap))

        val predictions = model.process(tensorBuffer).outputFeature0AsTensorBuffer.floatArray

        model.close()

        val bestN = getNBest(3, predictions).toObjectPredictions()

        bestN.forEach { Log.i("Best:", it.toString()) }

        return bestN
    }

    companion object : SingletonHolder<ObjectDetectionHelper, Context>(::ObjectDetectionHelper)
    {

        private const val LABELS_PATH = "coco_ssd_mobilenet_v1_1.0_labels2.txt"

        private const val DIM_X = 224
        private const val DIM_Y = 224

        private const val BATCH_SIZE = 1
        private const val PIXEL_SIZE = 3
        private val DATATYPE = DataType.FLOAT32


    }
}