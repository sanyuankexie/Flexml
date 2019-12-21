package com.guet.flexbox.widget

import android.view.View

interface EventHandler {
    fun handleEvent(v: View, key: String, value: Any)
}