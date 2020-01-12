package com.guet.flexbox.transaction

import com.guet.flexbox.el.PropsELContext

abstract class PageTransaction {
    abstract fun commit(): (PropsELContext) -> Unit
}