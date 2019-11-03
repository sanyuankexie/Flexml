@file:Suppress("DEPRECATION")

package com.guet.flexbox.build

import android.content.Context
import android.graphics.Outline
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.facebook.litho.ViewCompatComponent
import com.facebook.litho.viewcompat.SimpleViewBinder
import com.facebook.litho.viewcompat.ViewCreator
import java.lang.reflect.Constructor

internal object NativeFactory : WidgetFactory<ViewCompatComponent.Builder<View>>() {

    private val views = ViewTypeCache()

    override fun create(c: BuildContext, attrs: Map<String, String>)
            : ViewCompatComponent.Builder<View> {
        var type = attrs["type"]
        if (type != null) {
            type = c.tryGetValue(type, String::class.java, "")
            val radius = c.tryGetValue(attrs["borderRadius"], Float::class.java, 0f)
            if (type.isNotEmpty()) {
                val view = views[type]
                return ViewCompatComponent.get(ViewProvider(view, radius), type)
                        .create(c.componentContext)
                        .viewBinder(SimpleViewBinderSingleton)
            }
        }
        throw IllegalArgumentException("$type is not as 'View' type")
    }

    private class ViewTypeCache : LruCache<String, Constructor<*>>(Int.MAX_VALUE) {
        override fun create(key: String): Constructor<*> {
            val viewType = Class.forName(key)
            if (View::class.java.isAssignableFrom(viewType)) {
                return viewType.getConstructor(Context::class.java)
            } else {
                throw IllegalArgumentException("$key is not as 'View' type")
            }
        }
    }

    private class ViewProvider(val constructor: Constructor<*>, val radius: Float)
        : ViewOutlineProvider(),
            ViewCreator<View> {
        override fun createView(c: Context, parent: ViewGroup?): View {
            return (constructor.newInstance(c) as View).apply {
                outlineProvider = this@ViewProvider
            }
        }

        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }

    private object SimpleViewBinderSingleton : SimpleViewBinder<View>()
}