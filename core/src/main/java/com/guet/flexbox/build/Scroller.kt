package com.guet.flexbox.build

import androidx.annotation.RestrictTo
import com.guet.flexbox.enums.Orientation

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object Scroller : Widget() {
    override val dataBinding: DataBinding by DataBinding
            .create(CommonDefine) {
        bool("scrollBarEnable")
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
        bool("fillViewport")
    }
}