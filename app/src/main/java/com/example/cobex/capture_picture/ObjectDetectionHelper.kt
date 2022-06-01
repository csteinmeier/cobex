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
import android.util.Size
import com.example.cobex.helper.SingletonHolder
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op

/**
 * Helper class used to communicate between our app and the TF object detection model
 */
class ObjectDetectionHelper private constructor(context: Context) {

    /** Abstraction object that wraps a prediction output in an easy to parse way */
    data class ObjectPrediction(val location: RectF, val label: String, val score: Float)

    private val locations = arrayOf(Array(OBJECT_COUNT) { FloatArray(4) })
    private val labelIndices =  arrayOf(FloatArray(OBJECT_COUNT))
    private val scores =  arrayOf(FloatArray(OBJECT_COUNT))

    private var imageRotationDegrees: Int = 0
    private val tfImageBuffer = TensorImage(DataType.UINT8)

    private val labels by lazy {
        FileUtil.loadLabels(context, LABELS_PATH)
    }

    /**
     * To determine required tensor shape for a model
     */
    private val tfInputSize by lazy {
        val inputIndex = 0
        val inputShape = tfLite.getInputTensor(inputIndex).shape()
        Size(inputShape[2], inputShape[1]) // Order of axis is: {1, height, width, 3}
        //expects squares of size 300 x 300
    }

    /**
     * To transform image data for a model:
     */
    private fun tfImageProcessor(bitmap: Bitmap): ImageProcessor? {
        val cropSize = minOf(bitmap.width, bitmap.height)
        return ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(
                ResizeOp(
                tfInputSize.height, tfInputSize.width, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR)
            )
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(0f, 1f))
            .build()
    }


    /**
     * TfLite Interpreter
     * @see MODEL_PATH
     */
    private val tfLite by lazy {
        Interpreter(
            FileUtil.loadMappedFile(context, MODEL_PATH),
            // NNAPI DELEGATE to handle hardware acceleration of the model execution
            // Recommended but not required. (Speed up)
            Interpreter.Options().addDelegate(nnApiDelegate))
    }

    private val nnApiDelegate by lazy  {
        NnApiDelegate()
    }

    private val outputBuffer = mapOf(
        0 to locations,
        1 to labelIndices,
        2 to scores,
        3 to FloatArray(1)
    )

    private val predictions get() = (0 until OBJECT_COUNT).map { predictions ->
        ObjectPrediction(

            // The locations are an array of [0, 1] floats for [top, left, bottom, right]
            location = locations[0][predictions].let {
                RectF(it[1], it[0], it[3], it[2])
            },

            // SSD Mobilenet V1 Model assumes class 0 is background class
            // in label file and class labels start from 1 to number_of_classes + 1,
            // while outputClasses correspond to class index from 0 to number_of_classes
            label = labels[1 + labelIndices[0][predictions].toInt()],

            // Score is a single value of [0, 1]
            score = scores[0][predictions]
        )
    }


    private fun predict(image: TensorImage): List<ObjectPrediction> {
        tfLite.runForMultipleInputsOutputs(arrayOf(image.buffer), outputBuffer)
        return predictions
    }

    fun predict(bitmap: Bitmap): ObjectPrediction? {
        val tfImage = tfImageProcessor(bitmap)?.process(tfImageBuffer.apply { load(bitmap) })
        val predictions = tfImage?.let { predict(it) }
        return predictions?.maxByOrNull { it.score }
    }

    companion object : SingletonHolder<ObjectDetectionHelper, Context>(::ObjectDetectionHelper)
    {
        const val OBJECT_COUNT = 10
        // Settings for Object Detection Model
        private const val ACCURACY_THRESHOLD = 0.5f
        private const val MODEL_PATH = "coco_ssd_mobilenet_v1_1.0_quant.tflite"
        private const val LABELS_PATH = "coco_ssd_mobilenet_v1_1.0_labels.txt"

    }
}