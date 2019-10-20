package com.guet.flexbox.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.facebook.litho.MatrixDrawable

class AsyncMatrixDrawable(private val context: Context) : MatrixDrawable<Drawable>() {
    private var layoutWidth: Int = 0
    private var layoutHeight: Int = 0
    private var horizontalPadding: Int = 0
    private var verticalPadding: Int = 0
    private var radius: Float = 0f
    private val path = Path()
    private val rectF = RectF()

    private inner class DrawableTarget(
            width: Int,
            height: Int,
            private val scaleType: ImageView.ScaleType)
        : CustomTarget<Drawable>(width, height) {
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
            if (ImageView.ScaleType.FIT_XY == scaleType
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
            mount(resource, matrix)
            bind(drawableWidth, drawableHeight)
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
            scaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_XY
    ) {
        this.layoutHeight = layoutHeight
        this.layoutWidth = layoutWidth
        this.horizontalPadding = horizontalPadding
        this.verticalPadding = verticalPadding
        this.radius = radius
        Glide.with(context).load(url).into(DrawableTarget(
                layoutWidth - horizontalPadding,
                layoutHeight - verticalPadding,
                scaleType))
    }

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
