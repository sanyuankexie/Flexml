package com.guet.flexbox.build

import android.view.View

interface EventHandlerFactory {

    fun create(a: (View, Array<out Any?>) -> Unit): Any

    companion object {

        private const val default = "com.guet.flexbox.litho.LithoEventHandler"

        fun create(a: (View, Array<out Any?>) -> Unit): Any {

            val factory = Class.forName(
                    System.getProperty(
                            EventHandlerFactory::class.java.name,
                            default
                    )!!).getDeclaredField(
                    "INSTANCE"
            ).get(null) as EventHandlerFactory
            return factory.create(a)
        }
    }
}