package com.guet.flexbox.eventsystem

import android.view.View
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ExternalEventReceiver {
    fun receive(v: View?, args: Array<out Any?>?)
}