package com.guet.flexbox.databinding

import android.content.Context
import androidx.annotation.WorkerThread
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.ExposedPageContext
import com.guet.flexbox.FakePageContext
import com.guet.flexbox.Page
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.*
import com.guet.flexbox.el.PropsELContext

object Toolkit {

    @JvmOverloads
    @WorkerThread
    @JvmStatic
    fun preload(
            c: Context,
            templateNode: TemplateNode,
            data: Any? = null
    ): Page {
        val componentContext = ComponentContext(c)
        val dispatcher = ExposedPageContext()
        val elContext = PropsELContext(data, FakePageContext(dispatcher))
        val com = bindNode(
                componentContext,
                templateNode,
                elContext
        ).single()
        return Page(com, dispatcher)
    }

    internal fun bindAttr(
            declaration: Declaration,
            attrs: Map<String, String>,
            data: PropsELContext
    ): Map<String, Any> {
        return attrs.let {
            HashMap<String, Any>(it.size).apply {
                for ((key, raw) in attrs) {
                    val result = declaration[key]?.cast(data, raw)
                    if (result != null) {
                        this[key] = result
                    }
                }
            }
        }
    }

    internal fun bindNode(
            c: ComponentContext,
            templateNode: TemplateNode,
            data: PropsELContext,
            upperVisibility: Boolean = true
    ): List<Component> {
        val type = templateNode.type
        val pair = map[type] ?: Common to null
        val values = templateNode.attrs?.let {
            bindAttr(pair.first, it, data)
        } ?: emptyMap()
        val children = templateNode.children ?: emptyList()
        return pair.first.transform(
                c,
                pair.second,
                type,
                values,
                data,
                children,
                upperVisibility
        )
    }

    private val map: Map<String, Pair<Declaration, ToComponent<*>?>> = mapOf(
            "Empty" to (Empty to ToEmpty),
            "Flex" to (Flex to ToFlex),
            "Image" to (Image to ToImage),
            "Mount" to (Mount to ToMount),
            "Scroller" to (Scroller to ToScroller),
            "TextInput" to (AbstractText to ToTextInput),
            "Text" to (Text to ToText),
            "Stack" to (Common to ToStack),
            "for" to (For to null),
            "foreach" to (ForEach to null),
            "when" to (When to null)
    )
}