package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.widget.AsyncLazyDrawable
import com.guet.flexbox.widget.BackgroundDrawable
import com.guet.flexbox.widget.NoOpDrawable
import com.guet.flexbox.widget.parseUrl

internal abstract class Widget<C : Component.Builder<*>>(private val parent: Widget<in C>? = null) {

    protected abstract val attributeSet: AttributeSet<C>

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
        createBackground(com, renderNode.attrs)
        onInstallChildren(com, renderNode, children)
        return com.build()
    }

    private fun createBackground(c: C, attrs: Map<String, Any>) {
        val borderRadius = (attrs.getOrElse("borderRadius") { 0 } as Number).toPx()
        val borderWidth = (attrs.getOrElse("borderWidth") { 0 } as Number).toPx()
        val borderColor = attrs.getOrElse("borderColor") { Color.TRANSPARENT } as Int
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
        c.background(BackgroundDrawable(
                backgroundDrawable,
                borderRadius,
                borderWidth,
                borderColor
        ))
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