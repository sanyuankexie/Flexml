package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.drawable.load.CornerRadius
import com.guet.flexbox.litho.drawable.load.DelegateTarget
import com.guet.flexbox.litho.drawable.load.DrawableLoaderModule
import com.guet.flexbox.litho.transforms.FastBlur
import kotlin.math.max

class GlideDrawable(
        private val context: Context
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<BitmapDrawable> by DelegateTarget() {
    private var blurSampling: Float = 1f
        set(value) {
            field = max(1f, value)
        }
    private val cacheNoOpDrawable = wrappedDrawable
    private val cbs = ArrayList<SizeReadyCallback>()

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
        bind(bounds.width(), bounds.height())
    }

    fun unbind() {
    }

    fun bind(width: Int, height: Int) {
        val w = (width / blurSampling).toInt()
        val h = (height / blurSampling).toInt()
        cbs.forEach {
            it.onSizeReady(w, h)
        }
    }

    fun mount(
            model: Any,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType,
            leftTop: Float,
            rightTop: Float,
            rightBottom: Float,
            leftBottom: Float
    ) {
        this.blurSampling = blurSampling
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
        if (blurRadius > 0) {
            request = request.transform(FastBlur(blurRadius))
        }
        request.into(this)
    }

    override fun getSize(cb: SizeReadyCallback) {
        cbs.add(cb)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        cbs.remove(cb)
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