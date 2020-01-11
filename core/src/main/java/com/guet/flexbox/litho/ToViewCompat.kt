package com.guet.flexbox.litho

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.ComponentContext
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.SimpleViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import com.guet.flexbox.build.ViewCompat
import java.lang.reflect.Constructor

internal class ToViewCompat(
        viewType: Class<out View>
) : ToComponent<ViewCompatComponent.Builder<View>>(Common) {

    private val name = viewType.name

    private val constructor: Constructor<*> = viewType.getConstructor(
            Context::class.java,
            android.util.AttributeSet::class.java
    )

    override val attributeSet: AttributeSet<ViewCompatComponent.Builder<View>>
        get() = emptyMap()

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: Map<String, Any>
    ): ViewCompatComponent.Builder<View> {
        val attributeSet = ViewCompat.obtainAttributes(c.androidContext, attrs)
        val creator = ReflectViewCreator(
                constructor,
                attributeSet
        )
        return ViewCompatComponent.get(creator, name)
                .create(c)
                .viewBinder(NoOpViewBinder)
    }

    private class ReflectViewCreator(
            private val constructor: Constructor<*>,
            private val attributeSet: android.util.AttributeSet
    ) : ViewCreator<View> {
        override fun createView(c: Context, parent: ViewGroup?): View {
            return (constructor.newInstance(c, attributeSet) as View)
        }
    }

    companion object NoOpViewBinder : SimpleViewBinder<View>()
}