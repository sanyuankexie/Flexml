package com.guet.flexbox.build

interface EventHandlerFactory {

    fun create(a: EventHandler): Any

    companion object {

        private const val default = "com.guet.flexbox.litho.LithoEventHandlerFactory"

        fun create(a: EventHandler): Any {
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