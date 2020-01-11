package com.guet.flexbox.build

import com.facebook.litho.ClickEvent
import com.facebook.yoga.YogaAlign
import com.guet.flexbox.JoinPageContext
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.Visibility
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.litho.LithoEventHandler
import java.util.*

object Common : Declaration() {

    override val attributeSet: AttributeSet by create {
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
                "auto" to YogaAlign.AUTO,
                "flexStart" to YogaAlign.FLEX_START,
                "flexEnd" to YogaAlign.FLEX_END,
                "center" to YogaAlign.CENTER,
                "baseline" to YogaAlign.BASELINE,
                "stretch" to YogaAlign.STRETCH
        ))
        value("margin")
        value("padding")
        color("borderColor")
        value("borderRadius")
        value("borderWidth")
        value("shadowElevation")
        text("background")
        for (edge in arrayOf("Left", "Right", "Top", "Bottom")) {
            value("margin$edge")
            value("padding$edge")
        }
        typed("clickUrl") { pageContext, props, raw ->
            val url = props.tryGetValue<String>(raw)
            url?.let {
                LithoEventHandler.create<ClickEvent> {
                    pageContext.send(url)
                }
            }
        }
        typed("onClick") { pageContext, elContext, raw ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                LithoEventHandler.create<ClickEvent> { event ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", JoinPageContext(pageContext, event.view)
                    )) {
                        executable.invoke(elContext)
                    }
                }
            }
        }
    }

    override fun onBuild(
            bindings: BuildUtils,
            attrs: Map<String, Any>,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
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
            children.map {
                bindings.bindNode(
                        it,
                        pageContext,
                        data,
                        visibility,
                        other
                )
            }.flatten()
        }
        return listOf(factory.invoke(
                visibility,
                attrs,
                childrenComponent,
                other
        ))
    }
}