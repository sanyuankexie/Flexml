package com.guet.flexbox.widget

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapper

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

internal class NetworkDrawable(
        c: Context,
        url: CharSequence)
    : DrawableWrapper(NoOpDrawable) {

    init {
        Glide.with(c).load(url).into(BackgroundTarget())
    }

    private inner class BackgroundTarget : CustomTarget<Drawable>() {
        override fun onLoadCleared(placeholder: Drawable?) {
            if (placeholder != null) {
                onResourceReady(placeholder, null)
            } else {
                onResourceReady(NoOpDrawable, null)
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