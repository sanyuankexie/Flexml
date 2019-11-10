package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.Touchable

internal class NetworkMatrixDrawable(c: Context)
    : BorderDrawable<MatrixDrawable>(MatrixDrawable()), Touchable {
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
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {
        this.layoutHeight = layoutHeight
        this.layoutWidth = layoutWidth
        this.horizontalPadding = horizontalPadding
        this.verticalPadding = verticalPadding
        this.radius = radius
        this.width = width
        this.color = color
        if (TextUtils.isEmpty(url)) {
            notifyChanged(scaleType, ColorDrawable(Color.TRANSPARENT))
        } else {
            Glide.with(c).load(url)
                    .apply {
                        if (blurRadius > 0) {
                            transform(BlurTransformation(
                                    c,
                                    blurRadius,
                                    blurSampling
                            ))
                        }
                    }.into(DrawableTarget(
                            layoutWidth - horizontalPadding,
                            layoutHeight - verticalPadding,
                            scaleType
                    ))
        }
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

    internal fun notifyChanged(
            scaleType: ScaleType,
            resource: Drawable
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
        wrappedDrawable.mount(
                transition(wrappedDrawable.mountedDrawable, resource),
                matrix,
                drawableWidth,
                drawableHeight
        )
    }

    internal inner class DrawableTarget(
            width: Int,
            height: Int,
            private val scaleType: ScaleType
    ) : CustomTarget<Drawable>(width, height) {

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
            notifyChanged(scaleType, resource)
        }
    }

    internal companion object {
        internal fun transition(current: Drawable?, next: Drawable): Drawable {
            val transitionDrawable = TransitionDrawable(arrayOf(
                    current ?: ColorDrawable(Color.TRANSPARENT), next
            ))
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(200)
            return transitionDrawable
        }
    }
}