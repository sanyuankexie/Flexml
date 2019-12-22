package com.guet.flexbox.litho.build

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.LruCache
import android.view.ViewOutlineProvider
import com.facebook.litho.Border
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.litho.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.CornerOutlineProvider
import com.guet.flexbox.litho.widget.NoOpDrawable
import com.guet.flexbox.litho.widget.parseUrl

internal abstract class Widget<C : Component.Builder<*>>(private val parent: Widget<in C>? = null) {

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
            assignment.invoke(c, display, other, value)
        } else {
            parent?.assign(c, name, value, display, other)
        }
    }

    fun create(
            c: ComponentContext,
            renderNode: RenderNode,
            children: List<Component>
    ): Component {
        val com = onCreate(c, renderNode)
        for ((key, value) in renderNode.attrs) {
            assign(com, key, value, renderNode.visibility, renderNode.attrs)
        }
        createBorder(com, renderNode.attrs)
        createBackground(com, renderNode.attrs)
        onInstallChildren(com, renderNode, children)
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
            renderNode: RenderNode,
            children: List<Component>
    ) {
    }

    protected abstract fun onCreate(
            c: ComponentContext,
            renderNode: RenderNode
    ): C
}