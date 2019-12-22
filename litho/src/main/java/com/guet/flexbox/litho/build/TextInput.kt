package com.guet.flexbox.litho.build

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.litho.PageHost

internal object TextInput : Widget<TextInput.Builder>(Common) {
    override val attributeSet: AttributeSet<TextInput.Builder> by create {
        this["maxLines"] = object : Assignment<TextInput.Builder, Double>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                maxLines(value.toInt())
            }
        }
        this["minLines"] = object : Assignment<TextInput.Builder, Double>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                minLines(value.toInt())
            }
        }
        this["textSize"] = object : Assignment<TextInput.Builder, Double>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                textSizePx(value.toPx())
            }
        }
        this["textStyle"] = object : Assignment<TextInput.Builder, Int>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: Int) {
                typeface(Typeface.defaultFromStyle(value))
            }
        }
        this["ellipsize"] = object : Assignment<TextInput.Builder, TruncateAt>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: TruncateAt) {
                ellipsize(value)
            }
        }
        this["onTextChanged"] = object : Assignment<TextInput.Builder, LambdaExpression>() {
            override fun TextInput.Builder.assign(display: Boolean, other: Map<String, Any>, value: LambdaExpression) {
                textChangedEventHandler(PageHost.onTextChanged(context, value))
            }
        }
    }

    override fun onCreate(c: ComponentContext, renderNode: RenderNode): TextInput.Builder {
        return TextInput.create(c)
    }
}