package com.guet.flexbox.litho

import android.content.Context
import android.util.ArrayMap
import androidx.annotation.WorkerThread
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.ForwardContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.*
import com.guet.flexbox.build.Common
import com.guet.flexbox.el.PropsELContext

object LithoBuildUtils : BuildUtils() {

    @JvmStatic
    @JvmOverloads
    @WorkerThread
    fun preload(
            c: Context,
            templateNode: TemplateNode,
            data: Any? = null
    ): Page {
        val componentContext = ComponentContext(c)
        val eventBridge = ForwardContext()
        val elContext = PropsELContext(data)
        val com = build(
                templateNode,
                eventBridge,
                elContext,
                true,
                componentContext
        ).single() as Component
        return Page(templateNode, com, eventBridge)
    }

    private val myWidgets by lazy {
        val arr = arrayOf(
                "Empty" to (Empty to ToEmpty),
                "Flex" to (Flex to ToFlex),
                "Banner" to (Banner to ToBanner),
                "Image" to (Image to ToImage),
                "Scroller" to (Scroller to ToScroller),
                "TextInput" to (TextInput to ToTextInput),
                "Text" to (Text to ToText),
                "Stack" to (Common to ToStack),
                "for" to (For to null),
                "foreach" to (ForEach to null),
                "when" to (When to null),
                "if" to (If to null)
        )
        arr.toMap(ArrayMap<String, ToWidget>(arr.size))
    }

    override val widgets: Map<String, ToWidget> = myWidgets
}