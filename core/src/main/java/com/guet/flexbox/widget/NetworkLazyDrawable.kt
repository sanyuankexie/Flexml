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
        c: Context,
        private val url: CharSequence)
    : ComparableDrawableWrapper(NoOpDrawable()), WrapperTarget {

    private val trigger = AtomicBoolean(false)
    private val context = c.applicationContext

    override fun draw(canvas: Canvas) {
        if (trigger.compareAndSet(false, true)) {
            Glide.with(context).load(url).into(this)
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
        wrappedDrawable = WrapperTarget.transition(null, resource)
        invalidateSelf()
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        if (placeholder != null) {
            onResourceReady(placeholder, null)
        } else {
            onResourceReady(NoOpDrawable(), null)
        }
    }
}