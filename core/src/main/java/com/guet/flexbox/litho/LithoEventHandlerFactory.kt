package com.guet.flexbox.litho

import android.view.View
import androidx.annotation.Keep
import com.guet.flexbox.build.EventHandlerFactory

@Keep
object LithoEventHandlerFactory : EventHandlerFactory {
    override fun create(a: (View, Array<out Any?>) -> Unit): Any {
        return LithoEventHandler(a)
    }
}