package com.guet.flexbox.eventsystem

import android.view.View

interface EventAdapter {
    fun handleEvent(v: View?, args: Array<out Any?>?)
}