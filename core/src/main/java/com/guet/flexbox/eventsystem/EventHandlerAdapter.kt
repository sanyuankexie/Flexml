package com.guet.flexbox.eventsystem

import android.view.View

interface EventHandlerAdapter {
    fun handleEvent(v: View?, args: Array<out Any?>?)
}