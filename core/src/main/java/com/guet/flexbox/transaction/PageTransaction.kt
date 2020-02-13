package com.guet.flexbox.transaction

abstract class PageTransaction {

    protected val sends = ArrayList<Array<out Any?>>()

    abstract fun commit()

    fun send(vararg values: Any?): PageTransaction {
        sends.add(values)
        return this
    }
}