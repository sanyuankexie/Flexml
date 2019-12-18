package com.guet.flexbox.databinding

import android.content.Context
import androidx.annotation.WorkerThread
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
import com.guet.flexbox.data.Visibility
import com.guet.flexbox.el.PropsELContext

object DataBindingUtils {

    @WorkerThread
    @JvmStatic
    fun bind(c: Context, nodeInfo: NodeInfo, data: Any?): LockedInfo {
        return bind(c, nodeInfo, PropsELContext(data), true)!!
    }

    internal fun bind(
            c: Context,
            nodeInfo: NodeInfo,
            data: PropsELContext,
            upperVisibility: Boolean
    ): LockedInfo? {
        val type = nodeInfo.type
        var visibility = true
        val declaration = declarations[type]
        if (declaration != null) {
            val values = if (nodeInfo.attrs != null) {
                val map = HashMap<String, Any>(nodeInfo.attrs.size)
                for ((key, raw) in nodeInfo.attrs) {
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
            val children = checkList(nodeInfo.children) {
                declaration.transform(c, values, data, it, selfVisibility)
            }
            return LockedInfo(
                    type,
                    values,
                    selfVisibility,
                    children
            )
        } else {
            return LockedInfo(
                    type,
                    emptyMap(),
                    true,
                    checkList(nodeInfo.children) {
                        Common.transform(c, emptyMap(), data, it, true)
                    }
            )
        }
    }

    private inline fun checkList(
            list: List<NodeInfo>?,
            action: (List<NodeInfo>) -> List<LockedInfo>
    ): List<LockedInfo> {
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
            "Text" to Text,
            "TextInput" to TextInput,
            "for" to For,
            "foreach" to ForEach,
            "if" to If
    )
}