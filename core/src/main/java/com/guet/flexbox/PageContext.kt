package com.guet.flexbox


abstract class PageContext {

    abstract fun send(key: String, vararg data: Any)

    companion object FakePageContext : PageContext() {
        override fun send(key: String, vararg data: Any) = throw IllegalStateException()
    }
}