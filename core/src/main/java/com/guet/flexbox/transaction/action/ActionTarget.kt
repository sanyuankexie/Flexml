package com.guet.flexbox.transaction.action

import android.view.View
import com.guet.flexbox.transaction.ActionKey

interface ActionTarget {
    fun dispatchActions(
            key: ActionKey,
            source: View?,
            args: Array<out Any?>?
    )
}