package com.guet.flexbox.litho.build

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.TextUtils.TruncateAt
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.Text
import com.guet.flexbox.HorizontalGravity
import com.guet.flexbox.VerticalGravity
import com.guet.flexbox.content.RenderNode

internal object Text : Widget<Text.Builder>(Common) {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    override val attributeSet: AttributeSet<Text.Builder> by create {
        this["verticalGravity"] = object : Assignment<Text.Builder, VerticalGravity>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: VerticalGravity) {
                verticalGravity(com.facebook.litho.widget.VerticalGravity.valueOf(value.name))
            }
        }
        this["horizontalGravity"] = object : Assignment<Text.Builder, HorizontalGravity>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: HorizontalGravity) {
                textAlignment(when (value) {
                    HorizontalGravity.CENTER -> Alignment.ALIGN_CENTER
                    HorizontalGravity.RIGHT -> Alignment.valueOf("ALIGN_RIGHT")
                    else -> Alignment.valueOf("ALIGN_LEFT")
                })
            }
        }
        this["text"] = object : Assignment<Text.Builder, String>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: String) {
                text(value)
                if (!display) {
                    textColor(Color.TRANSPARENT)
                    textColorStateList(invisibleColor)
                }
            }
        }
        this["clipToBounds"] = object : Assignment<Text.Builder, Boolean>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Boolean) {
                clipToBounds(value)
            }
        }
        this["maxLines"] = object : Assignment<Text.Builder, Double>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                maxLines(value.toInt())
            }
        }
        this["minLines"] = object : Assignment<Text.Builder, Double>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                minLines(value.toInt())
            }
        }
        this["textSize"] = object : Assignment<Text.Builder, Double>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                textSizePx(value.toPx())
            }
        }
        this["textStyle"] = object : Assignment<Text.Builder, Int>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Int) {
                typeface(Typeface.defaultFromStyle(value))
            }
        }
        this["textColor"] = object : Assignment<Text.Builder, Int>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: Int) {
                if (display) {
                    textColor(value)
                } else {
                    textColor(Color.TRANSPARENT)
                    textColorStateList(invisibleColor)
                }
            }
        }
        this["ellipsize"] = object : Assignment<Text.Builder, TruncateAt>() {
            override fun Text.Builder.assign(display: Boolean, other: Map<String, Any>, value: TruncateAt) {
                ellipsize(value)
            }
        }
    }

    override fun onCreate(c: ComponentContext, renderNode: RenderNode): Text.Builder {
        return Text.create(c)
    }
}