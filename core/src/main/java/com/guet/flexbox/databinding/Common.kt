package com.guet.flexbox.databinding

import android.content.Context
import com.facebook.yoga.YogaAlign
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
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
        value("borderWidth")
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
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<NodeInfo>,
            selfVisibility: Boolean
    ): List<LockedInfo> {
        val list = ArrayList<LockedInfo>(children.size)
        for (item in children) {
            val b = DataBindingUtils.bind(c, item, data, selfVisibility)
            if (b != null) {
                list.add(b)
            }
        }
        return list
    }
}