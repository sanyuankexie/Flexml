package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.enums.Visibility
import com.guet.flexbox.event.ClickUrlHandler
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

object CommonProps : Declaration() {

    override val dataBinding by DataBinding.create {
        enum("visibility", mapOf(
                "visible" to Visibility.VISIBLE,
                "invisible" to Visibility.INVISIBLE,
                "gone" to Visibility.GONE
        ))
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
        typed("clickUrl", TextToHandler)
        event("onClick")
        event("onVisible")
    }

    private object TextToHandler : TextToAttribute<ClickUrlHandler> {
        override fun cast(
                engine: JexlEngine,
                dataContext: JexlContext,
                pageContext: PageContext,
                raw: String
        ): ClickUrlHandler? {
            return if (raw.isExpr) {
                ClickUrlHandler(
                        pageContext,
                        engine.createExpression(raw.innerExpr)
                                .evaluate(dataContext)
                                .toString()
                )
            } else {
                ClickUrlHandler(
                        pageContext,
                        raw
                )
            }
        }
    }

}