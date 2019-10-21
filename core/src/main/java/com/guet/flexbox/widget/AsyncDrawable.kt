package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

internal class AsyncDrawable(
        c: Context,
        url: CharSequence,
        radius: Float = 0f,
        width: Float = 0f,
        color: Int = Color.TRANSPARENT)
    : BorderDrawable<Drawable>(NoOpDrawable, radius, width, color) {
    init {
        Glide.with(c).load(url).into(object : CustomTarget<Drawable>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                if (placeholder != null) {
                    onResourceReady(placeholder, null)
                } else {
                    wrappedDrawable = NoOpDrawable
                    invalidateSelf()
                }
            }

            override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?) {
                resource.bounds = bounds
                wrappedDrawable = resource
                invalidateSelf()
            }
        })
    }
}