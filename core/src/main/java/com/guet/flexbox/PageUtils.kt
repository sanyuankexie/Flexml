package com.guet.flexbox

import android.content.Context
import androidx.annotation.WorkerThread
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.*
import com.guet.flexbox.databinding.*
import com.guet.flexbox.el.PropsELContext

object PageUtils {

    @JvmOverloads
    @WorkerThread
    @JvmStatic
    fun preload(
            c: Context,
            templateNode: TemplateNode,
            data: Any? = null
    ): PreloadPage {
        val componentContext = ComponentContext(c)
        val dispatcher = ExposedPageContext()
        val elContext = PropsELContext(data, FakePageContext(dispatcher))
        val com = bindNode(
                componentContext,
                templateNode,
                elContext
        ).single()
        return PreloadPage(templateNode, com, data, dispatcher)
    }

    internal fun bindNode(
            c: ComponentContext,
            templateNode: TemplateNode,
            data: PropsELContext,
            upperVisibility: Boolean = true
    ): List<Component> {
        val type = templateNode.type
        val pair = declarations[type] ?: Common to null
        val values = templateNode.attrs?.let {
            HashMap<String, Any>(it.size).apply {
                for ((key, raw) in templateNode.attrs) {
                    val result = pair.first[key]?.cast(c.androidContext, data, raw)
                    if (result != null) {
                        this[key] = result
                    }
                }
            }
        } ?: emptyMap<String, Any>()
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

    private val declarations: Map<String, Pair<Declaration, ComponentAdapt<*>?>> = mapOf(
            "Empty" to (Empty to EmptyAdapt),
            "Flex" to (Flex to FlexAdapt),
            "Image" to (Image to ImageAdapt),
            "Native" to (Native to NativeAdapt),
            "Scroller" to (Scroller to ScrollerAdapt),
            "TextInput" to (AbstractText to TextInputAdapt),
            "Text" to (Text to TextAdapt),
            "Stack" to (Common to StackAdapt),
            "for" to (For to null),
            "foreach" to (ForEach to null),
            "if" to (If to null)
    )
}