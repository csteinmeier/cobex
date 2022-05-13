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

    private val textPaint = Paint()
    private val bitmapPaint = Paint()

    private var text01 = ""
    private var text02 = ""


    init {
        transparentPaint.color = android.R.color.transparent.resourceToColor(context)

        val ovalPaintDefaultColor01 = android.R.color.holo_red_dark.resourceToColor(context)
        val ovalPaintDefaultColor02 = android.R.color.holo_blue_dark.resourceToColor(context)

        context.withStyledAttributes(attrs, R.styleable.DivisionBar){
            ovalPaint01.color = getColor(R.styleable.DivisionBar_color01, ovalPaintDefaultColor01)
            ovalPaint02.color = getColor(R.styleable.DivisionBar_color02, ovalPaintDefaultColor02)

            text01 = getString(R.styleable.DivisionBar_text01)?: ""
            text02 = getString(R.styleable.DivisionBar_text02)?: ""

            cornerRadius = getDimension(R.styleable.DivisionBar_circularCorner, 0f)
        }

        textPaint.color = Color.WHITE
        textPaint.textSize = 40f

    }

    private var value = 50f
    private var dragLeft = false

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

        fun calcMid() = (width / 100 * progress)

        fun getLeftSide() = RectF(0f, 10f, calcMid(), height)

        fun verticalPadding() = height / 4

        fun iconTop() = verticalPadding()

        fun iconBot() = height - verticalPadding()

        fun getMiddleOfLeftSide() = (((width / 100) * progress) / 2)

        fun getMiddleOfRightSide() = (getRightSide().right / 2) + (((width / 100) * progress) / 2)

        fun getRightSide() = RectF(calcMid(), 10f, width, height)

        fun getLeftIconPlace(iconSize: Int) =
            RectF(
                getMiddleOfLeftSide() - iconSize,
                iconTop() ,
                getMiddleOfLeftSide() + iconSize,
                iconBot())

        fun getRightIconPlace(iconSize: Int) =
            RectF(
                getMiddleOfRightSide() - iconSize,
                iconTop(),
                getMiddleOfRightSide() + iconSize,
                iconBot())


    }

}