package com.guet.flexbox.transaction

import com.guet.flexbox.el.ELContext

abstract class PageTransaction {

    protected val sends = ArrayList<Array<out Any?>>()

    abstract fun commit(): (ELContext) -> Unit

    fun send(vararg values: Any?): PageTransaction {
        sends.add(values)
        return this
    }

    companion object {
        @JvmStatic
        fun create(action: (ELContext) -> Unit): (ELContext) -> Unit {
            return action
        }
    }
}