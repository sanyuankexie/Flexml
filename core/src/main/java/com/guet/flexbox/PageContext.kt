package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import java.lang.ref.WeakReference
import java.util.*

abstract class PageContext {

    abstract fun send(vararg values: Any?)

    abstract fun http(): HttpTransaction?

    abstract fun refresh(): RefreshTransaction?

}

internal abstract class ForwardContext : PageContext() {

    internal abstract var target: PageContext?

    override fun send(vararg values: Any?) {
        target?.send(values)
    }

    override fun http(): HttpTransaction? {
        return target?.http()
    }

    override fun refresh(): RefreshTransaction? {
        return target?.refresh()
    }
}

internal class EventBridge : ForwardContext() {

    private var _target: WeakReference<PageContext>? = null

    override var target: PageContext?
        get() = _target?.get()
        set(value) {
            _target = WeakReference<PageContext>(value)
        }
}

private class ContextWithView(
        override var target: PageContext?,
        private val v: View
) : ForwardContext() {
    override fun send(vararg values: Any?) {
        super.send(values.toCollection(LinkedList()).apply {
            add(0, v)
        })
    }
}

internal fun PageContext.withView(v: View): PageContext {
    return ContextWithView(this, v)
}