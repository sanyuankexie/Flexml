package com.guet.flexbox.build

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object Text : Widget() {
    override val dataBinding by DataBinding
            .create(AbsText) {
                text("text")
                bool("clipToBounds")
                color("textColor")
            }
}