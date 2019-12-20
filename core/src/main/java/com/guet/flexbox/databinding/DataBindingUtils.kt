package com.guet.flexbox.databinding

import android.content.Context
import androidx.annotation.WorkerThread
import com.guet.flexbox.content.DynamicNode
import com.guet.flexbox.content.PageContext
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.el.PropsELContext

object DataBindingUtils {

    @WorkerThread
    @JvmOverloads
    @JvmStatic
    fun bind(
            c: Context,
            dynamicNode: DynamicNode,
            data: Any?,
            extension: (Map<String, Any>)? = null
    ): RenderContent {
        val pageContext = PageContext()
        val renderNode = bindNode(
                c,
                dynamicNode,
                PropsELContext(data, pageContext, extension)
        ).single()
        return RenderContent(pageContext, renderNode)
    }

    internal fun bindNode(
            c: Context,
            dynamicNode: DynamicNode,
            data: PropsELContext,
            upperVisibility: Boolean = true
    ): List<RenderNode> {
        val type = dynamicNode.type
        val declaration = declarations[type] ?: Common
        val values = dynamicNode.attrs?.let {
            HashMap<String, Any>(it.size).apply {
                for ((key, raw) in dynamicNode.attrs) {
                    val result = declaration[key]?.cast(c, data, raw)
                    if (result != null) {
                        this[key] = result
                    }
                }
            }
        } ?: emptyMap<String, Any>()
        return declaration.transform(
                c,
                type,
                values,
                data,
                dynamicNode.children ?: emptyList(),
                upperVisibility
        )
    }

    private val declarations: Map<String, Declaration> = mapOf(
            "Empty" to Empty,
            "Flex" to Flex,
            "Image" to Image,
            "Scroller" to Scroller,
            "TextInput" to AbstractText,
            "Text" to Text,
            "for" to For,
            "foreach" to ForEach,
            "if" to If
    )
}