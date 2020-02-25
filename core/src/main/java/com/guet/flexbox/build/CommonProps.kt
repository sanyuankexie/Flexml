package com.guet.flexbox.build

import android.view.View
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.enums.Visibility
import com.guet.flexbox.eventsystem.EventFactory
import com.guet.flexbox.eventsystem.EventHandlerAdapter
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.ClickExprEvent
import com.guet.flexbox.eventsystem.event.ClickUrlEvent
import com.guet.flexbox.eventsystem.event.TemplateEvent
import com.guet.flexbox.eventsystem.event.VisibleEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlScript

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
        typed("clickUrl", object : TextToAttribute<EventHandlerAdapter> {
            override fun cast(
                    engine: JexlEngine,
                    dataContext: JexlContext,
                    eventDispatcher: EventTarget,
                    raw: String
            ): EventHandlerAdapter? {
                val url = if (raw.isExpr) {
                    engine.createExpression(raw.innerExpr)
                            .evaluate(dataContext) as? String ?: ""
                } else {
                    raw
                }
                return object : EventHandlerAdapter {
                    override fun handleEvent(v: View?, args: Array<out Any?>?) {
                        eventDispatcher.dispatchEvent(
                                ClickUrlEvent(v!!, url)
                        )
                    }
                }
            }
        })
        event("onClick", object : EventFactory {
            override fun create(
                    source: View?,
                    args: Array<out Any?>?,
                    dataContext: JexlContext,
                    script: JexlScript
            ): TemplateEvent<*> {
                return ClickExprEvent(source!!, dataContext, script)
            }
        })
        event("onVisible", object : EventFactory {
            override fun create(
                    source: View?,
                    args: Array<out Any?>?,
                    dataContext: JexlContext,
                    script: JexlScript
            ): TemplateEvent<*> {
                return VisibleEvent(dataContext, script)
            }
        })
    }


}