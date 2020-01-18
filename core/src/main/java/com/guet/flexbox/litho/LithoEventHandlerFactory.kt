package com.guet.flexbox.litho

import androidx.annotation.Keep
import com.guet.flexbox.build.EventHandler
import com.guet.flexbox.build.EventHandlerFactory

@Keep
object LithoEventHandlerFactory : EventHandlerFactory {
    override fun create(a: EventHandler): Any {
        return LithoEventHandler(a)
    }
}