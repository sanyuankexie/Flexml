package com.guet.flexbox.transaction.impl

import android.view.View
import com.guet.flexbox.transaction.ActionKey
import com.guet.flexbox.transaction.Dispatcher
import com.guet.flexbox.transaction.action.ActionTarget

internal class ViewScopeDispatcher(
        private val target: ActionTarget,
        private val scope: View?
) : Dispatcher {

    override fun dispatchActions(
            key: ActionKey,
            args: Array<out Any?>?
    ) {
        target.dispatchActions(
                key,
                scope,
                args
        )
    }

}