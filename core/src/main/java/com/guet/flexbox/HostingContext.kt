package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

abstract class HostingContext {

    abstract fun send(source: View, values: Array<out Any?>)

    abstract fun http(source: View): HttpTransaction?

    abstract fun refresh(source: View): RefreshTransaction?

    fun withView(source: View): PageContext {
        return PageContext(source, this)
    }
}