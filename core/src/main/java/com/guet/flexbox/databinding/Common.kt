package com.guet.flexbox.databinding

import android.content.Context
import com.facebook.yoga.YogaAlign
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.data.Visibility
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext

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
        this["clickUrl"] = object : AttributeInfo<LambdaExpression>() {
            override fun cast(c: Context, props: PropsELContext, raw: String): LambdaExpression? {
                val url = props.tryGetValue<String>(raw, null)
                return url?.let { props.tryGetLambda("()->sender.send('${it}')") }
            }
        }
        this["onClick"] = object : AttributeInfo<LambdaExpression>() {
            override fun cast(c: Context, props: PropsELContext, raw: String): LambdaExpression? {
                return props.tryGetLambda(raw)
            }
        }
        this["onView"] = object : AttributeInfo<LambdaExpression>() {
            override fun cast(c: Context, props: PropsELContext, raw: String): LambdaExpression? {
                return props.tryGetLambda(raw)
            }
        }
    }

    override fun transform(
            c: Context,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<LayoutNode>,
            upperVisibility: Boolean
    ): List<RenderNode> {
        val selfVisibility = attrs["visibility"] ?: Visibility.VISIBLE
        if (selfVisibility == Visibility.GONE) {
            return emptyList()
        }
        val visibility = selfVisibility == Visibility.VISIBLE && upperVisibility
        val childrenRenderNode = if (children.isEmpty()) {
            emptyList()
        } else {
            children.map {
                DataBindingUtils.bindNode(c, it, data, visibility)
            }.flatten()
        }
        return listOf(RenderNode(type, attrs, visibility, childrenRenderNode))
    }
}