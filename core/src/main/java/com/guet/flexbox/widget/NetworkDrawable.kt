package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

internal class NetworkDrawable(
        c: Context,
        url: CharSequence)
    : DrawableWrapper<Drawable>(NoOpDrawable) {

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
            wrappedDrawable = transition(null, resource)
            invalidateSelf()
        }
    }

    internal companion object {
        internal fun transition(current: Drawable?, next: Drawable): Drawable {
            val transitionDrawable = TransitionDrawable(arrayOf(
                    current ?: ColorDrawable(Color.TRANSPARENT), next
            ))
            transitionDrawable.isCrossFadeEnabled = true
            transitionDrawable.startTransition(200)
            return transitionDrawable
        }
    }
}