package com.guet.flexbox.litho.transforms

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool

internal fun getAlphaSafeConfig(
        inBitmap: Bitmap
): Config {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        // Avoid short circuiting the sdk check.
        if (Config.RGBA_F16 == inBitmap.config) {
            // NOPMD
            return Config.RGBA_F16
        }
    }
    return Config.ARGB_8888
}

internal fun getAlphaSafeBitmap(
        pool: BitmapPool,
        maybeAlphaSafe: Bitmap
): Bitmap {
    val safeConfig = getAlphaSafeConfig(maybeAlphaSafe)
    if (safeConfig == maybeAlphaSafe.config) {
        return maybeAlphaSafe
    }
    val argbBitmap = pool[
            maybeAlphaSafe.width,
            maybeAlphaSafe.height,
            safeConfig]
    Canvas(argbBitmap).drawBitmap(
            maybeAlphaSafe,
            0f, 0f,
            null /*paint*/
    )
    // We now own this Bitmap. It's our responsibility
    // to replace it in the pool outside this method
    // when we're finished with it.
    return argbBitmap
}