@file:Suppress("DEPRECATION")

package com.guet.flexbox.build

import android.content.Context
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.ViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor

internal object NativeFactory : WidgetFactory<ViewCompatComponent.Builder<View>>() {

    init {
        text("visibility") { display, _ ->
            if (display) {
                viewBinder(displayValues[0])
            } else {
                viewBinder(displayValues[1])
            }
        }
    }

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>
    ): ViewCompatComponent.Builder<View> {
        val type = c.tryGetValue(attrs["type"], String::class.java, "")
        if (type.isNotEmpty()) {
            val view = ViewTypeCache[type]
            return ViewCompatComponent.get(view, type)
                    .create(c.componentContext)
        } else {
            throw IllegalArgumentException("$type is not as 'View' type")
        }
    }

    private object ViewTypeCache : LruCache<String, ReflectViewCreator>(Int.MAX_VALUE) {
        override fun create(key: String): ReflectViewCreator {
            val viewType = Class.forName(key)
            if (View::class.java.isAssignableFrom(viewType)) {
                return ReflectViewCreator(viewType.getConstructor(Context::class.java))
            } else {
                throw IllegalArgumentException("$key is not as 'View' type")
            }
        }
    }

    private class ReflectViewCreator(val constructor: Constructor<*>) : ViewCreator<View> {
        override fun createView(c: Context, parent: ViewGroup?): View {
            return (constructor.newInstance(c) as View)
        }
    }

    private class DisplayBinder(private val display: Boolean)
        : ViewBinder<View> {

        override fun prepare() {}

        override fun bind(view: View) {
            if (display) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.INVISIBLE
            }
        }

        override fun unbind(view: View) {}

    }

    private val displayValues = arrayOf(
            DisplayBinder(true),
            DisplayBinder(false)
    )

}