package com.guet.flexbox.databinding

import android.content.Context
import androidx.annotation.WorkerThread
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.el.PropsELContext

object DataBindingUtils {

    @WorkerThread
    @JvmOverloads
    @JvmStatic
    fun bind(
            c: Context,
            layoutNode: LayoutNode,
            data: Any?,
            extra: (Map<String, Any>) = emptyMap()
    ): RenderNode {
        val props = PropsELContext(data)
        return props.scope(extra) {
            bindNode(c, layoutNode, props, true).single()
        }
    }

    internal fun bindNode(
            c: Context,
            layoutNode: LayoutNode,
            data: PropsELContext,
            upperVisibility: Boolean
    ): List<RenderNode> {
        val type = layoutNode.type
        val declaration = declarations[type] ?: Common
        val values = layoutNode.attrs?.let {
            HashMap<String, Any>(it.size).apply {
                for ((key, raw) in layoutNode.attrs) {
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
                layoutNode.children ?: emptyList(),
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