package com.example.cobex.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.example.cobex.R
import com.example.cobex.artifacts.Artifact
import com.example.cobex.helper.Extensions.resourceToBitmap
import com.example.cobex.helper.Extensions.resourceToColor

class DivisionBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var cornerRadius: Float?= 0f


    private var fullRect : RectF ?= null

    private val path = Path()

    private val transparentPaint = Paint()
    private val ovalPaint01 = Paint()
    private val ovalPaint02 = Paint()

    var symbol01 : Bitmap ?= null
    var symbol02 : Bitmap ?= null

    private val bitmapPaint = Paint()


    init {
        /**
         * Some init Values, set at layout level
         */
        transparentPaint.color = android.R.color.transparent.resourceToColor(context)

        val ovalPaintDefaultColor01 = android.R.color.holo_red_dark.resourceToColor(context)
        val ovalPaintDefaultColor02 = android.R.color.holo_blue_dark.resourceToColor(context)

        context.withStyledAttributes(attrs, R.styleable.DivisionBar){
            ovalPaint01.color = getColor(R.styleable.DivisionBar_color01, ovalPaintDefaultColor01)
            ovalPaint02.color = getColor(R.styleable.DivisionBar_color02, ovalPaintDefaultColor02)

            cornerRadius = getDimension(R.styleable.DivisionBar_circularCorner, 0f)
        }
    }

    private var value = 50f
    private var dragLeft = false

    /**
     * @param value between 0 - 100,
     * example: value 60 will set the left Bar to percentage of 60 and the other on to 40
     *
     */
    fun resize(value: Float){
        dragLeft = this.value - value < 0

        this.value = value
        invalidate()
    }


    override fun draw(canvas: Canvas) {

        val spaceX = measuredWidth.toFloat()
        val spaceY = measuredHeight.toFloat()

        val manager = SizeManager(spaceX, spaceY, value)

        fullRect = RectF(0f, 0f, spaceX, spaceY)

        path.addRoundRect(fullRect!!, cornerRadius!!, cornerRadius!!, Path.Direction.CW)

        canvas.clipPath(path)

        /**
         * So no Icon will overdraw by its opposite bar,
         * its necessary the drawing will happen in the right order
         */
        drawInOrder(canvas, manager, dragLeft)

        super.draw(canvas)
    }

    private fun drawInOrder(canvas: Canvas, manager: SizeManager, dragLeft: Boolean) =
        when(dragLeft){
            true -> assumeRight(canvas, manager)
            false -> assumeLeft(canvas, manager)
        }

    private fun assumeLeft(canvas: Canvas, manager: SizeManager){
        if (symbol01 != null){
            if(manager.calcMid() > symbol01!!.width + 20) {
                drawRightFirst(canvas, manager)
            } else {
                drawLeftFirst(canvas, manager)
            }
        }
    }


    private fun assumeRight(canvas: Canvas, manager: SizeManager){
        if (symbol02 != null){
            if(manager.calcMid() > symbol02!!.width + 20) {
                drawRightFirst(canvas, manager)
            } else {
                drawLeftFirst(canvas, manager)
            }
        }
    }

    private fun drawLeftFirst(canvas: Canvas, manager: SizeManager){
        canvas.drawRect(manager.getLeftSide(), ovalPaint01)
        if(symbol01 != null)
            drawWhiteBitmap(canvas, symbol01!!, manager.getLeftIconPlace(symbol01!!.width))

        canvas.drawRect(manager.getRightSide(), ovalPaint02)
        if(symbol02 != null)
            drawWhiteBitmap(canvas, symbol02!!, manager.getRightIconPlace(symbol02!!.width))
    }

    private fun drawRightFirst(canvas: Canvas, manager: SizeManager){
        canvas.drawRect(manager.getRightSide(), ovalPaint02)
        if(symbol02 != null)
            drawWhiteBitmap(canvas, symbol02!!, manager.getRightIconPlace(symbol02!!.width))

        canvas.drawRect(manager.getLeftSide(), ovalPaint01)
        if(symbol01 != null)
            drawWhiteBitmap(canvas, symbol01!!, manager.getLeftIconPlace(symbol01!!.width))
    }

    private fun drawWhiteBitmap(canvas: Canvas, bitmap: Bitmap, place: RectF){
        bitmapPaint.colorFilter =
            PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, null, place, bitmapPaint)
    }

    private class SizeManager(val width : Float, val height: Float, val progress: Float){

        /**
         * @return Calculates the middle with a value given between 0 and 100
         */
        fun calcMid() = (width / 100 * progress)

        /**
         * @return Left rectangle and its place
         */
        fun getLeftSide() = RectF(0f, 10f, calcMid(), height)

        /**
         * @return Padding to the top
         */
        fun verticalPadding() = height / 4

        /**
         * @return Start point of the icon
         */
        fun iconTop() = verticalPadding()

        /**
         * @return Bottom pont of the icon
         */
        fun iconBot() = height - verticalPadding()

        /**
         * @return Middle point of the left rectangle
         */
        fun getMiddleOfLeftSide() = (((width / 100) * progress) / 2)


        /**
         * @return Middle point of the right rectangle
         */
        fun getMiddleOfRightSide() = (getRightSide().right / 2) + (((width / 100) * progress) / 2)

        /**
         * @return Right rectangle and its place
         */
        fun getRightSide() = RectF(calcMid(), 10f, width, height)

        /**
         * @return Rectangle and its place of the left icon
         */
        fun getLeftIconPlace(iconSize: Int) =
            RectF(
                getMiddleOfLeftSide() - iconSize,
                iconTop() ,
                getMiddleOfLeftSide() + iconSize,
                iconBot())

        /**
         * @return Rectangle and its place of the right icon
         */
        fun getRightIconPlace(iconSize: Int) =
            RectF(
                getMiddleOfRightSide() - iconSize,
                iconTop(),
                getMiddleOfRightSide() + iconSize,
                iconBot())


    }

}