package com.guet.flexbox.build

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.ComponentContext
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.ViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import com.guet.flexbox.content.RenderNode
import java.lang.reflect.Constructor

internal object Native : Widget<ViewCompatComponent.Builder<View>>(Common) {

    private val viewTypeCache = HashMap<Class<*>, ReflectViewCreator>(32)

    override val attributeSet: AttributeSet<ViewCompatComponent.Builder<View>>
        get() = emptyMap()

    override fun onCreate(c: ComponentContext, renderNode: RenderNode): ViewCompatComponent.Builder<View> {
        val type = Class.forName(renderNode.attrs.getValue("type") as String)
        if (!View::class.java.isAssignableFrom(type)) {
            throw IllegalStateException("$type is not as 'View' type")
        }
        val creator = viewTypeCache.getOrPut(type) {
            ReflectViewCreator(type.getConstructor(Context::class.java))
        }
        return ViewCompatComponent.get(creator, type.simpleName)
                .create(c)
                .apply {
                    viewBinder(if (renderNode.attrs["visibility"] == View.VISIBLE) {
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