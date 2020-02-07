package com.guet.flexbox.litho.widget

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

internal class NetworkMatrixDrawable(
        private val c: Context
) : DrawableWrapper<MatrixDrawable>(MatrixDrawable()),
        Touchable,
        Target<Drawable> by DelegateTarget() {
    private var width: Int = 0
    private var height: Int = 0
    private var scaleType = ScaleType.FIT_CENTER

    fun mount(
            resId: Int,
            width: Int,
            height: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {


        this.width = width
        this.height = height
        this.scaleType = scaleType
        if (resId != 0) {
            Glide.with(c).load(resId).apply {
                if (blurRadius > 0 && blurSampling > 0) {
                    transform(FastBlur(
                            blurRadius,
                            blurSampling
                    ))
                }
            }.into(this)
        } else {
            wrappedDrawable.mount(NoOpDrawable(), null, 0, 0)
        }
    }

    fun mount(drawable: Drawable,
              width: Int,
              height: Int,
              blurRadius: Float,
              blurSampling: Float,
              scaleType: ScaleType
    ) {
        this.width = width
        this.height = height
        this.scaleType = scaleType
        if (blurRadius > 0 && blurSampling > 0) {
            Glide.with(c).load(drawable)
                    .transform(FastBlur(
                            blurRadius,
                            blurSampling
                    ))
                    .into(this)
        } else {
            notifyChanged(scaleType, drawable)
        }
    }

    fun mount(
            url: CharSequence,
            width: Int,
            height: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {



        this.width = width
        this.height = height
        this.scaleType = scaleType
        if (url.isNotEmpty()) {
            Glide.with(c).load(url).apply {
                if (blurRadius > 0 && blurSampling > 0) {
                    transform(FastBlur(
                            blurRadius,
                            blurSampling
                    ))
                }
            }.into(this)
        } else {
            wrappedDrawable.mount(NoOpDrawable(), null, 0, 0)
        }
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
                resource,
                matrix,
                drawableWidth,
                drawableHeight
        )
    }
}