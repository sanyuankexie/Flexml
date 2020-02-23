package com.guet.flexbox.event

import android.view.View

interface ActionTarget {
    fun dispatchAction(
            key: ActionKey,
            source: View?,
            args: Array<out Any?>?
    )
}