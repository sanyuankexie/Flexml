package com.guet.flexbox.build

interface RenderNodeFactory<T : Any> {
    fun create(
            display: Boolean,
            attrs: PropSet,
            children: List<T>,
            other: Any?
    ): T
}