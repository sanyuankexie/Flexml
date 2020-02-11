package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        Glide.with(context).clear(this)
        wrappedDrawable = cacheNoOpDrawable
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        wrappedDrawable = cacheNoOpDrawable
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
            leftBottom: Float
    ) {
        this.width = width
        this.height = height
        var request = Glide.with(context)
                .load(model)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        val tf = OffScreenRender.Builder {
            this.blurRadius = blurRadius
            this.blurSampling = blurSampling
            this.scaleType = scaleType
            this.leftTop = leftTop
            this.rightTop = rightTop
            this.rightBottom = rightBottom
            this.leftBottom = leftBottom
        }.build()
        if (tf != null) {
            request = request.transform(tf)
        }
        request.into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?
    ) {
        wrappedDrawable = wrappedToTransition(resource)
        invalidateSelf()
    }

    private fun wrappedToTransition(target: Drawable): Drawable {
        val transitionDrawable = TransitionDrawable(arrayOf(cacheNoOpDrawable, target))
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(200)
        return transitionDrawable
    }
}