package com.guet.flexbox.build

import android.graphics.Color
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.data.LockedInfo
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
            lockedInfo: LockedInfo,
            children: List<Component>
    ): Component {
        val com = onCreate(c, lockedInfo)
        for ((key, value) in lockedInfo.attrs) {
            assign(com, key, value, lockedInfo.visibility, lockedInfo.attrs)
        }
        createBackground(com, lockedInfo.attrs)
        onInstallChildren(com, lockedInfo, children)
        return com.build()
    }

    private fun createBackground(c: C, attrs: Map<String, Any>) {
        val borderRadius = (attrs.getOrElse("borderRadius") { 0 } as Number).toPx()
        val borderWidth = (attrs.getOrElse("borderWidth") { 0 } as Number).toPx()
        val borderColor = attrs.getOrElse("borderColor") { Color.TRANSPARENT } as Int
        var backgroundDrawable: ComparableDrawable? = null
        val background = attrs["background"] as? String
        val context = c.getContext()!!.androidContext
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(Color.parseColor(background))
            } catch (e: Exception) {
                when (val model = parseUrl(context, background)) {
                    is ComparableDrawable -> {
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
            lockedInfo: LockedInfo,
            children: List<Component>
    ) {
    }

    protected abstract fun onCreate(
            c: ComponentContext,
            lockedInfo: LockedInfo
    ): C
}