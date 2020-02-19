package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.bitmap.CornerRadius
import com.guet.flexbox.litho.bitmap.GlideConstants
import com.guet.flexbox.litho.drawable.GlideDrawable.Companion.post
import com.guet.flexbox.litho.transforms.FastBlur

class GlideDrawable(
        private val context: Context
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<ExBitmapDrawable> by DelegateTarget() {

    companion object : Handler(Looper.getMainLooper())

    private val cacheNoOpDrawable = wrappedDrawable
    private var drawableWidth: Int = 0
    private var drawableHeight: Int = 0

    fun unmount() {
        post { Glide.with(context).clear(this) }
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
        drawableWidth = bounds.width()
        drawableHeight = bounds.height()
    }

    fun unbind() {
    }

    fun bind(width: Int, height: Int) {
        this.drawableWidth = width
        this.drawableHeight = height
    }

    private class MountRunnable(
            private val context: Context,
            private val target: Target<ExBitmapDrawable>,
            private val model: Any,
            private val width: Int,
            private val height: Int,
            private val scaleType: ScaleType,
            private val cornerRadius: CornerRadius,
            private val fastBlur: FastBlur?
    ) : Runnable {
        override fun run() {
            var request = Glide.with(context)
                    .`as`(ExBitmapDrawable::class.java)
                    .load(model)
                    .set(GlideConstants.scaleType, scaleType)
                    .set(GlideConstants.cornerRadius, cornerRadius)
                    .override(width, height)
            if (fastBlur != null) {
                request = request.transform(fastBlur)
            }
            request.into(target)
        }
    }

    fun mount(
            model: Any,
            width: Int,
            height: Int,
            blurRadius: Float,
            blurSampling: Float,
            scaleType: ScaleType,
            leftTopRadius: Float,
            rightTopRadius: Float,
            rightBottomRadius: Float,
            leftBottomRadius: Float
    ) {
        drawableWidth = width
        drawableHeight = height
        val cornerRadius = CornerRadius(
                leftTopRadius,
                rightTopRadius,
                rightBottomRadius,
                leftBottomRadius
        )
        var requestWidth = drawableWidth
        var requestHeight = drawableHeight
        if (blurSampling > 1) {
            requestWidth = (width / blurSampling).toInt()
            requestHeight = (height / blurSampling).toInt()
        }
        var fastBlur: FastBlur? = null
        if (blurRadius > 0) {
            fastBlur = FastBlur(blurRadius)
        }
        post(MountRunnable(
                context,
                this,
                model,
                requestWidth,
                requestHeight,
                scaleType,
                cornerRadius,
                fastBlur
        ))
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(drawableWidth, drawableHeight)
    }

    override fun onResourceReady(
            resource: ExBitmapDrawable,
            transition: Transition<in ExBitmapDrawable>?
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