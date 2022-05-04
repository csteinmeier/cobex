package com.example.cobex.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import com.example.cobex.R

class CircularTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var circularColor: Int? = null
    private val circularTexColor: Int? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.CircularTextView){
            circularColor = getColor(R.styleable.CircularTextView_circularColor, 0)
        }
    }

    private var additionalSize = 0f

    fun resize(additional: Float){
        additionalSize += additional
        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        val circle = Paint()
        circle.color = circularColor?: R.color.black

        val stroke = Paint()
        stroke.color = circularTexColor?: R.color.white

        val diameter = if(height > width) height else width
        val radius = (diameter / 2f) + additionalSize
        Log.e("Radius", radius.toString() +" " + (diameter / 2).toString())

        //canvas?.drawCircle(radius, radius, radius, stroke)

        canvas?.drawCircle(radius, radius, radius- stroke.strokeWidth, circle);

        super.draw(canvas)
    }



}