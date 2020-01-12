package com.guet.flexbox.litho

import android.view.View
import com.facebook.litho.ComponentContext
import com.facebook.litho.ViewCompat
import com.guet.flexbox.build.AttributeSet

internal class ToViewCompat(
        private val viewType: Class<out View>
) : ToComponent<ViewCompat.Builder>(Common) {

    override val attributeAssignSet: AttributeAssignSet<ViewCompat.Builder>
        get() = emptyMap()

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): ViewCompat.Builder {
        return ViewCompat.create(
                c,
                viewType,
                attrs.toAndroidAttributeSet(c.androidContext)
        )
    }

    override fun onInstallChildren(
            owner: ViewCompat.Builder,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        owner.children(children)
    }
}