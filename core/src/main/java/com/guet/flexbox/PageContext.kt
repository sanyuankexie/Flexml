package com.guet.flexbox

import java.lang.ref.WeakReference

interface PageContext {
    fun send(key: String, vararg data: Any)
}

internal class EventBridge : PageContext {

    private var _target: WeakReference<PageContext>? = null

    var target: PageContext?
        get() = _target?.get()
        set(value) {
            _target = WeakReference<PageContext>(value)
        }

    override fun send(key: String, vararg data: Any) {
        target?.send(key, data)
    }
}

internal class FakePageContext(private val target: PageContext) : PageContext by target