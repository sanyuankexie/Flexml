package com.guet.flexbox.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import java.util.*

internal interface DrawableTarget : Target<Drawable> {

    override fun onLoadFailed(errorDrawable: Drawable?) {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun onStart() {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun onStop() {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun onDestroy() {
        // Intentionally empty, this can be optionally implemented by subclasses.
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        // Do nothing, this class does not retain SizeReadyCallbacks.
    }

    override fun setRequest(request: Request?) {
        requests[this] = request
    }

    override fun getRequest(): Request? {
        return requests[this]
    }

    companion object  {

        internal val requests = Collections
                .synchronizedMap(WeakHashMap<DrawableTarget, Request>())

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
