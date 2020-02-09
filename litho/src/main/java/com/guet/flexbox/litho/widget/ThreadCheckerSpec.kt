package com.guet.flexbox.litho.widget

import android.os.Looper
import android.util.Log
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop

@LayoutSpec
object ThreadCheckerSpec {

    private val mainThread = Looper.getMainLooper()

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @Prop component: Component): Component {
        if (Looper.myLooper() == mainThread) {
            Log.e(ThreadChecker::class.java.simpleName,
                    "Flexbox layout in main thread"
            )
        }
        return component
    }
}