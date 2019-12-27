package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.LruCache
import android.view.ViewOutlineProvider
import com.facebook.litho.Border
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.CornerOutlineProvider
import com.guet.flexbox.widget.NoOpDrawable
import com.guet.flexbox.widget.parseUrl

internal abstract class ToComponent<C : Component.Builder<*>>(
        private val parent: ToComponent<in C>? = null
) {

    protected abstract val attributeSet: AttributeSet<C>

    private val caches = LruCache<Int, ViewOutlineProvider>(32)

    private fun assign(
            c: C,
            name: String,
            value: Any,
            display: Boolean,
            other: Map<String, Any>
    ) {
        @Suppress("UNCHECKED_CAST")
        val assignment = attributeSet[name] as? Assignment<C, Any>
        if (assignment != null) {
            assignment(c, display, other, value)
        } else {
            parent?.assign(c, name, value, display, other)
        }
    }

    fun toComponent(
            c: ComponentContext,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>,
            children: List<Component>
    ): Component {
        val com = create(c, type, visibility, attrs)
        for ((key, value) in attrs) {
            assign(com, key, value, visibility, attrs)
        }
        createBorder(com, attrs)
        createBackground(com, attrs)
        onInstallChildren(com, type, visibility, attrs, children)
        return com.build()
    }


    private fun createBorder(c: C, attrs: Map<String, Any>) {
        val borderRadius = (attrs.getOrElse("borderRadius") { 0 } as Number).toPx()
        val borderWidth = (attrs.getOrElse("borderWidth") { 0 } as Number).toPx()
        val borderColor = attrs.getOrElse("borderColor") { Color.TRANSPARENT } as Int
        c.border(
                Border.create(c.getContext())
                        .color(YogaEdge.ALL, borderColor)
                        .widthPx(YogaEdge.ALL, borderWidth)
                        .radiusPx(borderRadius)
                        .build()
        )
        if (borderRadius > 0) {
            var outline: ViewOutlineProvider? = caches[borderRadius]
            if (outline == null) {
                outline = CornerOutlineProvider(borderRadius)
                caches.put(borderColor, outline)
            }
            c.outlineProvider(outline)
            c.clipToOutline(true)
        }
    }

    private fun createBackground(c: C, attrs: Map<String, Any>) {
        var backgroundDrawable: Drawable? = null
        val background = attrs["background"] as? String
        val context = c.getContext()!!.androidContext
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(Color.parseColor(background))
            } catch (e: Exception) {
                when (val model = parseUrl(context, background)) {
                    is Drawable -> {
                        backgroundDrawable = model
                    }
                    is CharSequence, is Int -> {
                        backgroundDrawable = AsyncLazyDrawable(
                                context,
                                model
                        )
                    }
                }
            }
        }
        if (backgroundDrawable == null) {
            backgroundDrawable = NoOpDrawable()
        }
        c.background(backgroundDrawable)
    }

    protected open fun onInstallChildren(
            owner: C,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>,
            children: List<Component>
    ) {
    }

    protected abstract fun create(
            c: ComponentContext,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>
    ): C
}