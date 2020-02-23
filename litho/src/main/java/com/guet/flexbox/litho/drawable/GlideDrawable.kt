package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.load.CornerRadius
import com.guet.flexbox.litho.load.DelegateTarget
import com.guet.flexbox.litho.load.DrawableLoaderModule
import com.guet.flexbox.litho.transforms.FastBlur

class GlideDrawable(
        private val context: Context
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<BitmapDrawable> by DelegateTarget() {
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
                .`as`(BitmapDrawable::class.java)
                .load(model)
                .set(DrawableLoaderModule.scaleType, scaleType)
                .set(DrawableLoaderModule.cornerRadius, CornerRadius(
                        leftTop,
                        rightTop,
                        rightBottom,
                        leftBottom
                ))
        if (blurSampling > 1) {
            request = request.override(
                    (width / blurSampling).toInt(),
                    (height / blurSampling).toInt()
            )
        }
        if (blurRadius > 0) {
            request = request.transform(FastBlur(blurRadius))
        }
        request.into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun onResourceReady(
            resource: BitmapDrawable,
            transition: Transition<in BitmapDrawable>?
    ) {
        wrappedDrawable = resource
        invalidateSelf()
    }

//    private fun wrappedToTransition(target: Drawable): Drawable {
//        val transitionDrawable = TransitionDrawable(arrayOf(cacheNoOpDrawable, target))
//        transitionDrawable.isCrossFadeEnabled = true
//        transitionDrawable.startTransition(200)
//        return transitionDrawable
//    }
}