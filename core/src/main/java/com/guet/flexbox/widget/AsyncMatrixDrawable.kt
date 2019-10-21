package com.guet.flexbox.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.MatrixDrawable

internal class AsyncMatrixDrawable(private val context: Context)
    : BorderDrawable<MatrixDrawable<Drawable>>(MatrixDrawable()) {

    private var layoutWidth: Int = 0
    private var layoutHeight: Int = 0
    private var horizontalPadding: Int = 0
    private var verticalPadding: Int = 0

    private inner class MatrixDrawableTarget(private val scaleType: ScaleType)
        : CustomTarget<Drawable>() {
        override fun onLoadCleared(placeholder: Drawable?) {
            if (placeholder != null) {
                onResourceReady(placeholder, null)
            } else {
                unmount()
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
            wrappedDrawable.mount(resource, matrix)
            wrappedDrawable.bind(drawableWidth, drawableHeight)
            invalidateSelf()
        }
    }

    fun mount(
            url: CharSequence,
            layoutWidth: Int,
            layoutHeight: Int,
            horizontalPadding: Int,
            verticalPadding: Int,
            radius: Float,
            width: Float,
            color: Int,
            scaleType: ScaleType = ScaleType.FIT_XY
    ) {
        this.layoutHeight = layoutHeight
        this.layoutWidth = layoutWidth
        this.horizontalPadding = horizontalPadding
        this.verticalPadding = verticalPadding
        this.radius = radius
        this.width = width
        this.color = color
        Glide.with(context).load(url).into(MatrixDrawableTarget(scaleType))
    }

    fun unmount() {
        wrappedDrawable.unmount()
    }
}
