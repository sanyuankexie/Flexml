package com.guet.flexbox.build

import androidx.annotation.RestrictTo
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.eventsystem.ClickUrlEventReceiver
import com.guet.flexbox.eventsystem.event.ClickExprEvent
import com.guet.flexbox.eventsystem.event.VisibleEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object CommonProps : Declaration() {

    override val dataBinding by DataBinding.create {
        value("width")
        value("height")
        value("flexGrow")
        value("flexShrink")
        value("minWidth")
        value("maxWidth")
        value("minHeight")
        value("maxHeight")
        enum("alignSelf", mapOf(
                "auto" to FlexAlign.AUTO,
                "flexStart" to FlexAlign.FLEX_START,
                "flexEnd" to FlexAlign.FLEX_END,
                "center" to FlexAlign.CENTER,
                "baseline" to FlexAlign.BASELINE,
                "stretch" to FlexAlign.STRETCH
        ))
        value("margin")
        value("padding")
        color("borderColor")
        value("borderRadius")
        for (lr in arrayOf("Left", "Right")) {
            for (tb in arrayOf("Top", "Bottom")) {
                value("border${lr}${tb}Radius")
            }
        }
        value("borderWidth")
        value("shadowElevation")
        text("background")
        for (edge in arrayOf("Left", "Right", "Top", "Bottom")) {
            value("margin$edge")
            value("padding$edge")
        }
        typed("clickUrl", ClickUrlEventReceiver.Covertor)
        event("onClick", ClickExprEvent.Factory)
        event("onVisible", VisibleEvent.Factory)
    }

}