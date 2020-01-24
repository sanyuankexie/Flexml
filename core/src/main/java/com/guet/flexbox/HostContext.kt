package com.guet.flexbox

import android.view.View

abstract class HostContext {

    abstract fun dispatchEvent(key: ActionKey, vararg args: Any?): Any?

    fun toPageContext(source: View? = null): PageContext {
        return PageContext(source, this)
    }

    enum class ActionKey {
        SendObjects,
        RefreshPage,
        HttpRequest
    }
}