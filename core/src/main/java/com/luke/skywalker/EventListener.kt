package com.luke.skywalker

interface EventListener {
    fun handleEvent(key: String, value: Any)
}
