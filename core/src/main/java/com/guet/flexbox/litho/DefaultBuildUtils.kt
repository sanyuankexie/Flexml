package com.guet.flexbox.litho

import android.content.Context
import androidx.annotation.WorkerThread
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.EventBridge
import com.guet.flexbox.FakePageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.*
import com.guet.flexbox.build.Common
import com.guet.flexbox.el.PropsELContext

object DefaultBuildUtils : BuildUtils() {

    @JvmStatic
    @JvmOverloads
    @WorkerThread
    fun preload(
            c: Context,
            templateNode: TemplateNode,
            data: Any? = null
    ): Page {
        val componentContext = ComponentContext(c)
        val eventBridge = EventBridge()
        val proxy = FakePageContext(eventBridge)
        val elContext = PropsELContext(data)
        val com = bindNode(
                templateNode,
                proxy,
                elContext,
                true,
                componentContext
        ).single()
        return Page(com as Component, eventBridge)
    }

    override val bindingMap: Map<String, Pair<Declaration, Binding?>> by lazy {
        mapOf(
                "Empty" to (Empty to ToEmpty),
                "Flex" to (Flex to ToFlex),
                "Image" to (Image to ToImage),
                "Mount" to (Mount to ToMount),
                "Scroller" to (Scroller to ToScroller),
                "TextInput" to (TextInput to ToTextInput),
                "Text" to (Text to ToText),
                "Stack" to (Common to ToStack),
                "for" to (For to null),
                "foreach" to (ForEach to null),
                "when" to (When to null),
                "if" to (If to null)
        )
    }
}