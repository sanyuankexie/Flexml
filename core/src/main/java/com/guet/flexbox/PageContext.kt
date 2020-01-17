package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import java.lang.ref.WeakReference

abstract class HostingContext {

    abstract fun send(source: View, values: Array<out Any?>)

    abstract fun http(source: View): HttpTransaction?

    abstract fun refresh(source: View): RefreshTransaction?

    fun withView(source: View): PageContext {
        return PageContext(source, this)
    }
}

class PageContext(
        private val source: View,
        private val host: HostingContext
) {

    fun send(vararg values: Any?) {
        host.send(source, values)
    }

    fun http(): HttpTransaction? {
        return host.http(source)
    }

    fun refresh(): RefreshTransaction? {
        return host.refresh(source)
    }
}

internal class ForwardContext : HostingContext() {

    private var _target: WeakReference<HostingContext?>? = null

    var target: HostingContext?
        set(value) {
            _target = WeakReference(value)
        }
        get() {
            return _target?.get()
        }

    override fun send(source: View, values: Array<out Any?>) {
        target?.send(source, values)
    }

    override fun refresh(source: View): RefreshTransaction? {
        return target?.refresh(source)
    }

    override fun http(source: View): HttpTransaction? {
        return target?.http(source)
    }
}