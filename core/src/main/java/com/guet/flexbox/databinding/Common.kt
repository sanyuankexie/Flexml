package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.Visibility
import com.guet.flexbox.build.ToComponent
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.event.ClickUrlEventHandler
import com.guet.flexbox.event.OnClickEventHandler

internal object Common : Declaration() {

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
        text("background")
        for (edge in arrayOf("Left", "Right", "Top", "Bottom")) {
            value("margin$edge")
            value("padding$edge")
        }
        this["clickUrl"] = object : AttributeInfo<ClickUrlEventHandler>() {
            override fun cast(
                    pageContext: PageContext,
                    props: PropsELContext,
                    raw: String
            ): ClickUrlEventHandler? {
                return ClickUrlEventHandler(pageContext, props.getValue(raw))
            }
        }
        this["onClick"] = object : AttributeInfo<OnClickEventHandler>() {
            override fun cast(
                    pageContext: PageContext,
                    props: PropsELContext,
                    raw: String
            ): OnClickEventHandler? {
                return props.tryGetValue<LambdaExpression>(raw)?.let {
                    OnClickEventHandler(pageContext, props, it)
                }
            }
        }
    }

    override fun transform(
            c: ComponentContext,
            to: ToComponent<*>?,
            type: String,
            attrs: Map<String, Any>,
            pageContext: PageContext,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        val selfVisibility = attrs["visibility"] ?: Visibility.VISIBLE
        if (selfVisibility == Visibility.GONE) {
            return emptyList()
        }
        val visibility = selfVisibility == Visibility.VISIBLE && upperVisibility
        val childrenComponent = if (children.isEmpty()) {
            emptyList()
        } else {
            children.map {
                Toolkit.bindNode(c, it, pageContext, data, visibility)
            }.flatten()
        }
        return listOf(to!!.toComponent(c, type, visibility, attrs, childrenComponent))
    }
}