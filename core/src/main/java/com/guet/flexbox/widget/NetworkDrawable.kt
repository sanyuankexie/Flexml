package com.guet.flexbox.widget

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

internal class NetworkDrawable(
        width: Int,
        height: Int,
        c: Context,
        url: CharSequence)
    : DrawableWrapper<Drawable>(NoOpDrawable()) {

    init {
        Glide.with(c).load(url).into(DrawableTarget(width, height))
    }

    internal inner class DrawableTarget(
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
            wrappedDrawable = resource
            invalidateSelf()
        }
    }
}