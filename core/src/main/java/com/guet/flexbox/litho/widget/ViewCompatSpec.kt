package com.guet.flexbox.litho.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.viewcompat.SimpleViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor

@LayoutSpec
object ViewCompatSpec {

    @OnCreateLayout
    fun onCreateLayout(
            c: ComponentContext,
            @Prop name: String,
            @Prop constructor: Constructor<out View>,
            @Prop attrs: AttributeSet,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component {
        return ViewCompatComponent.get(ReflectViewCreator(
                c,
                constructor,
                attrs,
                children
        ), name).create(c)
                .viewBinder(NoOpViewBinder)
                .build()
    }

    private class ReflectViewCreator(
            private val c: ComponentContext,
            private val constructor: Constructor<out View>,
            private val attributeSet: AttributeSet,
            private val children: List<Component>?
    ) : ViewCreator<View> {
        override fun createView(ctx: Context, parent: ViewGroup?): View {
            val v = constructor.newInstance(ctx, attributeSet)
            if (v is ViewGroup && !children.isNullOrEmpty()) {
                children.map {
                    LithoView.create(c, it)
                }.forEach {
                    v.addView(it)
                }
            }
            return v
        }
    }

    object NoOpViewBinder : SimpleViewBinder<View>()
}