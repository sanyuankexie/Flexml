package com.guet.flexbox.litho.factories

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.widget.ImageView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.litho.widget.Image
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.build.RenderNodeFactory
import com.guet.flexbox.build.UrlType
import com.guet.flexbox.litho.drawable.ColorDrawable
import com.guet.flexbox.litho.drawable.LazyDrawableLoader
import com.guet.flexbox.litho.getFloatValue
import com.guet.flexbox.litho.toPxFloat

object ToImage : RenderNodeFactory<Component> {

    override fun create(
            display: Boolean,
            attrs: PropSet,
            children: List<Component>,
            other: Any?
    ): Component {
        return toComponent(
                other as ComponentContext,
                attrs,
                display
        )
    }

    private fun toComponent(
            c: ComponentContext,
            attrs: PropSet,
            visibility: Boolean = true
    ): Component {
        if (!visibility) {
            return Row.create(c).build()
        }
        val url = attrs["src"] as? CharSequence
        val lt = attrs.getFloatValue("borderLeftTopRadius").toPxFloat()
        val rt = attrs.getFloatValue("borderRightTopRadius").toPxFloat()
        val lb = attrs.getFloatValue("borderLeftBottomRadius").toPxFloat()
        val rb = attrs.getFloatValue("borderRightBottomRadius").toPxFloat()
        val needCorners = lt != 0f || rb != 0f || lb != 0f || rt != 0f
        val isSameCorners = lt == rt && lt == rb && lt == lb
        if (url != null) {
            val (type, prams) = UrlType.parseUrl(c.androidContext, url)
            when (type) {
                UrlType.GRADIENT -> {
                    val orientation = prams[0] as Orientation
                    val colors = prams[1] as IntArray
                    val drawable = LazyDrawableLoader.from {
                        GradientDrawable(
                                orientation, colors
                        ).apply {
                            if (needCorners) {
                                if (isSameCorners) {
                                    cornerRadius = lb
                                } else {
                                    cornerRadii = floatArrayOf(
                                            lt, rt, rb, lb
                                    )
                                }
                            }
                        }
                    }
                    return Image.create(c)
                            .scaleType(ImageView.ScaleType.FIT_XY)
                            .drawable(drawable)
                            .build()
                }
                UrlType.COLOR -> {
                    val color = prams[0] as Int
                    val drawable = LazyDrawableLoader.from {
                        ColorDrawable(
                                color
                        ).apply {
                            if (isSameCorners) {
                                cornerRadius = lb
                            } else {
                                cornerRadii = floatArrayOf(
                                        lt, lt, rt, rt,
                                        rb, rb, lb, lb
                                )
                            }
                        }
                    }
                    return Image.create(c)
                            .scaleType(ImageView.ScaleType.FIT_XY)
                            .drawable(drawable)
                            .build()
                }
                UrlType.URL -> {
                    return ToDynamicImage.toComponent(c, visibility, attrs, emptyList())
                }
                UrlType.RESOURCE -> {
                    val id = prams[0] as Int
                    return ToDynamicImage.toComponent(c, visibility,
                            (attrs as MutableMap).apply {
                                this["src"] = id
                            }, emptyList())
                }
                else -> Unit
            }
        }
        return Row.create(c)
                .build()
    }


}