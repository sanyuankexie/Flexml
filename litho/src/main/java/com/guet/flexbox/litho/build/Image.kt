package com.guet.flexbox.litho.build

import android.widget.ImageView.ScaleType
import com.facebook.litho.ComponentContext
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.litho.widget.AsyncImage

internal object Image : Widget<AsyncImage.Builder>(Common) {

    override val attributeSet: AttributeSet<AsyncImage.Builder> by create {
        this["scaleType"] = object : Assignment<AsyncImage.Builder, ScaleType>() {
            override fun AsyncImage.Builder.assign(display: Boolean, other: Map<String, Any>, value: ScaleType) {
                scaleType(value)
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
                } else {
                    url("")
                }
            }
        }
    }

    override fun onCreate(c: ComponentContext, renderNode: RenderNode): AsyncImage.Builder {
        return AsyncImage.create(c)
    }
}