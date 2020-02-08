package com.guet.flexbox.litho.widget

import android.util.Log
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop

@LayoutSpec
object RootSpec {

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @Prop component: Component): Component {
        Log.d("Root", "Current layout thread = " + Thread.currentThread())
        return component
    }
}