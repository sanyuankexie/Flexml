package com.guet.flexbox.litho.widget

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
        val view = constructor.newInstance(c.androidContext, attrs)
        if (view is ViewGroup && !children.isNullOrEmpty()) {
            children.map {
                LithoView.create(c, it)
            }.forEach {
                view.addView(it)
            }
        }
        return ViewCompatComponent.get(
                { _, _ ->
                    view
                },
                name
        ).create(c).viewBinder(NoOpViewBinder)
                .build()
    }

    object NoOpViewBinder : SimpleViewBinder<View>()
}