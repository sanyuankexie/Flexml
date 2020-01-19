package com.guet.flexbox.build

import com.facebook.yoga.YogaAlign
import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.Visibility
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope
import java.util.*

internal object Common : Declaration() {

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
                EventHandlerFactory.create { v, _ ->
                    pageContext.send(v, arrayOf(url))
                }
            }
        }
        typed("onClick") { pageContext, elContext, raw ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                EventHandlerFactory.create { view, _ ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", pageContext.withView(view)
                    )) {
                        executable.exec(elContext)
                    }
                }
            }
        }
    }

    override fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: HostingContext,
            data: PropsELContext,
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