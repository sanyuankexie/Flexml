package com.guet.flexbox.build

import androidx.annotation.RestrictTo
import com.guet.flexbox.enums.Orientation

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object Scroller : Declaration() {
    override val dataBinding: DataBinding by DataBinding
            .create(CommonProps) {
        bool("scrollBarEnable")
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
        bool("fillViewport")
    }
}