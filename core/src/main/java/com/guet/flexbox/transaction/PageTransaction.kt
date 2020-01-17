package com.guet.flexbox.transaction

import com.guet.flexbox.el.PropsELContext

abstract class PageTransaction {

    protected val sends = ArrayList<Array<out Any?>>()

    abstract fun commit(): (PropsELContext) -> Unit

    fun send(vararg values: Any?): PageTransaction {
        sends.add(values)
        return this
    }
}