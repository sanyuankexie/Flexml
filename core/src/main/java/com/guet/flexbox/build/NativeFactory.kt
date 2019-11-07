@file:Suppress("DEPRECATION")

package com.guet.flexbox.build

import android.content.Context
import android.graphics.Outline
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.ViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor

internal object NativeFactory : WidgetFactory<ViewCompatComponent.Builder<View>>() {


    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): ViewCompatComponent.Builder<View> {
        if (attrs != null) {
            val type = c.tryGetValue(attrs["type"], String::class.java, "")
            if (type.isNotEmpty()) {
                val view = ViewTypeCache[type]
                val radius = c.tryGetValue(attrs["borderRadius"], Float::class.java, 0f)
                val va = ViewAdapter(visibility, radius)
                return ViewCompatComponent.get(view, type)
                        .create(c.componentContext)
                        .viewBinder(va)
            }
        }
        throw IllegalArgumentException("can not found View type")
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

    private class ViewAdapter(
            private val visibility: Int,
            private val radius: Float
    ) : ViewOutlineProvider(), ViewBinder<View> {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }

        override fun prepare() {}

        override fun bind(view: View) {
            view.visibility = visibility
            if (radius > 0) {
                view.outlineProvider = this
            }
        }

        override fun unbind(view: View) {
            view.visibility
            view.outlineProvider = BACKGROUND
        }

    }

}