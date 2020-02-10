package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.transforms.OffScreenRender

class GlideDrawable(
        private val context: Context
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<Drawable> by DelegateTarget() {
    private val cacheNoOpDrawable = wrappedDrawable
    private var width: Int = 0
    private var height: Int = 0

    fun unmount() {
        wrappedDrawable = cacheNoOpDrawable
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        unmount()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        wrappedDrawable = cacheNoOpDrawable
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        width = bounds.width()
        height = bounds.height()
    }

    fun unbind() {
    }

    fun bind(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun mount(
            model: Any,
            width: Int,
            height: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType,
            leftTop: Float,
            rightTop: Float,
            rightBottom: Float,
            lightBottom: Float
    ) {
        this.width = width
        this.height = height
        Glide.with(context)
                .load(model)
                .transform(
                        OffScreenRender.Builder {
                            this.blurRadius = blurRadius
                            this.blurSampling = blurSampling
                            this.scaleType = scaleType
                            this.leftTop = leftTop
                            this.rightTop = rightTop
                            this.rightBottom = rightBottom
                            this.leftBottom = lightBottom
                        }.build()
                ).into(this)
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