package com.guet.flexbox.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

internal class AsyncDrawable(
        c: Context,
        url: CharSequence,
        private val radius: Float)
    : DrawableWrapper<Drawable>(NoOpDrawable) {
    init {
        Glide.with(c).load(url).into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                if (placeholder != null) {
                    onResourceReady(placeholder, null)
                } else {
                    wrappedDrawable = NoOpDrawable
                }
            }

            override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?) {
                wrappedDrawable = resource
                invalidateSelf()
            }
        })
    }

    private val path = Path()
    private val rectF = RectF()

    override fun getOutline(outline: Outline) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && radius != 0f) {
            outline.setRoundRect(bounds, radius)
        } else {
            super.getOutline(outline)
        }
    }

    override fun draw(canvas: Canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP || radius == 0f) {
            super.draw(canvas)
        } else {
            val sc = canvas.save()
            path.reset()
            path.addRoundRect(rectF.apply {
                set(bounds)
            }, radius, radius, Path.Direction.CW)
            @Suppress("DEPRECATION")
            canvas.clipPath(path, Region.Op.DIFFERENCE)
            super.draw(canvas)
            canvas.restoreToCount(sc)
        }
    }
}