package com.guet.flexbox.event

import android.view.View

interface EventHandler {
    fun handleEvent(v: View?, args: Array<out Any?>?)
}