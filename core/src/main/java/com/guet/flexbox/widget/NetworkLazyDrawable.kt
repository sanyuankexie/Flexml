package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.litho.drawable.ComparableDrawableWrapper
import java.util.concurrent.atomic.AtomicBoolean

internal class NetworkLazyDrawable(
        c: Context,
        private val width: Int,
        private val height: Int,
        private val url: CharSequence)
    : ComparableDrawableWrapper(NoOpDrawable()) {

    private val loaded = AtomicBoolean(false)
    private val context = c.applicationContext

    override fun draw(canvas: Canvas) {
        if (loaded.compareAndSet(false, true)) {
            Glide.with(context).load(url)
                    .into(LazyTarget(width, height))
        } else {
            super.draw(canvas)
        }
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is NetworkLazyDrawable) {
            return width == other.width
                    && height == other.height
                    && TextUtils.equals(url, other.url)
        }
        return false
    }

    internal inner class LazyTarget(
            width: Int,
            height: Int
    ) : CustomTarget<Drawable>(width, height) {
        override fun onLoadCleared(placeholder: Drawable?) {
            if (placeholder != null) {
                onResourceReady(placeholder, null)
            } else {
                onResourceReady(NoOpDrawable(), null)
            }
        }

        override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?) {
            resource.bounds = bounds
            wrappedDrawable = NetworkMatrixDrawable.transition(null, resource)
            invalidateSelf()
        }
    }
}