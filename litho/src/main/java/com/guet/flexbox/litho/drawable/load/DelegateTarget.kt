package com.guet.flexbox.litho.drawable.load

import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition

open class DelegateTarget<T> : BaseTarget<T>() {
    override fun getSize(cb: SizeReadyCallback) {
    }

    override fun removeCallback(cb: SizeReadyCallback) {
    }

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
    }
}
