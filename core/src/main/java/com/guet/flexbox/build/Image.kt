package com.guet.flexbox.build

import android.widget.ImageView.ScaleType
import com.facebook.litho.ComponentContext
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.widget.AsyncImage

internal object Image : Widget<AsyncImage.Builder>(Common) {

    override val attributeSet: AttributeSet<AsyncImage.Builder> by create {
        this["scaleType"] = object : Assignment<AsyncImage.Builder, ScaleType>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: ScaleType) {
                scaleType(value)
            }
        }
        this["borderColor"] = object : Assignment<AsyncImage.Builder, Int>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Int) {
                borderColor(value)
            }
        }
        this["borderRadius"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                borderRadius(value.toPx())
            }
        }
        this["borderWidth"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                borderWidth(value.toPx())
            }
        }
        this["blurRadius"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                blurRadius(value.toFloat())
            }
        }
        this["blurSampling"] = object : Assignment<AsyncImage.Builder, Double>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                blurSampling(value.toFloat())
            }
        }
        this["url"] = object : Assignment<AsyncImage.Builder, String>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: String) {
                if (display) {
                    url(value)
                }
            }
        }
    }

    override fun onCreate(c: ComponentContext, lockedInfo: LockedInfo): AsyncImage.Builder {
        return AsyncImage.create(c)
    }
}