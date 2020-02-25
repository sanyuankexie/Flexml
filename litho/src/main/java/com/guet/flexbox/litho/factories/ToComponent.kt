package com.guet.flexbox.litho.factories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.RenderNodeFactory
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.drawable.*
import com.guet.flexbox.litho.getFloatValue
import com.guet.flexbox.litho.resolve.AttrsAssigns
import com.guet.flexbox.litho.resolve.UrlType
import com.guet.flexbox.litho.toPx
import com.guet.flexbox.litho.toPxFloat

abstract class ToComponent<C : Component.Builder<*>> : RenderNodeFactory<Component> {

    abstract val attrsAssigns: AttrsAssigns<C>

    override fun create(
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<Component>,
            other: Any?
    ): Component {
        return toComponent(
                other as ComponentContext,
                visibility,
                attrs,
                children
        )
    }

    fun toComponent(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<Widget>
    ): Component {
        val com = create(c, visibility, attrs)
        prepareAssign(attrs)
        attrsAssigns.assign(com, visibility, attrs)
        createBackgroundWithBorder(com, attrs)
        onInstallChildren(com, visibility, attrs, children)
        return com.build()
    }

    private fun prepareAssign(attrs: AttributeSet) {
        val borderRadius = attrs["borderRadius"]
        if (borderRadius != null) {
            for (lr in arrayOf("Left", "Right")) {
                for (tb in arrayOf("Top", "Bottom")) {
                    (attrs as MutableMap)["border${lr}${tb}Radius"] = borderRadius
                }
            }
        }
        val borderWidth = attrs["borderWidth"] as? Float
        if (borderWidth != null) {
            for (edge in arrayOf("Left", "Right", "Top", "Bottom")) {
                val key = "margin${edge}"
                val value = attrs[key] as? Float ?: 0f
                (attrs as MutableMap)[key] = value + borderWidth
            }
        }
    }

    private fun createBackgroundWithBorder(c: C, attrs: AttributeSet) {
        val background = attrs["background"] as? CharSequence
        val context = c.getContext()!!.androidContext
        val lt = attrs.getFloatValue("borderLeftTopRadius").toPxFloat()
        val rt = attrs.getFloatValue("borderRightTopRadius").toPxFloat()
        val lb = attrs.getFloatValue("borderLeftBottomRadius").toPxFloat()
        val rb = attrs.getFloatValue("borderRightBottomRadius").toPxFloat()
        val needCorners = lt != 0f || rb != 0f || lb != 0f || rt != 0f
        val isSameCorners = lt == rt && lt == rb && lt == lb
        if (background != null) {
            val (type, prams) = UrlType.parseUrl(
                    context, background
            )
            when (type) {
                UrlType.GRADIENT -> {
                    val orientation = prams[0] as Orientation
                    val colors = prams[1] as IntArray
                    val drawable = lazyDrawable {
                        GradientDrawable(
                                orientation, colors
                        ).apply {
                            if (needCorners) {
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
                    }
                    c.background(drawable)
                }
                UrlType.COLOR -> {
                    val color = prams[0] as Int
                    val drawable = lazyDrawable {
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
                    c.background(drawable)
                }
                UrlType.URL, UrlType.RESOURCE -> {
                    val model = prams[0]
                    val drawable = if (needCorners) {
                        if (isSameCorners) {
                            LazyImageDrawable(context, model, lt)
                        } else {
                            LazyImageDrawable(
                                    context,
                                    model,
                                    lt, rt, rb, lb
                            )
                        }
                    } else {
                        LazyImageDrawable(context, model)
                    }
                    c.background(drawable)
                }
                else -> Unit
            }
        }
        val borderWidth = attrs.getFloatValue("borderWidth").toPx()
        val borderColor = attrs["borderColor"] as? Int ?: Color.TRANSPARENT
        val needBorder = borderWidth != 0 && borderColor != Color.TRANSPARENT
        if (needBorder) {
            c.foreground(ColorBorderDrawable.Builder()
                    .borderColor(borderColor)
                    .borderWidth(borderWidth)
                    .borderRadius(lt, rt, rb, lb)
                    .build())
        }
    }

    protected open fun onInstallChildren(
            owner: C,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<Widget>
    ) {

    }

    protected abstract fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): C
}