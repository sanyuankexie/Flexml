package com.guet.flexbox.transaction.impl

import android.view.View
import com.guet.flexbox.transaction.dispatch.ActionKey
import com.guet.flexbox.transaction.dispatch.Dispatcher
import com.guet.flexbox.transaction.dispatch.ActionTarget

internal class ViewScopeDispatcher(
        private val target: ActionTarget,
        private val scope: View?
) : Dispatcher {

    override fun dispatchAction(
            key: ActionKey,
            args: Array<out Any?>?
    ) {
        target.dispatchAction(
                key,
                scope,
                args
        )
    }

}