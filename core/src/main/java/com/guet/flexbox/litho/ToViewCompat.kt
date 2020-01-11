package com.guet.flexbox.litho

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.ComponentContext
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.ViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor

internal object ToViewCompat : ToComponent<ViewCompatComponent.Builder<View>>(Common) {

    private val viewTypeCache = HashMap<Class<*>, ReflectViewCreator>(32)

    override val attributeSet: AttributeSet<ViewCompatComponent.Builder<View>>
        get() = emptyMap()

    override fun create(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): ViewCompatComponent.Builder<View> {
        val typeClass = attrs["type"] as Class<*>
        val creator = viewTypeCache.getOrPut(typeClass) {
            ReflectViewCreator(typeClass.getConstructor(Context::class.java))
        }
        return ViewCompatComponent.get(creator, typeClass.simpleName)
                .create(c)
                .apply {
                    viewBinder(if (attrs["visibility"] == View.VISIBLE) {
                        Visibility.VISIBLE
                    } else {
                        Visibility.GONE
                    })
                }
    }

    private class ReflectViewCreator(private val constructor: Constructor<*>) : ViewCreator<View> {
        override fun createView(c: Context, parent: ViewGroup?): View {
            return (constructor.newInstance(c) as View)
        }
    }

    private enum class Visibility(val visibility: Int) : ViewBinder<View> {

        VISIBLE(View.VISIBLE),
        GONE(View.GONE);

        override fun prepare() {
        }

        override fun bind(view: View) {
            view.visibility = visibility
        }

        override fun unbind(view: View) {
            view.visibility = View.GONE
        }
    }
}