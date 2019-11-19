package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.litho.drawable.ComparableDrawableWrapper
import java.util.concurrent.atomic.AtomicBoolean

internal class NetworkLazyDrawable(
        private val c: Context,
        private val url: CharSequence)
    : ComparableDrawableWrapper(NoOpDrawable), DrawableTarget {

    private val hasDrawTask = AtomicBoolean(false)

    override fun draw(canvas: Canvas) {
        if (hasDrawTask.compareAndSet(false, true)) {
            Glide.with(c).load(url).into(this)
        } else {
            super.draw(canvas)
        }
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(bounds.width(), bounds.height())
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is NetworkLazyDrawable) {
            return TextUtils.equals(url, other.url)
        }
        return false
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        resource.bounds = bounds
        wrappedDrawable = DrawableTarget.transition(null, resource)
        invalidateSelf()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        hasDrawTask.set(false)
    }
}