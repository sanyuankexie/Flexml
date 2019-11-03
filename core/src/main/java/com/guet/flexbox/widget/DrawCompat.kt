package com.guet.flexbox.widget

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.MainThread
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.util.Util
import java.util.concurrent.atomic.AtomicBoolean

internal object DrawCompat {
    private const val BITMAP_TRACKER = 10000
    private val canvas = Canvas()
    private val isRegistered = AtomicBoolean(false)
    private val bitmapRecycler = Handler(Looper.getMainLooper(), CallbacksImpl)
    private val bitmapPool: LruBitmapPool

    init {
        val dis = Resources.getSystem().displayMetrics
        bitmapPool = LruBitmapPool(Util.getBitmapByteSize(
                dis.widthPixels,
                dis.heightPixels,
                Bitmap.Config.ARGB_8888
        ).toLong())
    }

    internal fun ensureInit(c: Context) {
        if (isRegistered.compareAndSet(false, true)) {
            c.applicationContext.registerComponentCallbacks(CallbacksImpl)
        }
    }

    @MainThread
    internal inline fun drawToBitmap(drawable: Drawable, action: (Bitmap) -> Unit) {
        val bounds = drawable.bounds
        if (bounds.width() > 0 && bounds.height() > 0) {
            val bitmap = bitmapPool[
                    bounds.width(),
                    bounds.height(),
                    Bitmap.Config.ARGB_8888
            ]
            canvas.setBitmap(bitmap)
            drawable.draw(canvas)
            action(bitmap)
            canvas.setBitmap(null)
            bitmapRecycler.sendMessageAtFrontOfQueue(Message.obtain()
                    .apply {
                        what = BITMAP_TRACKER
                        obj = bitmap
                    })
        }
    }

    private object CallbacksImpl : ComponentCallbacks2, Handler.Callback {

        override fun handleMessage(msg: Message): Boolean {
            val bitmap = msg.obj
            if (bitmap is Bitmap) {
                bitmapPool.put(bitmap)
            }
            return true
        }

        override fun onTrimMemory(level: Int) {
            bitmapRecycler.removeMessages(BITMAP_TRACKER)
            bitmapPool.trimMemory(level)
        }

        override fun onLowMemory() {
            bitmapRecycler.removeMessages(BITMAP_TRACKER)
            bitmapPool.clearMemory()
        }

        override fun onConfigurationChanged(newConfig: Configuration?) {}
    }
}