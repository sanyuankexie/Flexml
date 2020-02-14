package com.guet.flexbox.litho.drawable

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition

internal class DelegateTarget : BaseTarget<Drawable>() {
    override fun getSize(cb: SizeReadyCallback) {
    }

    override fun removeCallback(cb: SizeReadyCallback) {
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
    }
}
