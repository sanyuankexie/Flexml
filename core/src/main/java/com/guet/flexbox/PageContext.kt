package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import java.lang.ref.WeakReference

abstract class HostingContext {

    abstract fun send(source: View, vararg values: Any?)

    abstract fun http(source: View): HttpTransaction?

    abstract fun refresh(source: View): RefreshTransaction?

    fun withView(source: View): PageContext {
        return PageContext(source, this)
    }
}

class PageContext(
        private val source: View,
        private val hosting: HostingContext
) {

    fun send(vararg values: Any?) {
        hosting.send(source, values)
    }

    fun http(): HttpTransaction? {
        return hosting.http(source)
    }

    fun refresh(): RefreshTransaction? {
        return hosting.refresh(source)
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

    override fun send(source: View, vararg values: Any?) {
        target?.send(source, values)
    }

    override fun refresh(source: View): RefreshTransaction? {
        return target?.refresh(source)
    }

    override fun http(source: View): HttpTransaction? {
        return target?.http(source)
    }
}