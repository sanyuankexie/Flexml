package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.drawable.DelegateTarget
import com.guet.flexbox.litho.drawable.DrawableWrapper
import com.guet.flexbox.litho.drawable.NoOpDrawable
import com.guet.flexbox.litho.transforms.FastBlur
import com.guet.flexbox.litho.transforms.ImageScale

class GlideDrawable(
        private val context: Context
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<Drawable> by DelegateTarget() {
    private var width: Int = 0
    private var height: Int = 0

    fun unmount() {
        width = 0
        height = 0
        wrappedDrawable = NoOpDrawable()
    }

    fun mount(
            model: Any,
            width: Int,
            height: Int,
            radiusArray: FloatArray,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType
    ) {
        this.width = width
        this.height = height
        Glide.with(context)
                .load(model)
                .transform(
                        ImageScale(
                                scaleType
                        ),
                        FastBlur(
                                blurRadius,
                                blurSampling
                        ),
                        GranularRoundedCorners(
                                radiusArray[0],
                                radiusArray[1],
                                radiusArray[2],
                                radiusArray[3]
                        )
                )
                .into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
    ) {
        resource.bounds = bounds
        wrappedDrawable = resource
        invalidateSelf()
    }

}