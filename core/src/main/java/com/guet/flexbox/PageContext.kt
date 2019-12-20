package com.guet.flexbox

class PageContext @JvmOverloads constructor(
        var listener: EventListener? = null
) {

    @JvmName("send")
    fun send(key: String, vararg data: Any) {
        listener?.handleEvent(key, data)
    }
}