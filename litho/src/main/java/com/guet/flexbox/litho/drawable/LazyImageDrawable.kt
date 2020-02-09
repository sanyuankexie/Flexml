package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import java.util.concurrent.atomic.AtomicBoolean

open class LazyImageDrawable(
        private val context: Context,
        private val model: Any
) : DrawableWrapper<Drawable>(NoOpDrawable()),
        Target<Drawable> by DelegateTarget(),
        ComparableDrawable {

    private val isInit = AtomicBoolean(false)

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is LazyImageDrawable
                && model == other.model
    }

    protected open fun buildRequest(
            builder: RequestBuilder<Drawable>
    ): RequestBuilder<Drawable> {
        return builder
    }

    override fun draw(canvas: Canvas) {
        if (isInit.compareAndSet(false, true)) {
            Glide.with(context)
                    .load(model)
                    .override(bounds.width(), bounds.height())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .let(this::buildRequest)
                    .into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?) {
        resource.bounds = bounds
        wrappedDrawable = resource
        invalidateSelf()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        isInit.set(false)
        wrappedDrawable = NoOpDrawable()
        invalidateSelf()
    }

}