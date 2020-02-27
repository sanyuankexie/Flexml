package com.guet.flexbox.build

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object Image : Widget() {
    override val dataBinding by DataBinding
            .create(Graphic) {
                value("blurRadius")
                value("blurSampling")
            }
}