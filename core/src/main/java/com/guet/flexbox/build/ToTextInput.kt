package com.guet.flexbox.build

import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.TextInput
import com.guet.flexbox.event.OnTextChangedEventHandler

internal object ToTextInput : ToComponent<TextInput.Builder>(Common) {
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
        this["onTextChanged"] = object : Assignment<TextInput.Builder, OnTextChangedEventHandler>() {
            override fun TextInput.Builder.assign(
                    display: Boolean,
                    other: Map<String, Any>,
                    value: OnTextChangedEventHandler
            ) {
                textChangedEventHandler(value)
            }
        }
    }

    override fun create(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): TextInput.Builder {
        return TextInput.create(c)
    }
}