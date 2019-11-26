package com.guet.flexbox.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.Touchable
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.build.parseUrl

internal class AsyncMatrixDrawable(
        private val c: Context
) : BorderDrawable<MatrixDrawable>(MatrixDrawable()), Touchable, Target<Drawable> by DelegateTarget() {

    private var width: Int = 0
    private var height: Int = 0
    private var scaleType = ScaleType.FIT_CENTER

    fun mount(
            url: CharSequence,
            width: Int,
            height: Int,
            radius: Int,
            borderWidth: Int,
            borderColor: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {
        this.width = width
        this.height = height
        this.radius = radius
        this.borderWidth = borderWidth
        this.borderColor = borderColor
        this.scaleType = scaleType
        when (val model = parseUrl(c, url.toString())) {
            is ComparableDrawable -> {
                notifyChanged(scaleType, model)
            }
            is CharSequence, is Int -> {
                loadModel(model, blurRadius, blurSampling)
            }
            else -> {
                setToEmpty()
            }
        }
    }

    private fun setToEmpty() {
        wrappedDrawable.mount(NoOpDrawable(), null, 0, 0)
    }

    private fun loadModel(
            model: Any,
            blurRadius: Float,
            blurSampling: Float
    ) {
        Glide.with(c).load(model).apply {
            if (blurRadius > 0) {
                transform(BlurTransformation(
                        blurRadius,
                        blurSampling
                ))
            }
        }.into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        if (placeholder != null) {
            onResourceReady(placeholder, null)
        } else {
            wrappedDrawable.unmount()
        }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        wrappedDrawable.unmount()
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
    ) {
        notifyChanged(scaleType, resource)
    }

    fun unmount() {
        wrappedDrawable.unmount()
        Glide.with(c).clear(this)
    }

    @TargetApi(LOLLIPOP)
    override fun onTouchEvent(event: MotionEvent, host: View): Boolean {
        return wrappedDrawable.onTouchEvent(event, host)
    }

    override fun shouldHandleTouchEvent(event: MotionEvent): Boolean {
        return wrappedDrawable.shouldHandleTouchEvent(event)
    }

    private fun notifyChanged(
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
            drawableWidth = width
            drawableHeight = height
        } else {
            matrix = DrawableMatrix.create(
                    resource,
                    scaleType,
                    width,
                    height
            )
            drawableWidth = resource.intrinsicWidth
            drawableHeight = resource.intrinsicHeight
        }
        wrappedDrawable.mount(
                DelegateTarget.transition(wrappedDrawable.mountedDrawable, resource),
                matrix,
                drawableWidth,
                drawableHeight
        )
    }
}