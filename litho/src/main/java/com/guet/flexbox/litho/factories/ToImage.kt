package com.guet.flexbox.litho.factories

import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.facebook.litho.widget.EmptyComponent
import com.facebook.litho.widget.Image
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.Child
import com.guet.flexbox.build.RenderNodeFactory
import com.guet.flexbox.litho.drawable.RoundedColorDrawable
import com.guet.flexbox.litho.drawable.RoundedGradientDrawable
import com.guet.flexbox.litho.toPxFloat

object ToImage : RenderNodeFactory {

    override fun invoke(
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<Child>,
            other: Any
    ): Any {
        return toComponent(
                other as ComponentContext,
                visibility,
                attrs
        )
    }

    private fun toComponent(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component {
        if (!visibility) {
            return EmptyComponent.create(c).build()
        }
        val url = attrs["url"] as? CharSequence
        val lt = attrs.getFloatValue("borderLeftTopRadius").toPxFloat()
        val rt = attrs.getFloatValue("borderRightTopRadius").toPxFloat()
        val lb = attrs.getFloatValue("borderLeftBottomRadius").toPxFloat()
        val rb = attrs.getFloatValue("borderRightBottomRadius").toPxFloat()
        val needRoundedCorners = lt != 0f || rb != 0f || lb != 0f || rt != 0f
        if (url != null) {
            val (type, prams) = UrlType.parseUrl(
                    c.androidContext, url
            )
            when (type) {
                UrlType.GRADIENT -> {
                    val o = prams[0] as GradientDrawable.Orientation
                    val colors = prams[1] as IntArray
                    return Image.create(c)
                            .scaleType(ImageView.ScaleType.FIT_XY)
                            .drawable(if (needRoundedCorners) {
                                RoundedGradientDrawable(
                                        o,
                                        colors,
                                        lt,
                                        rt,
                                        rb,
                                        lb
                                )
                            } else {
                                ComparableGradientDrawable(o, colors)
                            }).build()
                }
                UrlType.COLOR -> {
                    val color = prams[0] as Int
                    return Image.create(c)
                            .scaleType(ImageView.ScaleType.FIT_XY)
                            .drawable(if (needRoundedCorners) {
                                RoundedColorDrawable(
                                        color,
                                        lt,
                                        rt,
                                        rb,
                                        lb
                                )
                            } else {
                                ComparableColorDrawable.create(color)
                            }).build()
                }
                UrlType.URL -> return ToGlideImage.toComponent(c, visibility, attrs, emptyList())
                UrlType.RESOURCE -> {
                    val id = prams[0] as Int
                    return ToGlideImage.toComponent(c, visibility,
                            (attrs as MutableMap).apply {
                                this["url"] = id
                            }, emptyList())
                }
                else -> {
                    return EmptyComponent.create(c).build()
                }
            }
        }
        return EmptyComponent.create(c).build()
    }
}