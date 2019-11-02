package com.guet.flexbox.widget

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.util.Util
import java.lang.ref.SoftReference
import java.util.concurrent.atomic.AtomicBoolean

internal object SharedCanvas : Canvas(), ComponentCallbacks {
    private val isRegistered = AtomicBoolean(false)
    private val bitmapRecycler = Handler(Looper.getMainLooper())
    private val bitmapPool: BitmapPool

    init {
        val dis = Resources.getSystem().displayMetrics
        bitmapPool = LruBitmapPool(Util.getBitmapByteSize(
                dis.widthPixels,
                dis.heightPixels,
                Bitmap.Config.ARGB_8888
        ) * 3L / 2L)
    }

    fun initLowMemoryCallback(c: Context) {
        if (isRegistered.compareAndSet(false, true)) {
            c.applicationContext.registerComponentCallbacks(this)
        }
    }

    internal inline fun draw(
            drawable: Drawable,
            action: (Bitmap) -> Unit
    ) {
        val bounds = drawable.bounds
        if (bounds.width() > 0 && bounds.height() > 0) {
            val bitmap = bitmapPool[
                    bounds.width(),
                    bounds.height(),
                    Bitmap.Config.ARGB_8888
            ]
            setBitmap(bitmap)
            drawable.draw(this)
            action(bitmap)
            setBitmap(null)
            bitmapRecycler.postAtFrontOfQueue(BitmapTracker(bitmap))
        }
    }

    override fun onLowMemory() {
        bitmapPool.clearMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    internal class BitmapTracker(referent: Bitmap)
        : SoftReference<Bitmap>(referent), Runnable {
        override fun run() {
            get()?.let {
                bitmapPool.put(it)
            }
        }
    }
}