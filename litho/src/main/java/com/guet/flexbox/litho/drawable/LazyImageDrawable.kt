package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.litho.drawable.load.CornerRadius
import com.guet.flexbox.litho.drawable.load.DelegateTarget
import com.guet.flexbox.litho.drawable.load.DrawableLoaderModule
import java.util.concurrent.atomic.AtomicBoolean

class LazyImageDrawable private constructor(
        private val context: Context,
        private val model: Any,
        private val radius: CornerRadius
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<BitmapDrawable> by DelegateTarget(),
        ComparableDrawable {

    private val cacheNoOpDrawable = wrappedDrawable

    constructor(
            context: Context,
            model: Any,
            leftTop: Float,
            rightTop: Float,
            rightBottom: Float,
            leftBottom: Float
    ) : this(
            context, model,
            CornerRadius(
                    leftTop,
                    rightTop,
                    rightBottom,
                    leftBottom
            )
    )

    constructor(
            context: Context,
            model: Any,
            radius: Float
    ) : this(context, model, CornerRadius(radius))

    constructor(
            context: Context,
            model: Any
    ) : this(context, model, 0f)

    private val isInit = AtomicBoolean(false)

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is LazyImageDrawable
                && model == other.model
                && radius == other.radius
    }

    override fun draw(canvas: Canvas) {
        if (isInit.compareAndSet(false, true)) {
            Glide.with(context)
                    .`as`(BitmapDrawable::class.java)
                    .load(model)
                    .set(DrawableLoaderModule.scaleType, ScaleType.FIT_XY)
                    .set(DrawableLoaderModule.cornerRadius, radius)
                    .into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(bounds.width(), bounds.height())
    }

    override fun onResourceReady(
            resource: BitmapDrawable,
            transition: Transition<in BitmapDrawable>?) {
        wrappedDrawable = resource
        invalidateSelf()
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        isInit.set(false)
        wrappedDrawable = cacheNoOpDrawable
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        onLoadCleared(null)
    }

//    private fun wrappedToTransition(target: Drawable): Drawable {
//        val transitionDrawable = TransitionDrawable(arrayOf(cacheNoOpDrawable, target))
//        transitionDrawable.isCrossFadeEnabled = true
//        transitionDrawable.startTransition(200)
//        return transitionDrawable
//    }

}