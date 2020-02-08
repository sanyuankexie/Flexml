package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.DrawableMatrix
import com.guet.flexbox.litho.transforms.FastBlur

class MatrixGlideDrawable(
        private val context: Context
) : MatrixDrawable(), Target<Drawable> by DelegateTarget() {
    private var width: Int = 0
    private var height: Int = 0
    private var scaleType = ScaleType.FIT_CENTER

    fun mount(
            model: Any,
            width: Int,
            height: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType,
            lt: Float,
            rt: Float,
            rb: Float,
            lb: Float
    ) {
        this.width = width
        this.height = height
        this.scaleType = scaleType
        val needRoundedCorners = lt != 0f || rb != 0f || lb != 0f || rt != 0f
        var request = Glide.with(context)
                .load(model)
        var transforms: ArrayList<Transformation<Bitmap>>? = null
        if (needRoundedCorners) {
            transforms = ArrayList()
            transforms.add(GranularRoundedCorners(
                    lt,
                    rt,
                    rb,
                    lb
            ))
        }
        if (blurRadius <= 0 || blurSampling < 1) {
            if (transforms == null) {
                transforms = ArrayList()
            }
            transforms.add(FastBlur(blurRadius, blurSampling))
        }
        if (!transforms.isNullOrEmpty()) {
            request = request.transform(*transforms.toTypedArray())
        }
        request.into(this)
    }

    fun bind(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun unmount() {
        super.unmount()
        Glide.with(context).clear(this)
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
    ) {
        notifyChanged(resource)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    private fun notifyChanged(resource: Drawable) {
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
        mount(
                resource,
                matrix,
                drawableWidth,
                drawableHeight
        )
    }
}