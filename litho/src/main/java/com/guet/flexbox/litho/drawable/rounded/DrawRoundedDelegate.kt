package com.guet.flexbox.litho.drawable.rounded

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable

internal abstract class DrawRoundedDelegate {
    private val rectF = RectF()
    private val array = FloatArray(8)
    private val path = Path()
    private var pathIsDirty = true

    fun onBoundChanged() {
        pathIsDirty = true
    }

    protected open fun <T> buildPath(
            drawable: T,
            path: Path
    ) where T : Drawable, T : RoundedRadius {
        path.apply {
            reset()
            if (drawable.hasRounded) {
                addRoundRect(
                        rectF.apply {
                            set(drawable.bounds)
                        },
                        drawable.toRadiiArray(array),
                        Path.Direction.CW
                )
            } else {
                addRect(
                        rectF.apply {
                            set(drawable.bounds)
                        },
                        Path.Direction.CW
                )
            }
            close()
        }
    }

    fun <T> buildPathIfDirty(
            drawable: T
    ): Path where T : Drawable, T : RoundedRadius {
        if (pathIsDirty) {
            buildPath(drawable, path)
        }
        return path
    }

    abstract fun draw(canvas: Canvas)
}