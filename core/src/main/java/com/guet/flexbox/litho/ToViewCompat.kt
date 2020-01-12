package com.guet.flexbox.litho

import android.content.Context
import android.view.View
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.ViewCompat
import java.lang.reflect.Constructor
import com.guet.flexbox.litho.widget.ViewCompat as LithoViewCompat

internal class ToViewCompat(
        viewType: Class<out View>
) : ToComponent<LithoViewCompat.Builder>(Common) {

    private val name = viewType.name

    private val constructor: Constructor<out View> = viewType.getConstructor(
            Context::class.java,
            android.util.AttributeSet::class.java
    )

    override val attributeSet: AttributeSet<LithoViewCompat.Builder>
        get() = emptyMap()


    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: Map<String, Any>
    ): LithoViewCompat.Builder {
        val attributeSet = ViewCompat.obtainAttributes(
                c.androidContext,
                attrs
        )
        @Suppress("UNCHECKED_CAST")
        return LithoViewCompat.create(c)
                .attrs(attributeSet)
                .name(name)
                .constructor(constructor as Constructor<View>)
    }

    override fun onInstallChildren(
            owner: LithoViewCompat.Builder,
            visibility: Boolean,
            attrs: Map<String, Any>,
            children: List<Component>
    ) {
        owner.children(children)
    }
}