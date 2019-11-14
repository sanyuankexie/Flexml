@file:Suppress("DEPRECATION")

package com.guet.flexbox.build

import android.content.Context
import android.graphics.Outline
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.facebook.litho.ComponentContext
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.ViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor


internal object NativeFactory : WidgetFactory<ViewCompatComponent.Builder<View>>() {

    override fun onCreateWidget(
            c: ComponentContext,
            dataBinding: DataBinding,
            attrs: Map<String, String>?,
            visibility: Int
    ): ViewCompatComponent.Builder<View> {
        if (attrs != null) {
            val type = dataBinding.tryGetValue(attrs["type"], "")
            if (type.isNotEmpty()) {
                val view = ViewTypeCache[type]
                val radius = dataBinding.tryGetValue(attrs["borderRadius"], 0f)
                val va = ViewAdapter(visibility, radius)
                return ViewCompatComponent.get(view, type)
                        .create(c)
                        .viewBinder(va)
            }
        }
        throw IllegalArgumentException("can not found View type")
    }

    internal object ViewTypeCache : LruCache<String, ReflectViewCreator>(32) {
        override fun create(key: String): ReflectViewCreator {
            val viewType = Class.forName(key)
            if (View::class.java.isAssignableFrom(viewType)) {
                return ReflectViewCreator(viewType.getConstructor(Context::class.java))
            } else {
                throw IllegalArgumentException("$key is not as 'View' type")
            }
        }
    }

    internal class ReflectViewCreator(private val constructor: Constructor<*>) : ViewCreator<View> {
        override fun createView(c: Context, parent: ViewGroup?): View {
            return (constructor.newInstance(c) as View)
        }
    }

    internal class ViewAdapter(
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
            view.visibility = View.GONE
            view.outlineProvider = BACKGROUND
        }
    }
}