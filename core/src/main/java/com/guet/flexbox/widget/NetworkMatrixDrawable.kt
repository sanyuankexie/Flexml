package com.guet.flexbox.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.MatrixDrawable
import com.facebook.litho.Touchable

internal class NetworkMatrixDrawable(c: Context) : BorderDrawable<MatrixDrawable<Drawable>>(MatrixDrawable()), Touchable {
    private val c: Context = c.applicationContext
    private var layoutWidth: Int = 0
    private var layoutHeight: Int = 0
    private var horizontalPadding: Int = 0
    private var verticalPadding: Int = 0

    fun mount(
            url: CharSequence,
            layoutWidth: Int,
            layoutHeight: Int,
            horizontalPadding: Int,
            verticalPadding: Int,
            radius: Int,
            width: Int,
            color: Int,
            blurRadius: Int,
            blurSampling: Int,
            scaleType: ScaleType
    ) {
        this.layoutHeight = layoutHeight
        this.layoutWidth = layoutWidth
        this.horizontalPadding = horizontalPadding
        this.verticalPadding = verticalPadding
        this.radius = radius
        this.width = width
        this.color = color
        Glide.with(c).load(url)
                .apply {
                    if (blurRadius > 0) {
                        transform(BlurTransformation(
                                c,
                                blurRadius,
                                blurSampling
                        ))
                    }
                }.into(DrawableTarget(scaleType))
    }

    fun unmount() {
        wrappedDrawable.unmount()
    }

    override fun onTouchEvent(event: MotionEvent?, host: View?): Boolean {
        return wrappedDrawable.onTouchEvent(event, host)
    }

    override fun shouldHandleTouchEvent(event: MotionEvent?): Boolean {
        return wrappedDrawable.shouldHandleTouchEvent(event)
    }

    private inner class DrawableTarget(private val scaleType: ScaleType) : CustomTarget<Drawable>() {

        override fun onLoadCleared(placeholder: Drawable?) {
            if (placeholder != null) {
                onResourceReady(placeholder, null)
            } else {
                wrappedDrawable.unmount()
            }
        }

        override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
        ) {
            val drawableWidth: Int
            val drawableHeight: Int
            val matrix: DrawableMatrix?
            if (ScaleType.FIT_XY == scaleType
                    || resource.intrinsicWidth <= 0
                    || resource.intrinsicHeight <= 0) {
                matrix = null
                drawableWidth = layoutWidth - horizontalPadding
                drawableHeight = layoutHeight - verticalPadding
            } else {
                matrix = DrawableMatrix.create(
                        resource,
                        scaleType,
                        layoutWidth - horizontalPadding,
                        layoutHeight - verticalPadding)
                drawableWidth = resource.intrinsicWidth
                drawableHeight = resource.intrinsicHeight
            }
            wrappedDrawable.mount(NetworkDrawable.transition(
                    wrappedDrawable.mountedDrawable,
                    resource
            ), matrix)
            wrappedDrawable.bind(drawableWidth, drawableHeight)
            invalidateSelf()
        }
    }
}