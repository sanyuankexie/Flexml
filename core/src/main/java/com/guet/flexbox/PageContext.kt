package com.guet.flexbox

import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import java.lang.ref.WeakReference

abstract class PageContext {
    abstract fun send(key: String, vararg data: Any?)

    abstract fun http(): HttpTransaction?

    abstract fun refresh(): RefreshTransaction?
}

internal abstract class ProxyContext : PageContext() {

    internal abstract var target: PageContext?

    override fun send(key: String, vararg data: Any?) {
        target?.send(key, data)
    }

    override fun http(): HttpTransaction? {
        return target?.http()
    }

    override fun refresh(): RefreshTransaction? {
        return target?.refresh()
    }
}

internal class EventBridge : ProxyContext() {

    private var _target: WeakReference<PageContext>? = null

    override var target: PageContext?
        get() = _target?.get()
        set(value) {
            _target = WeakReference<PageContext>(value)
        }

    override fun send(key: String, vararg data: Any?) {
        target?.send(key, data)
    }
}

internal class JoinPageContext(
        private var _target: PageContext?,
        private vararg val args: Any
) : ProxyContext() {

    override var target: PageContext?
        get() = _target
        set(value) {
            _target = value
        }

    override fun send(key: String, vararg data: Any?) {
        val array = Array<Any?>(data.size + 1) {}
        for (index in data.indices) {
            array[index] = data[index]
        }
        array[array.size - 1] = args
        target?.send(key, array)
    }
}