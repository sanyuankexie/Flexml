package com.guet.flexbox.playground.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

class BlurLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
        ViewTreeObserver.OnPreDrawListener {

    private val rect = Rect()

    private val canvas = Canvas()

    private val inBlur = AtomicBoolean(false)

    private var buffer: Bitmap? = null

    init {
        setWillNotDraw(false)
        viewTreeObserver.addOnPreDrawListener(this)
    }

    private fun blurTheFrame() {
        getGlobalVisibleRect(rect)
        if (rect.width() * rect.height() == 0) {
            inBlur.set(false)
            return
        }
        val context = context.applicationContext
        val bitmapPool = Glide.get(context).bitmapPool
        val myBuffer = bitmapPool[
                rect.width(),
                rect.height(),
                Bitmap.Config.ARGB_8888
        ]
        canvas.setBitmap(myBuffer)
        canvas.translate(
                -rect.left.toFloat(),
                -rect.top.toFloat()
        )
        rootView.draw(canvas)
        canvas.translate(
                rect.left.toFloat(),
                rect.top.toFloat()
        )
        canvas.setBitmap(null)
        blurThread.post {
            postToDraw(blurBuffer(context, myBuffer))
        }
    }

    private fun postToDraw(bitmap: Bitmap) {
        post {
            val oldBuffer = buffer
            buffer = bitmap
            val bitmapPool = Glide.get(context).bitmapPool
            if (oldBuffer != null) {
                bitmapPool.put(oldBuffer)
            }
            invalidate()
            inBlur.set(false)
        }
    }

    override fun onPreDraw(): Boolean {
        if (inBlur.compareAndSet(false, true)) {
            blurTheFrame()
        }
        return true
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (this.canvas !== canvas) {
            super.dispatchDraw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val myBuffer = buffer ?: return
        if (this.canvas !== canvas) {
            rect.set(0, 0, width, height)
            canvas.drawBitmap(myBuffer, null, rect, null)
        }
    }

    private companion object {

        private val blurThread = Handler(HandlerThread("blurThread")
                .apply {
                    start()
                }.looper)

        private fun blurBuffer(
                context: Context,
                bitmap: Bitmap
        ): Bitmap {
            val radius = max(0f, min(15f, 25f))
            var rs: RenderScript? = null
            var input: Allocation? = null
            var output: Allocation? = null
            var blur: ScriptIntrinsicBlur? = null
            try {
                rs = RenderScript.create(context)
                rs.messageHandler = RenderScript.RSMessageHandler()
                input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                        Allocation.USAGE_SCRIPT)
                output = Allocation.createTyped(rs, input.type)
                blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

                blur.setInput(input)
                blur.setRadius(radius)
                blur.forEach(output)
                output.copyTo(bitmap)
            } finally {
                rs?.destroy()
                input?.destroy()
                output?.destroy()
                blur?.destroy()
            }
            return bitmap
        }
    }
}

