package com.guet.flexbox.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.Touchable

internal class NetworkMatrixDrawable(c: Context)
    : BorderDrawable<MatrixDrawable>(MatrixDrawable()), Touchable, WrapperTarget {

    private val c: Context = c.applicationContext
    private var layoutWidth: Int = 0
    private var layoutHeight: Int = 0
    private var horizontalPadding: Int = 0
    private var verticalPadding: Int = 0
    private var scaleType = ScaleType.FIT_CENTER

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
        this.scaleType = scaleType
        if (TextUtils.isEmpty(url)) {
            notifyChanged(scaleType, ColorDrawable(Color.TRANSPARENT))
        } else {
            Glide.with(c).load(url).apply {
                if (blurRadius > 0) {
                    transform(BlurTransformation(
                            c,
                            blurRadius,
                            blurSampling
                    ))
                }
            }.into(this)
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(
                layoutWidth - horizontalPadding,
                layoutHeight - verticalPadding
        )
    }

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

    fun unmount() {
        wrappedDrawable.unmount()
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
                WrapperTarget.transition(wrappedDrawable.mountedDrawable, resource),
                matrix,
                drawableWidth,
                drawableHeight
        )
    }
}