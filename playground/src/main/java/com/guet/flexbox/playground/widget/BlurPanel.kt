package com.guet.flexbox.playground.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.guet.flexbox.litho.widget.BlurTransformation

class BlurPanel @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val canvas: Canvas = Canvas()

    private var bitmap: Bitmap? = null

    init {
        scaleType = ScaleType.FIT_XY
        viewTreeObserver.addOnPreDrawListener {
            rootView.draw(canvas)
            Glide.with(this)
                    .load(bitmap)
                    .transform(BlurTransformation(10f,0.5f))
                    .into(this)
            return@addOnPreDrawListener true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val oldBitmap = bitmap
        if (oldBitmap != null) {
            val requestSize = w * h * Int.SIZE_BYTES
            if (oldBitmap.allocationByteCount >= requestSize) {
                oldBitmap.reconfigure(w, h, Bitmap.Config.ARGB_8888)
                return
            } else {
                canvas.setBitmap(null)
                oldBitmap.recycle()
            }
        }
        val newBitmap = Bitmap.createBitmap(
                w, h,
                Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(newBitmap)
        bitmap = newBitmap
    }
}