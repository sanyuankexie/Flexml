package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class LazyImageDrawable private constructor(
        context: Context,
        private val model: Any,
        private val radiusArray: FloatArray
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<Drawable> by DelegateTarget(),
        ComparableDrawable {

    private companion object {
        private val emptyArray = FloatArray(0)
    }

    private val cacheNoOpDrawable = wrappedDrawable

    private val weakContext = WeakReference<Context>(context)

    constructor(
            context: Context,
            model: Any,
            leftTop: Float,
            rightTop: Float,
            rightBottom: Float,
            leftBottom: Float
    ) : this(
            context, model,
            floatArrayOf(leftTop, rightTop, rightBottom, leftBottom)
    )

    constructor(
            context: Context,
            model: Any,
            radius: Float
    ) : this(context, model, floatArrayOf(radius))

    constructor(
            context: Context,
            model: Any
    ) : this(context, model, emptyArray)

    private val isInit = AtomicBoolean(false)

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is LazyImageDrawable
                && model == other.model
                && radiusArray.contentEquals(other.radiusArray)
    }

    override fun draw(canvas: Canvas) {
        val context = weakContext.get()
        if (context != null && isInit.compareAndSet(false, true)) {
            var request = Glide.with(context)
                    .load(model)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(bounds.width(), bounds.height())
            if (radiusArray.size == 4) {
                request = request.transform(
                        GranularRoundedCorners(
                                radiusArray[0],
                                radiusArray[1],
                                radiusArray[2],
                                radiusArray[3]
                        )
                )
            } else if (radiusArray.size == 1) {
                request = request.transform(
                        RoundedCorners(radiusArray[0].toInt())
                )
            }
            request.into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(bounds.width(), bounds.height())
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?) {
        wrappedDrawable = wrappedToTransition(resource)
        invalidateSelf()
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        isInit.set(false)
        wrappedDrawable = cacheNoOpDrawable
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        onLoadCleared(null)
    }

    private fun wrappedToTransition(target: Drawable): Drawable {
        val transitionDrawable = TransitionDrawable(arrayOf(cacheNoOpDrawable, target))
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(200)
        return transitionDrawable
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        val context = weakContext.get()
        if (context != null) {
            Glide.with(context).clear(this)
        }
    }
}