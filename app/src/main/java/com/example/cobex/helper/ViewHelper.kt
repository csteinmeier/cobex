package com.example.cobex.helper

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.cobex.R
import com.example.cobex.helper.Extensions.millisToMinFormat
import com.example.cobex.helper.Extensions.toImage
import com.example.cobex.helper.ViewHelper.SimpleTextField

/**
 *
 * ## To automatically set the Value to the view
 *
 *
 * * **Class / Enumeration to contain all the different views
 * that should be filled with different values.**
 *
 * * Each inner class consists of a specific view that is populated using the resource.
 * Usage is mainly intended for transformations that bloat code and happen more frequently.
 *
 * * Easy Example [SimpleTextField]
 *
 * @sample ViewHelper.SimpleTextField.initView
 * @sample ViewHelper.TextFieldSimpleTime.initView
 * @sample ViewHelper.ImageViewBitmap.initView
 *
 *
 */
abstract class ViewHelper<V : View, T>(
    protected val view: V,
    protected val resource: T,
    protected val context: Context?
) {

    abstract fun initView()

    /**
     * Call this class with a **TextField** and a **text**
     * and this class will set the text automatically to the **TextField**.
     *
     * @sample SimpleTextField.initView
     */
    class SimpleTextField(view: TextView, resource: String) :
        ViewHelper<TextView, String>(view, resource, null) {
        init {
            initView()
        }

        override fun initView() = run { view.text = resource }
    }

    /**
     * Call this class with a **TextField** and a **resource** to a saved String (Values)
     * and this class will set the text automatically to the **TextField**.
     *
     * @param context required !
     *
     * @sample TextFieldResourceString.initView
     */
    class TextFieldResourceString(view: TextView, resource: Int, context: Context) :
        ViewHelper<TextView, Int>(view, resource, context) {
        init {
            initView()
        }

        override fun initView() = run { view.text = context!!.getText(resource) }
    }

    /**
     * Call this class with a **ImageView** and a **resource** to a drawable
     * and this class will set the image automatically to the **ImageView**.
     *
     * @param context required !
     *
     * @sample ImageViewDrawable.initView
     */
    class ImageViewDrawable(view: ImageView, resource: Int, context: Context) :
        ViewHelper<ImageView, Int>(view, resource, context) {
        init {
            initView()
        }

        override fun initView() =
            run { view.setImageDrawable(ContextCompat.getDrawable(context!!, resource)) }
    }

    /**
     * Call this class with a **ImageView** and a **resource** to a saved Uri
     * and this class will set the image automatically to the **ImageView**.
     *
     * @param context required !
     *
     * @sample ImageViewBitmap.initView
     */
    class ImageViewBitmap(view: ImageView, resource: String, context: Context) :
        ViewHelper<ImageView, String>(view, resource, context) {
        init {
            initView()
        }

        override fun initView() = run { view.setImageBitmap(resource.toImage(context!!)) }
    }

    /**
     * Call this class with a **TextField** and a **resource** with a String in form of
     * [DateTimeFormatter.ISO]
     * and this class will set the text automatically to the **TextField**.
     *
     * @param context required !
     *
     * @sample TextFieldSimpleTime.initView
     */
    class TextFieldSimpleTime(view: TextView, resource: String, context: Context) :
        ViewHelper<TextView, String>(view, resource, context) {
        init {
            initView()
        }

        override fun initView(): Unit =
            run { view.text = TimeHelper.fromCreatedTillNowEasyString(context!!, resource) }
    }

    /**
     * Call this class with a **TextField** and a **resource** with a String in form of
     * [DateTimeFormatter.ISO]
     * and this class will set the text automatically to the **TextField**.
     *
     * @param context required !
     *
     * @sample TextFieldSimpleTime.initView
     */
    class TextFieldDuration(view: TextView, resource: String, context: Context) :
        ViewHelper<TextView, String>(view, resource, context) {
        init {
            initView()
        }

        override fun initView() {
            val mediaPlayer = MediaPlayer.create(context, resource.toUri())
            view.text = mediaPlayer.duration.millisToMinFormat()
        }
    }

    class SoundButton(view: ImageView, resource: String, context: Context) :
        ViewHelper<ImageView, String>(view, resource, context) {
        init {
            initView()
        }

        override fun initView(): Unit = run {
            view.setImageResource(R.drawable.play)
            view.setOnClickListener {
                val mPlayer = MediaPlayer.create(context, resource.toUri())
                mPlayer.start()
            }
        }

    }
}