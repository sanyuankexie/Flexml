package com.guet.flexbox.litho.factories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableGradientDrawable
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.build.Child
import com.guet.flexbox.build.RenderNodeFactory
import com.guet.flexbox.litho.ChildComponent
import com.guet.flexbox.litho.drawable.*
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
        val borderRadius = attrs["borderRadius"] as? Double
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
        val borderWidth = attrs.getFloatValue("borderWidth").toPxFloat()
        val borderColor = attrs["borderColor"] as? Int ?: Color.TRANSPARENT
        val needBorder = borderWidth != 0f && borderColor != Color.TRANSPARENT
        val needRoundedCorners = lt != 0f || rb != 0f || lb != 0f || rt != 0f
        if (background != null) {
            val (type, prams) = UrlType.parseUrl(
                    context, background
            )
            when (type) {
                UrlType.GRADIENT -> {
                    val o = prams[0] as GradientDrawable.Orientation
                    val colors = prams[1] as IntArray
                    if (needRoundedCorners) {
                        val rounded = RoundedGradientDrawable(
                                o,
                                colors,
                                lt,
                                rt,
                                rb,
                                lb
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    rounded,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(rounded)
                        }
                    } else {
                        val normal = ComparableGradientDrawable(o, colors)
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    normal,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(normal)
                        }
                    }
                }
                UrlType.COLOR -> {
                    val color = prams[0] as Int
                    if (needRoundedCorners) {
                        val rounded = RoundedColorDrawable(
                                color,
                                lt,
                                rt,
                                rb,
                                lb
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    rounded,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(rounded)
                        }
                    } else {
                        val normal =
                                ComparableColorDrawable.create(color)
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    normal,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(normal)
                        }
                    }
                }
                UrlType.URL -> {
                    val url = prams[0] as CharSequence
                    if (needRoundedCorners) {
                        val rounded = RoundedGlideDrawable(
                                context,
                                url.toString(),
                                lt,
                                rt,
                                rb,
                                lb
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    rounded,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(rounded)
                        }
                    } else {
                        val normal = GlideDrawable(
                                context,
                                url.toString()
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    normal,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(normal)
                        }
                    }
                }
                UrlType.RESOURCE -> {
                    val id = prams[0] as Int
                    if (needRoundedCorners) {
                        val rounded = RoundedGlideDrawable(
                                context,
                                id,
                                lt,
                                rt,
                                rb,
                                lb
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    rounded,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(rounded)
                        }
                    } else {
                        val normal = GlideDrawable(
                                context,
                                id
                        )
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    normal,
                                    borderWidth,
                                    borderColor
                            ))
                        } else {
                            c.background(normal)
                        }
                    }
                }
                else -> {
                    if (needRoundedCorners) {
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    RoundedNoOpDrawable(
                                            lt,
                                            rt,
                                            rb,
                                            lb
                                    ),
                                    borderWidth,
                                    borderColor
                            ))
                        }
                    } else {
                        if (needBorder) {
                            c.background(BorderDrawable(
                                    NoOpDrawable(),
                                    borderWidth,
                                    borderColor
                            ))
                        }
                    }
                }
            }
        } else {
            if (needRoundedCorners) {
                if (needBorder) {
                    c.background(BorderDrawable(
                            RoundedNoOpDrawable(
                                    lt,
                                    rt,
                                    rb,
                                    lb
                            ),
                            borderWidth,
                            borderColor
                    ))
                }
            } else {
                if (needBorder) {
                    c.background(BorderDrawable(
                            NoOpDrawable(),
                            borderWidth,
                            borderColor
                    ))
                }
            }
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