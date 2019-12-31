package com.guet.flexbox

import java.lang.ref.WeakReference

abstract class PageContext {
    abstract fun send(key: String, vararg data: Any)
}

internal class EventBridge : PageContext() {

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

internal class FakePageContext(private val target: PageContext) : PageContext() {
    override fun send(key: String, vararg data: Any) {
        target.send(key, data)
    }
}

internal class JoinPageContext(
        private val target: PageContext,
        private vararg val args: Any
) : PageContext() {
    override fun send(key: String, vararg data: Any) {
        target.send(key, args.zip(data))
    }
}