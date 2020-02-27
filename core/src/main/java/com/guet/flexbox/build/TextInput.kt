package com.guet.flexbox.build

import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.TextChangedEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object TextInput : Declaration() {
    override val dataBinding by DataBinding
            .create(CommonProps) {
                event("onTextChanged", TextChangedEvent.Factory)
            }
}