package com.guet.flexbox.databinding

import android.content.Context
import androidx.annotation.WorkerThread
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.Visibility
import com.guet.flexbox.el.PropsELContext

object DataBindingUtils {

    @WorkerThread
    @JvmStatic
    fun bind(c: Context, layoutNode: LayoutNode, data: Any?): RenderNode {
        return bind(c, layoutNode, PropsELContext(data), true)!!
    }

    internal fun bind(
            c: Context,
            layoutNode: LayoutNode,
            data: PropsELContext,
            upperVisibility: Boolean
    ): RenderNode? {
        val type = layoutNode.type
        var visibility = true
        val declaration = declarations[type]
        if (declaration != null) {
            val values = if (layoutNode.attrs != null) {
                val map = HashMap<String, Any>(layoutNode.attrs.size)
                for ((key, raw) in layoutNode.attrs) {
                    val result = declaration[key]?.cast(c, data, raw)
                    if (result != null) {
                        if (key == "visibility") {
                            when (result) {
                                Visibility.INVISIBLE -> {
                                    visibility = false
                                }
                                Visibility.GONE -> {
                                    return null
                                }
                            }
                        } else {
                            map[key] = result
                        }
                    }
                }
                map
            } else {
                emptyMap<String, Any>()
            }
            val selfVisibility = visibility && upperVisibility
            val children = checkList(layoutNode.children) {
                declaration.transform(c, values, data, it, selfVisibility)
            }
            return RenderNode(
                    type,
                    values,
                    selfVisibility,
                    children
            )
        } else {
            return RenderNode(
                    type,
                    emptyMap(),
                    true,
                    checkList(layoutNode.children) {
                        Common.transform(c, emptyMap(), data, it, true)
                    }
            )
        }
    }

    private inline fun checkList(
            list: List<LayoutNode>?,
            action: (List<LayoutNode>) -> List<RenderNode>
    ): List<RenderNode> {
        return if (list.isNullOrEmpty()) {
            emptyList()
        } else {
            action(list)
        }
    }

    private val declarations: Map<String, Declaration> = mapOf(
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