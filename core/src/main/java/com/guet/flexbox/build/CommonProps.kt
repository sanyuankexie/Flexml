package com.guet.flexbox.build

import com.guet.flexbox.EventContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.event.ClickUrlHandler
import com.guet.flexbox.build.event.OnClickHandler
import com.guet.flexbox.build.event.OnVisibleHandler
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.tryGetValue
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.enums.Visibility

object CommonProps : Declaration() {

    override val attributeInfoSet: AttributeInfoSet by create {
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
        event("clickUrl") { hostContext, elContext, raw ->
            val url = elContext.tryGetValue<String>(raw)
            url?.let {
                return@let ClickUrlHandler(elContext, hostContext, url)
            }
        }
        event("onClick") { hostContext, elContext, raw ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                return@let OnClickHandler(elContext, hostContext, executable)
            }
        }
        event("onVisible") { hostContext, elContext, raw ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                return@let OnVisibleHandler(elContext, hostContext, executable)
            }
        }
    }

    override fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory?,
            eventContext: EventContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        if (factory == null) {
            return emptyList()
        }
        val selfVisibility = attrs["visibility"] ?: Visibility.VISIBLE
        if (selfVisibility == Visibility.GONE) {
            return emptyList()
        }
        val visibility = selfVisibility == Visibility.VISIBLE && upperVisibility
        val childrenComponent = if (children.isEmpty()) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    eventContext,
                    data,
                    visibility,
                    other
            )
        }
        return listOf(factory.invoke(
                visibility,
                attrs,
                childrenComponent,
                other
        ))
    }
}