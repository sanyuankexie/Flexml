package com.guet.flexbox.litho.factories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable.Orientation
import com.facebook.litho.Border
import com.facebook.litho.Border.Corner
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.Child
import com.guet.flexbox.build.RenderNodeFactory
import com.guet.flexbox.litho.ChildComponent
import com.guet.flexbox.litho.drawable.ColorDrawable
import com.guet.flexbox.litho.drawable.GradientDrawable
import com.guet.flexbox.litho.drawable.LazyImageDrawable
import com.guet.flexbox.litho.drawable.lazyDrawable
import com.guet.flexbox.litho.resolve.UrlType
import com.guet.flexbox.litho.toPx
import com.guet.flexbox.litho.toPxFloat

abstract class ToComponent<C : Component.Builder<*>>(
        private val parent: ToComponent<in C>? = null
) : RenderNodeFactory {

    protected abstract val attributeAssignSet: AttributeAssignSet<C>

    private fun assign(
            c: C,
            name: String,
            value: Any,
            display: Boolean,
            other: Map<String, Any>
    ) {
        @Suppress("UNCHECKED_CAST")
        val assignment = attributeAssignSet[name] as? Assignment<C, Any>
        if (assignment != null) {
            assignment(c, display, other, value)
        } else {
            parent?.assign(c, name, value, display, other)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<Child>,
            other: Any
    ): Any = toComponent(
            other as ComponentContext,
            visibility,
            attrs,
            children as List<ChildComponent>
    )

    fun toComponent(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ): Component {
        val com = create(c, visibility, attrs)
        prepareAssign(attrs)
        for ((key, value) in attrs) {
            assign(com, key, value, visibility, attrs)
        }
        createBackground(com, attrs)
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
    }


    private fun createBackground(c: C, attrs: AttributeSet) {
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
        val needBorder = borderWidth != 0
                && borderColor != Color.TRANSPARENT
        if (needBorder) {
            var width: Int = borderWidth
            for (value in floatArrayOf(lt, rt, rb, lb)) {
                if (!value.isNaN() && value < width) {
                    width = value.toInt()
                }
            }
            val padding = width / 2f
            //borderRadius是外边框弧度
            c.border(Border.create(c.getContext())
                    .color(YogaEdge.ALL, borderColor)
                    .widthPx(YogaEdge.ALL, width)
                    .radiusPx(Corner.TOP_LEFT, (lt - padding).toInt())
                    .radiusPx(Corner.TOP_RIGHT, (rt - padding).toInt())
                    .radiusPx(Corner.BOTTOM_RIGHT, (rb - padding).toInt())
                    .radiusPx(Corner.BOTTOM_LEFT, (lb - padding).toInt())
                    .build()
            )
        }
    }

    protected open fun onInstallChildren(
            owner: C,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {

    }

    protected abstract fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): C
}